"""
Bloom filter: probabilistic set membership backed by a bit array and
multiple independent hash functions (via double hashing). Membership
checks can false-positive but never false-negative, and the filter
uses far less memory than storing the actual set.

status - completed
"""

import hashlib

from base_logger.logging_event import create_logger


class BloomFilter:

    log = create_logger(__name__)

    def __init__(self, size: int = 1000, num_hashes: int = 5):
        self.size = size
        self.num_hashes = num_hashes
        self.bits = [0] * size

    def _hashes(self, item: str):
        item_bytes = item.encode("utf-8")
        h1 = int(hashlib.md5(item_bytes).hexdigest(), 16)
        h2 = int(hashlib.sha1(item_bytes).hexdigest(), 16)
        for i in range(self.num_hashes):
            yield (h1 + i * h2) % self.size

    def add(self, item: str):
        for index in self._hashes(item):
            self.bits[index] = 1

    def might_contain(self, item: str) -> bool:
        return all(self.bits[index] == 1 for index in self._hashes(item))


if __name__ == "__main__":
    log = create_logger("bloom_filter_demo")

    bloom = BloomFilter(size=2000, num_hashes=5)

    added = [f"user-{i}@example.com" for i in range(200)]
    for email in added:
        bloom.add(email)

    all_added_found = all(bloom.might_contain(email) for email in added)
    log.info("all 200 added items report present: %s (expected True, no false negatives)",
             all_added_found)

    not_added = [f"stranger-{i}@example.com" for i in range(500)]
    false_positives = sum(1 for email in not_added if bloom.might_contain(email))
    log.info("false positives among 500 never-added items: %d / %d (%.1f%%)",
             false_positives, len(not_added), 100.0 * false_positives / len(not_added))
    log.info("expected a small but nonzero false positive rate, never a false negative")
