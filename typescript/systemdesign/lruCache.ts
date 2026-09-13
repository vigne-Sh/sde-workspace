/*
In-memory LRU cache with O(1) get/put using a Map (insertion order = recency
order, so touching a key means delete+re-set it to move it to the end).
Common system design interview exercise.

status - completed
*/

import { logp } from "../utils/logger";

class LRUCache<K, V> {
  private readonly capacity: number;
  private readonly cache: Map<K, V> = new Map();

  constructor(capacity: number) {
    if (capacity <= 0) {
      throw new Error("capacity must be positive");
    }
    this.capacity = capacity;
  }

  get(key: K): V | undefined {
    if (!this.cache.has(key)) {
      return undefined;
    }
    const value = this.cache.get(key)!;
    // re-insert to mark as most recently used
    this.cache.delete(key);
    this.cache.set(key, value);
    return value;
  }

  put(key: K, value: V): void {
    if (this.cache.has(key)) {
      this.cache.delete(key);
    } else if (this.cache.size >= this.capacity) {
      // Map preserves insertion order, so the first key is the least recently used
      const lruKey = this.cache.keys().next().value as K;
      this.cache.delete(lruKey);
    }
    this.cache.set(key, value);
  }

  keysInOrder(): K[] {
    return Array.from(this.cache.keys());
  }
}

// usage scenarios

const cache = new LRUCache<number, string>(2);
cache.put(1, "a");
cache.put(2, "b");
logp(`order after inserting 1,2: ${cache.keysInOrder()}`);

logp(`get(1) -> ${cache.get(1)}`);
logp(`order after touching 1: ${cache.keysInOrder()}`);

cache.put(3, "c");
logp("put(3) triggers eviction of least recently used key 2");
logp(`order after inserting 3: ${cache.keysInOrder()}`);
logp(`get(2) -> ${cache.get(2)} (expected undefined, evicted)`);
logp(`get(3) -> ${cache.get(3)}`);
