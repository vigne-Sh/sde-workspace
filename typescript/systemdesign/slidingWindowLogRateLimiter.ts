/*
Sliding window log rate limiter: stores a per-client log (queue) of exact
request timestamps and evicts any that fall outside the trailing window
before deciding whether a new request is allowed - the precise variant of
sliding-window limiting, as opposed to bucketed approximations.

status - completed
*/

import { logp } from "../utils/logger";

class SlidingWindowLogRateLimiter {
  private readonly requestLogByClient: Map<string, number[]> = new Map();

  constructor(private readonly maxRequests: number, private readonly windowMs: number) {}

  private evictOutsideWindow(log: number[], nowMs: number): void {
    const cutoff = nowMs - this.windowMs;
    while (log.length > 0 && log[0] <= cutoff) {
      log.shift();
    }
  }

  isAllowed(clientId: string, nowMs: number = Date.now()): boolean {
    let log = this.requestLogByClient.get(clientId);
    if (!log) {
      log = [];
      this.requestLogByClient.set(clientId, log);
    }
    this.evictOutsideWindow(log, nowMs);

    if (log.length >= this.maxRequests) {
      return false;
    }
    log.push(nowMs);
    return true;
  }

  requestCountInWindow(clientId: string, nowMs: number = Date.now()): number {
    const log = this.requestLogByClient.get(clientId);
    if (!log) return 0;
    this.evictOutsideWindow(log, nowMs);
    return log.length;
  }
}

// usage scenarios

const limiter = new SlidingWindowLogRateLimiter(5, 1000);
const client = "client-42";

const results: boolean[] = [];
for (let i = 0; i < 5; i++) {
  results.push(limiter.isAllowed(client, i * 50));
}
logp(`5 requests within window (t=0..200ms): ${results.join(", ")}`);

const sixthAt250 = limiter.isAllowed(client, 250);
logp(`6th request at t=250ms, still within window -> ${sixthAt250} (expected false, limit hit)`);

logp(`request count still counted in window: ${limiter.requestCountInWindow(client, 250)}`);

const afterWindowSlides = limiter.isAllowed(client, 1100);
logp(`request at t=1100ms, first request (t=0) now outside window -> ${afterWindowSlides} (expected true)`);
logp(`request count in window at t=1100ms: ${limiter.requestCountInWindow(client, 1100)}`);
