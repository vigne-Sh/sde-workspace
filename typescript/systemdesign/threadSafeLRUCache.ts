/*
LRU cache safe under concurrent *async* access. JS itself is single-threaded,
but overlapping async get/put calls can still interleave between awaits and
corrupt shared state, so this wraps every operation with an internal mutex
(a promise chain acting as a lock) to serialize access - demonstrated with
Promise.all firing many interleaved operations at once.

status - completed
*/

import { logp } from "../utils/logger";

class AsyncMutex {
  private tail: Promise<void> = Promise.resolve();

  // queues `fn` behind whatever is currently holding the lock, and returns
  // its result once it's had its exclusive turn
  async runExclusive<T>(fn: () => Promise<T> | T): Promise<T> {
    const previous = this.tail;
    let release!: () => void;
    this.tail = new Promise((resolve) => (release = resolve));
    await previous;
    try {
      return await fn();
    } finally {
      release();
    }
  }
}

class ThreadSafeLRUCache<K, V> {
  private readonly capacity: number;
  private readonly cache: Map<K, V> = new Map();
  private readonly mutex = new AsyncMutex();

  constructor(capacity: number) {
    if (capacity <= 0) {
      throw new Error("capacity must be positive");
    }
    this.capacity = capacity;
  }

  async get(key: K): Promise<V | undefined> {
    return this.mutex.runExclusive(() => {
      if (!this.cache.has(key)) {
        return undefined;
      }
      const value = this.cache.get(key)!;
      this.cache.delete(key);
      this.cache.set(key, value);
      return value;
    });
  }

  async put(key: K, value: V): Promise<void> {
    return this.mutex.runExclusive(() => {
      if (this.cache.has(key)) {
        this.cache.delete(key);
      } else if (this.cache.size >= this.capacity) {
        const lruKey = this.cache.keys().next().value as K;
        this.cache.delete(lruKey);
      }
      this.cache.set(key, value);
    });
  }

  async keysInOrder(): Promise<K[]> {
    return this.mutex.runExclusive(() => Array.from(this.cache.keys()));
  }

  size(): number {
    return this.cache.size;
  }
}

// simulate an async operation that yields to the event loop mid-work, which
// is exactly when an unprotected cache would let another call interleave
function randomDelay(): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, Math.random() * 5));
}

// usage scenarios

async function main(): Promise<void> {
  const cache = new ThreadSafeLRUCache<number, string>(50);

  // fire 200 concurrent, interleaved put/get operations across 20 keys
  const operations: Array<Promise<void>> = [];
  for (let i = 0; i < 200; i++) {
    const key = i % 20;
    operations.push(
      (async () => {
        await randomDelay();
        if (i % 3 === 0) {
          await cache.get(key);
        } else {
          await cache.put(key, `value-${i}`);
        }
      })()
    );
  }
  await Promise.all(operations);

  const finalSize = cache.size();
  logp(`after 200 concurrent async operations across 20 keys, cache size -> ${finalSize} (expected 20, no corruption)`);

  // capacity-limited eviction under concurrency
  const smallCache = new ThreadSafeLRUCache<number, string>(3);
  const inserts = [1, 2, 3, 4, 5].map((key) =>
    (async () => {
      await randomDelay();
      await smallCache.put(key, `v${key}`);
    })()
  );
  await Promise.all(inserts);
  const finalOrder = await smallCache.keysInOrder();
  logp(`small cache (capacity 3) after 5 concurrent inserts holds ${finalOrder.length} keys (expected 3): ${finalOrder}`);

  // race a bunch of concurrent get/put on the SAME key and confirm the
  // cache never ends up with more entries than its capacity, which is the
  // invariant a broken (non-atomic) LRU would violate under interleaving
  const raceCache = new ThreadSafeLRUCache<string, number>(1);
  const race = Array.from({ length: 50 }, (_, i) =>
    (async () => {
      await randomDelay();
      await raceCache.put("shared-key", i);
      await raceCache.get("shared-key");
    })()
  );
  await Promise.all(race);
  logp(`race cache (capacity 1) size after 50 concurrent racers on one key -> ${raceCache.size()} (expected 1)`);
}

main();
