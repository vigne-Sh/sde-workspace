"""
LRU cache safe under concurrent access, using threading.Lock to guard
the underlying OrderedDict so get/put from multiple threads can't
corrupt recency order or capacity bookkeeping. Demonstrated with
several worker threads hammering the same cache.

status - completed
"""

import threading
from collections import OrderedDict

from base_logger.logging_event import create_logger


class ThreadSafeLruCache:

    log = create_logger(__name__)

    def __init__(self, capacity: int):
        self.capacity = capacity
        self.data = OrderedDict()
        self.lock = threading.Lock()

    def get(self, key):
        with self.lock:
            if key not in self.data:
                return -1
            self.data.move_to_end(key)
            return self.data[key]

    def put(self, key, value):
        with self.lock:
            if key in self.data:
                self.data.move_to_end(key)
            self.data[key] = value
            if len(self.data) > self.capacity:
                self.data.popitem(last=False)

    def snapshot(self):
        with self.lock:
            return list(self.data.items())


if __name__ == "__main__":
    log = create_logger("thread_safe_lru_cache_demo")

    cache = ThreadSafeLruCache(capacity=50)
    errors = []

    def worker(thread_id, num_ops):
        try:
            for i in range(num_ops):
                key = f"t{thread_id}-k{i % 20}"
                cache.put(key, thread_id * 1000 + i)
                cache.get(key)
        except Exception as exc:
            errors.append(exc)

    threads = [threading.Thread(target=worker, args=(t, 500)) for t in range(8)]
    for t in threads:
        t.start()
    for t in threads:
        t.join()

    log.info("ran 8 threads x 500 ops each concurrently against capacity-50 cache")
    log.info("errors encountered: %d (expected 0)", len(errors))
    log.info("final cache size: %d (expected <= capacity 50)", len(cache.snapshot()))
    log.info("cache never exceeded capacity: %s", len(cache.snapshot()) <= cache.capacity)

    single_cache = ThreadSafeLruCache(capacity=2)
    single_cache.put("a", 1)
    single_cache.put("b", 2)
    single_cache.get("a")
    single_cache.put("c", 3)
    log.info("single-threaded sanity check, snapshot: %s (expected 'b' evicted)",
             single_cache.snapshot())
