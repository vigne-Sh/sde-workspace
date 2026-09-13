/*
In-memory key-value store where entries expire after a TTL. Expiry is lazy
(checked on read) plus an active purge sweep is available to reclaim memory
from keys nobody has touched since expiring.

status - completed
*/

import { logp } from "../utils/logger";

interface Entry<V> {
  value: V;
  expiresAtMs: number | null;
}

class InMemoryKVStoreWithTTL<V> {
  private readonly store: Map<string, Entry<V>> = new Map();

  constructor(private readonly nowFn: () => number = Date.now) {}

  private isExpired(entry: Entry<V>, nowMs: number): boolean {
    return entry.expiresAtMs !== null && entry.expiresAtMs <= nowMs;
  }

  set(key: string, value: V, ttlMs?: number): void {
    const nowMs = this.nowFn();
    const expiresAtMs = ttlMs !== undefined ? nowMs + ttlMs : null;
    this.store.set(key, { value, expiresAtMs });
  }

  get(key: string): V | undefined {
    const entry = this.store.get(key);
    if (!entry) {
      return undefined;
    }
    if (this.isExpired(entry, this.nowFn())) {
      this.store.delete(key);
      return undefined;
    }
    return entry.value;
  }

  has(key: string): boolean {
    return this.get(key) !== undefined;
  }

  delete(key: string): boolean {
    return this.store.delete(key);
  }

  // active purge: sweeps all entries and evicts anything already expired,
  // without waiting for a read to trigger lazy expiry
  purgeExpired(): number {
    const nowMs = this.nowFn();
    let purged = 0;
    for (const [key, entry] of this.store) {
      if (this.isExpired(entry, nowMs)) {
        this.store.delete(key);
        purged++;
      }
    }
    return purged;
  }

  size(): number {
    return this.store.size;
  }
}

// usage scenarios: fake clock for deterministic timing

let simulatedNow = 0;
const kv = new InMemoryKVStoreWithTTL<string>(() => simulatedNow);

kv.set("permanent", "stays-forever");
kv.set("short-lived", "gone-soon", 100);

logp(`get(permanent) -> ${kv.get("permanent")}`);
logp(`get(short-lived) at t=0 -> ${kv.get("short-lived")}`);

simulatedNow = 150;
logp(`get(short-lived) at t=150 (lazy expiry) -> ${kv.get("short-lived")} (expected undefined, expired)`);
logp(`store size after lazy expiry removed it -> ${kv.size()}`);

kv.set("also-expires", "x", 50);
kv.set("keeps-going", "y", 10_000);
simulatedNow = 300;
const purgedCount = kv.purgeExpired();
logp(`active purge at t=300 removed ${purgedCount} expired entr${purgedCount === 1 ? "y" : "ies"}`);
logp(`get(keeps-going) -> ${kv.get("keeps-going")}`);
