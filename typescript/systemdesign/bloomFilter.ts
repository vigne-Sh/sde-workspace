/*
Bloom filter: probabilistic set membership over a fixed bit array using
multiple independent hash functions (derived via double hashing from two
base hashes). False positives are possible, false negatives never are.

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

function djb2(value: string): number {
  let hash = 5381;
  for (let i = 0; i < value.length; i++) {
    hash = (hash * 33) ^ value.charCodeAt(i);
  }
  return hash >>> 0;
}

class BloomFilter {
  private readonly bits: Uint8Array;
  private readonly sizeBits: number;

  constructor(sizeBits: number, private readonly numHashes: number = 4) {
    this.sizeBits = sizeBits;
    this.bits = new Uint8Array(sizeBits);
  }

  // double hashing: combine two base hashes to cheaply simulate `numHashes`
  // independent hash functions, per Kirsch-Mitzenmacher
  private indexesFor(value: string): number[] {
    const h1 = fnv1a(value);
    const h2 = djb2(value);
    const indexes: number[] = [];
    for (let i = 0; i < this.numHashes; i++) {
      const combined = (h1 + i * h2) >>> 0;
      indexes.push(combined % this.sizeBits);
    }
    return indexes;
  }

  add(value: string): void {
    for (const idx of this.indexesFor(value)) {
      this.bits[idx] = 1;
    }
  }

  mightContain(value: string): boolean {
    return this.indexesFor(value).every((idx) => this.bits[idx] === 1);
  }
}

// usage scenarios

const filter = new BloomFilter(1000, 4);
const added = ["alice", "bob", "carol", "dave"];
added.forEach((name) => filter.add(name));

for (const name of added) {
  logp(`mightContain("${name}") -> ${filter.mightContain(name)} (expected true, was added)`);
}

const notAdded = ["eve", "mallory", "trent"];
let falsePositives = 0;
for (const name of notAdded) {
  const result = filter.mightContain(name);
  if (result) falsePositives++;
  logp(`mightContain("${name}") -> ${result} (not added; true would be a false positive)`);
}
logp(`false positives among ${notAdded.length} unadded names: ${falsePositives}`);

// demonstrate false-positive rate stays low with a well-sized filter and a
// larger batch of never-added values
const largeFilter = new BloomFilter(10_000, 4);
for (let i = 0; i < 500; i++) {
  largeFilter.add(`present-${i}`);
}
let fpCount = 0;
const trials = 1000;
for (let i = 0; i < trials; i++) {
  if (largeFilter.mightContain(`absent-${i}`)) fpCount++;
}
logp(`false positive rate over ${trials} unadded keys: ${((100 * fpCount) / trials).toFixed(2)}% (expected low, well under 100%)`);
