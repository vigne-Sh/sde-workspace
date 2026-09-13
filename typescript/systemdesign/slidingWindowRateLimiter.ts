/*
Sliding window rate limiter: keeps a queue of request timestamps per client
and allows a new request only if fewer than `maxRequests` timestamps fall
within the trailing `windowMs` window, evicting stale timestamps as it goes.

status - completed
*/

import { logp } from "../utils/logger";

class SlidingWindowRateLimiter {
  private readonly timestampsByClient: Map<string, number[]> = new Map();

  constructor(private readonly maxRequests: number, private readonly windowMs: number) {}

  private evictStale(timestamps: number[], nowMs: number): void {
    const threshold = nowMs - this.windowMs;
    let removeCount = 0;
    while (removeCount < timestamps.length && timestamps[removeCount] <= threshold) {
      removeCount++;
    }
    if (removeCount > 0) {
      timestamps.splice(0, removeCount);
    }
  }

  allow(clientId: string, nowMs: number = Date.now()): boolean {
    let timestamps = this.timestampsByClient.get(clientId);
    if (!timestamps) {
      timestamps = [];
      this.timestampsByClient.set(clientId, timestamps);
    }
    this.evictStale(timestamps, nowMs);

    if (timestamps.length >= this.maxRequests) {
      return false;
    }
    timestamps.push(nowMs);
    return true;
  }
}

// usage scenarios

const limiter = new SlidingWindowRateLimiter(3, 1000);
const client = "client-1";

for (let i = 0; i < 4; i++) {
  const nowMs = i * 100;
  logp(`t=${nowMs}ms request ${i + 1} -> ${limiter.allow(client, nowMs) ? "allowed" : "rejected"}`);
}

logp("advancing past the 1000ms window so old requests slide out");
const laterNow = 1200;
logp(`t=${laterNow}ms request -> ${limiter.allow(client, laterNow) ? "allowed" : "rejected"} (window slid, room again)`);
