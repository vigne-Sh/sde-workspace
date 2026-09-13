"""
LFU (least frequently used) cache with O(1)-ish get/put. Keeps a
frequency count per key and buckets keys by frequency using ordered
dicts, so both the minimum frequency bucket and per-bucket recency
order are tracked without scanning the whole cache.

status - completed
"""

from collections import OrderedDict, defaultdict

from base_logger.logging_event import create_logger


class LfuCache:

    log = create_logger(__name__)

    def __init__(self, capacity: int):
        self.capacity = capacity
        self.values = {}
        self.freq = {}
        self.buckets = defaultdict(OrderedDict)
        self.min_freq = 0

    def _touch(self, key):
        f = self.freq[key]
        del self.buckets[f][key]
        if not self.buckets[f]:
            del self.buckets[f]
            if self.min_freq == f:
                self.min_freq += 1
        self.freq[key] = f + 1
        self.buckets[f + 1][key] = True

    def get(self, key):
        if key not in self.values:
            return -1
        self._touch(key)
        return self.values[key]

    def put(self, key, value):
        if self.capacity <= 0:
            return

        if key in self.values:
            self.values[key] = value
            self._touch(key)
            return

        if len(self.values) >= self.capacity:
            evict_key, _ = self.buckets[self.min_freq].popitem(last=False)
            if not self.buckets[self.min_freq]:
                del self.buckets[self.min_freq]
            del self.values[evict_key]
            del self.freq[evict_key]

        self.values[key] = value
        self.freq[key] = 1
        self.buckets[1][key] = True
        self.min_freq = 1


if __name__ == "__main__":
    log = create_logger("lfu_cache_demo")

    cache = LfuCache(capacity=2)
    cache.put(1, "a")
    cache.put(2, "b")
    log.info("get(1) -> %s (freq(1)=2, freq(2)=1)", cache.get(1))

    cache.put(3, "c")
    log.info("put(3) evicts key 2 (lowest frequency)")
    log.info("get(2) -> %s (expected -1, evicted)", cache.get(2))
    log.info("get(1) -> %s", cache.get(1))
    log.info("get(3) -> %s", cache.get(3))

    cache.put(4, "d")
    log.info("put(4): freq(3)=1 vs freq(1)=high, so 3 is evicted")
    log.info("get(3) -> %s (expected -1, evicted)", cache.get(3))
    log.info("get(1) -> %s", cache.get(1))
    log.info("get(4) -> %s", cache.get(4))
