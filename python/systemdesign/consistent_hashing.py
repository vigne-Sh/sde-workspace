"""
Consistent hashing ring with virtual nodes, used to shard keys across
servers so that adding or removing a node only remaps a small fraction
of keys instead of the whole keyspace.

status - completed
"""

import bisect
import hashlib

from base_logger.logging_event import create_logger


class ConsistentHashRing:

    log = create_logger(__name__)

    def __init__(self, virtual_nodes_per_node: int = 100):
        self.virtual_nodes_per_node = virtual_nodes_per_node
        self.ring = {}
        self.sorted_hashes = []

    def _hash(self, value: str) -> int:
        return int(hashlib.md5(value.encode("utf-8")).hexdigest(), 16)

    def add_node(self, node: str):
        for i in range(self.virtual_nodes_per_node):
            vnode_hash = self._hash(f"{node}#{i}")
            self.ring[vnode_hash] = node
            bisect.insort(self.sorted_hashes, vnode_hash)

    def remove_node(self, node: str):
        for i in range(self.virtual_nodes_per_node):
            vnode_hash = self._hash(f"{node}#{i}")
            if vnode_hash in self.ring:
                del self.ring[vnode_hash]
                idx = bisect.bisect_left(self.sorted_hashes, vnode_hash)
                self.sorted_hashes.pop(idx)

    def get_node(self, key: str) -> str:
        if not self.sorted_hashes:
            return None
        key_hash = self._hash(key)
        idx = bisect.bisect(self.sorted_hashes, key_hash)
        if idx == len(self.sorted_hashes):
            idx = 0
        return self.ring[self.sorted_hashes[idx]]


if __name__ == "__main__":
    log = create_logger("consistent_hashing_demo")

    ring = ConsistentHashRing(virtual_nodes_per_node=100)
    for node in ["server-a", "server-b", "server-c"]:
        ring.add_node(node)

    keys = [f"key-{i}" for i in range(1000)]
    before = {key: ring.get_node(key) for key in keys}

    counts_before = {}
    for owner in before.values():
        counts_before[owner] = counts_before.get(owner, 0) + 1
    log.info("distribution across 3 nodes: %s", counts_before)

    ring.add_node("server-d")
    after = {key: ring.get_node(key) for key in keys}

    moved = sum(1 for key in keys if before[key] != after[key])
    log.info("keys remapped after adding server-d: %d / %d (%.1f%%)",
             moved, len(keys), 100.0 * moved / len(keys))
    log.info("expected roughly 1/4 of keys moved (new node takes its fair share)")

    ring.remove_node("server-b")
    after_removal = {key: ring.get_node(key) for key in keys}
    moved_on_removal = sum(1 for key in keys if after[key] != after_removal[key])
    still_on_b = sum(1 for key in keys if after[key] == "server-b")
    log.info("keys remapped after removing server-b: %d / %d, all %d of server-b's keys moved",
             moved_on_removal, len(keys), still_on_b)
