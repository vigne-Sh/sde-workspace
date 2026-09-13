/*
Circuit breaker state machine (closed/open/half-open) wrapping a "remote
call" that can fail: trips open after a failure threshold, waits a cooldown,
then allows a limited trial in half-open before fully closing or re-opening.

status - completed
*/

import { logp } from "../utils/logger";

enum CircuitState {
  CLOSED = "CLOSED",
  OPEN = "OPEN",
  HALF_OPEN = "HALF_OPEN",
}

class CircuitOpenError extends Error {
  constructor() {
    super("circuit is open, call rejected");
  }
}

class CircuitBreaker {
  private state: CircuitState = CircuitState.CLOSED;
  private consecutiveFailures = 0;
  private openedAtMs = 0;

  constructor(
    private readonly failureThreshold: number,
    private readonly cooldownMs: number,
    private readonly nowFn: () => number = Date.now
  ) {}

  getState(): CircuitState {
    if (this.state === CircuitState.OPEN && this.nowFn() - this.openedAtMs >= this.cooldownMs) {
      this.state = CircuitState.HALF_OPEN;
    }
    return this.state;
  }

  call<T>(action: () => T): T {
    const currentState = this.getState();

    if (currentState === CircuitState.OPEN) {
      throw new CircuitOpenError();
    }

    try {
      const result = action();
      this.onSuccess();
      return result;
    } catch (err) {
      this.onFailure();
      throw err;
    }
  }

  private onSuccess(): void {
    this.consecutiveFailures = 0;
    this.state = CircuitState.CLOSED;
  }

  private onFailure(): void {
    this.consecutiveFailures++;
    if (this.state === CircuitState.HALF_OPEN || this.consecutiveFailures >= this.failureThreshold) {
      this.state = CircuitState.OPEN;
      this.openedAtMs = this.nowFn();
    }
  }
}

// usage scenarios: a flaky remote call plus a fake clock

let simulatedNow = 0;
let shouldFail = true;
const flakyRemoteCall = () => {
  if (shouldFail) {
    throw new Error("remote service unavailable");
  }
  return "ok";
};

const breaker = new CircuitBreaker(3, 1000, () => simulatedNow);

for (let i = 1; i <= 3; i++) {
  try {
    breaker.call(flakyRemoteCall);
  } catch (err) {
    logp(`call ${i} failed (${(err as Error).message}), state=${breaker.getState()}`);
  }
}

try {
  breaker.call(flakyRemoteCall);
} catch (err) {
  logp(`call 4 rejected immediately: ${(err as Error).message}, state=${breaker.getState()}`);
}

logp("advancing simulated clock past cooldown");
simulatedNow += 1000;
logp(`state after cooldown -> ${breaker.getState()} (expected HALF_OPEN)`);

shouldFail = false;
const result = breaker.call(flakyRemoteCall);
logp(`trial call in half-open succeeded -> "${result}", state=${breaker.getState()} (expected CLOSED)`);
