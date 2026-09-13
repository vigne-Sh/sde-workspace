"""
LRU cache with O(1) get/put using a dict of nodes plus a hand-rolled
doubly linked list to track recency order. Classic system design /
interview exercise.

status - completed
"""

from base_logger.logging_event import create_logger


class Node:

    def __init__(self, key, value):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None


class LruCache:

    log = create_logger(__name__)

    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = {}
        self.head = Node(None, None)
        self.tail = Node(None, None)
        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node):
        node.prev.next = node.next
        node.next.prev = node.prev

    def _add_to_front(self, node):
        node.next = self.head.next
        node.prev = self.head
        self.head.next.prev = node
        self.head.next = node

    def get(self, key):
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._remove(node)
        self._add_to_front(node)
        return node.value

    def put(self, key, value):
        if key in self.cache:
            node = self.cache[key]
            node.value = value
            self._remove(node)
            self._add_to_front(node)
            return

        if len(self.cache) >= self.capacity:
            lru_node = self.tail.prev
            self._remove(lru_node)
            del self.cache[lru_node.key]

        node = Node(key, value)
        self.cache[key] = node
        self._add_to_front(node)

    def keys_in_order(self):
        order = []
        node = self.head.next
        while node is not self.tail:
            order.append(node.key)
            node = node.next
        return order


if __name__ == "__main__":
    log = create_logger("lru_cache_demo")

    cache = LruCache(capacity=2)
    cache.put(1, "a")
    cache.put(2, "b")
    log.info("order after inserting 1,2: %s", cache.keys_in_order())

    log.info("get(1) -> %s", cache.get(1))
    log.info("order after touching 1: %s", cache.keys_in_order())

    cache.put(3, "c")
    log.info("put(3) triggers eviction of least recently used key 2")
    log.info("order after inserting 3: %s", cache.keys_in_order())
    log.info("get(2) -> %s (expected -1, evicted)", cache.get(2))
    log.info("get(3) -> %s", cache.get(3))
