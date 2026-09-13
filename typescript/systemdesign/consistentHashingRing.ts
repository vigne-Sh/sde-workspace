/*
Consistent hashing ring with virtual nodes: shards keys across servers so
that adding or removing a server only remaps a small, proportional slice of
keys instead of the whole keyspace. Uses FNV-1a plus a murmur-style avalanche
finalizer for the hash, since a weak/linear hash clusters sequential keys and
would make the remapping demo misleading.

status - completed
*/

import { logp } from "../utils/logger";

function fnv1a(value: string): number {
  let hash = 0x811c9dc5;
  for (let i = 0; i < value.length; i++) {
    hash ^= value.charCodeAt(i);
    hash = Math.imul(hash, 0x01000193);
  }
  return hash >>> 0;
}

// finalizer borrowed from murmur3's avalanche mix, spreads bits so nearby
// inputs (like "key-1", "key-2", ...) land far apart on the ring
function avalanche(h: number): number {
  h ^= h >>> 16;
  h = Math.imul(h, 0x85ebca6b);
  h ^= h >>> 13;
  h = Math.imul(h, 0xc2b2ae35);
  h ^= h >>> 16;
  return h >>> 0;
}

function hashString(value: string): number {
  return avalanche(fnv1a(value));
}

class ConsistentHashRing {
  private readonly ring: Map<number, string> = new Map();
  private sortedHashes: number[] = [];

  constructor(private readonly virtualNodesPerNode: number = 100) {}

  private insertSorted(hash: number): void {
    let lo = 0;
    let hi = this.sortedHashes.length;
    while (lo < hi) {
      const mid = (lo + hi) >>> 1;
      if (this.sortedHashes[mid] < hash) {
        lo = mid + 1;
      } else {
        hi = mid;
      }
    }
    this.sortedHashes.splice(lo, 0, hash);
  }

  addNode(node: string): void {
    for (let i = 0; i < this.virtualNodesPerNode; i++) {
      const vnodeHash = hashString(`${node}#${i}`);
      this.ring.set(vnodeHash, node);
      this.insertSorted(vnodeHash);
    }
  }

  removeNode(node: string): void {
    for (let i = 0; i < this.virtualNodesPerNode; i++) {
      const vnodeHash = hashString(`${node}#${i}`);
      if (this.ring.has(vnodeHash)) {
        this.ring.delete(vnodeHash);
        const idx = this.sortedHashes.indexOf(vnodeHash);
        if (idx !== -1) {
          this.sortedHashes.splice(idx, 1);
        }
      }
    }
  }

  getNode(key: string): string | undefined {
    if (this.sortedHashes.length === 0) {
      return undefined;
    }
    const keyHash = hashString(key);
    let lo = 0;
    let hi = this.sortedHashes.length;
    while (lo < hi) {
      const mid = (lo + hi) >>> 1;
      if (this.sortedHashes[mid] <= keyHash) {
        lo = mid + 1;
      } else {
        hi = mid;
      }
    }
    const idx = lo === this.sortedHashes.length ? 0 : lo;
    return this.ring.get(this.sortedHashes[idx]);
  }
}

// usage scenarios

const ring = new ConsistentHashRing(100);
for (const node of ["server-a", "server-b", "server-c"]) {
  ring.addNode(node);
}

const keys = Array.from({ length: 1000 }, (_, i) => `key-${i}`);
const before = new Map(keys.map((key) => [key, ring.getNode(key)!]));

const countsBefore: Record<string, number> = {};
for (const owner of before.values()) {
  countsBefore[owner] = (countsBefore[owner] ?? 0) + 1;
}
logp(`distribution across 3 nodes: ${JSON.stringify(countsBefore)}`);

ring.addNode("server-d");
const after = new Map(keys.map((key) => [key, ring.getNode(key)!]));

const moved = keys.filter((key) => before.get(key) !== after.get(key)).length;
logp(`keys remapped after adding server-d: ${moved} / ${keys.length} (${((100 * moved) / keys.length).toFixed(1)}%)`);
logp("expected roughly 1/4 of keys moved (new node takes its fair share)");

ring.removeNode("server-b");
const afterRemoval = new Map(keys.map((key) => [key, ring.getNode(key)!]));
const movedOnRemoval = keys.filter((key) => after.get(key) !== afterRemoval.get(key)).length;
const stillOnB = keys.filter((key) => after.get(key) === "server-b").length;
logp(
  `keys remapped after removing server-b: ${movedOnRemoval} / ${keys.length}, all ${stillOnB} of server-b's keys moved`
);
