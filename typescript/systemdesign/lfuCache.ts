/*
LFU cache with O(1)-ish get/put: tracks per-key frequency counts and buckets
keys by frequency (each bucket ordered by recency via Map) so eviction always
picks the least-frequently-used key, breaking ties by least-recently-used.

status - completed
*/

import { logp } from "../utils/logger";

class LFUCache<K, V> {
  private readonly capacity: number;
  private readonly values: Map<K, V> = new Map();
  private readonly freqOf: Map<K, number> = new Map();
  private readonly bucketsByFreq: Map<number, Map<K, true>> = new Map();
  private minFreq = 0;

  constructor(capacity: number) {
    if (capacity <= 0) {
      throw new Error("capacity must be positive");
    }
    this.capacity = capacity;
  }

  private touch(key: K): void {
    const freq = this.freqOf.get(key)!;
    const bucket = this.bucketsByFreq.get(freq)!;
    bucket.delete(key);
    if (bucket.size === 0) {
      this.bucketsByFreq.delete(freq);
      if (this.minFreq === freq) {
        this.minFreq = freq + 1;
      }
    }
    const newFreq = freq + 1;
    this.freqOf.set(key, newFreq);
    if (!this.bucketsByFreq.has(newFreq)) {
      this.bucketsByFreq.set(newFreq, new Map());
    }
    this.bucketsByFreq.get(newFreq)!.set(key, true);
  }

  get(key: K): V | undefined {
    if (!this.values.has(key)) {
      return undefined;
    }
    this.touch(key);
    return this.values.get(key);
  }

  put(key: K, value: V): void {
    if (this.capacity === 0) {
      return;
    }
    if (this.values.has(key)) {
      this.values.set(key, value);
      this.touch(key);
      return;
    }

    if (this.values.size >= this.capacity) {
      const lowestBucket = this.bucketsByFreq.get(this.minFreq)!;
      const evictKey = lowestBucket.keys().next().value as K;
      lowestBucket.delete(evictKey);
      if (lowestBucket.size === 0) {
        this.bucketsByFreq.delete(this.minFreq);
      }
      this.values.delete(evictKey);
      this.freqOf.delete(evictKey);
    }

    this.values.set(key, value);
    this.freqOf.set(key, 1);
    if (!this.bucketsByFreq.has(1)) {
      this.bucketsByFreq.set(1, new Map());
    }
    this.bucketsByFreq.get(1)!.set(key, true);
    this.minFreq = 1;
  }
}

// usage scenarios

const cache = new LFUCache<number, string>(2);
cache.put(1, "a");
cache.put(2, "b");
logp(`get(1) -> ${cache.get(1)} (freq(1)=2, freq(2)=1)`);

cache.put(3, "c");
logp("put(3) evicts key 2, the least frequently used");
logp(`get(2) -> ${cache.get(2)} (expected undefined, evicted)`);
logp(`get(1) -> ${cache.get(1)}`);
logp(`get(3) -> ${cache.get(3)}`);

cache.put(4, "d");
logp("put(4): key 1 (freq 3) and key 3 (freq 2) tie is broken - key 3 is LFU, evicted");
logp(`get(3) -> ${cache.get(3)} (expected undefined, evicted)`);
logp(`get(1) -> ${cache.get(1)}`);
logp(`get(4) -> ${cache.get(4)}`);
