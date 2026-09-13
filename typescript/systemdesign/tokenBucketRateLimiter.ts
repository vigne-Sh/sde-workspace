/*
Token bucket rate limiter: a bucket holds up to `capacity` tokens, refills
continuously at `refillRatePerMs`, and each request consumes one token.
Requests are allowed only while tokens are available, giving smooth
average-rate limiting that still tolerates short bursts.

status - completed
*/

import { logp } from "../utils/logger";

class TokenBucketRateLimiter {
  private tokens: number;
  private lastRefillMs: number;

  constructor(
    private readonly capacity: number,
    private readonly refillRatePerMs: number,
    nowMs: number = Date.now()
  ) {
    this.tokens = capacity;
    this.lastRefillMs = nowMs;
  }

  private refill(nowMs: number): void {
    const elapsed = Math.max(0, nowMs - this.lastRefillMs);
    if (elapsed === 0) {
      return;
    }
    const refreshed = elapsed * this.refillRatePerMs;
    this.tokens = Math.min(this.capacity, this.tokens + refreshed);
    this.lastRefillMs = nowMs;
  }

  tryAcquire(nowMs: number = Date.now()): boolean {
    this.refill(nowMs);
    if (this.tokens >= 1) {
      this.tokens -= 1;
      return true;
    }
    return false;
  }

  availableTokens(): number {
    return Math.floor(this.tokens);
  }
}

// usage scenarios: simulate a fake clock so the demo is deterministic

let simulatedNow = 0;
// capacity 3, refills 1 token every 100ms
const limiter = new TokenBucketRateLimiter(3, 1 / 100, simulatedNow);

for (let i = 0; i < 5; i++) {
  const allowed = limiter.tryAcquire(simulatedNow);
  logp(`t=${simulatedNow}ms request ${i + 1} -> ${allowed ? "allowed" : "rejected"}`);
}

logp("advancing simulated clock by 250ms to let tokens refill");
simulatedNow += 250;
const allowedAfterWait = limiter.tryAcquire(simulatedNow);
logp(`t=${simulatedNow}ms request -> ${allowedAfterWait ? "allowed" : "rejected"} (tokens refilled)`);
