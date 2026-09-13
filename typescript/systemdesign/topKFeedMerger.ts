/*
Merges k user timelines, each already sorted most-recent-first, into a
single feed ordered by recency, using a min-of-k... actually max-of-k heap
over the current head of each timeline so the overall merge runs in
O(n log k) instead of concatenating and sorting everything.

status - completed
*/

import { logp } from "../utils/logger";

interface Post {
  authorId: string;
  postedAtMs: number;
  text: string;
}

interface HeapEntry {
  post: Post;
  timelineIndex: number;
  postIndex: number;
}

class MaxHeap<T> {
  private readonly items: T[] = [];

  constructor(private readonly compare: (a: T, b: T) => number) {}

  get size(): number {
    return this.items.length;
  }

  push(item: T): void {
    this.items.push(item);
    let i = this.items.length - 1;
    while (i > 0) {
      const parent = (i - 1) >> 1;
      if (this.compare(this.items[i], this.items[parent]) <= 0) break;
      [this.items[i], this.items[parent]] = [this.items[parent], this.items[i]];
      i = parent;
    }
  }

  pop(): T | undefined {
    if (this.items.length === 0) return undefined;
    const top = this.items[0];
    const last = this.items.pop()!;
    if (this.items.length > 0) {
      this.items[0] = last;
      let i = 0;
      const n = this.items.length;
      while (true) {
        const left = 2 * i + 1;
        const right = 2 * i + 2;
        let largest = i;
        if (left < n && this.compare(this.items[left], this.items[largest]) > 0) largest = left;
        if (right < n && this.compare(this.items[right], this.items[largest]) > 0) largest = right;
        if (largest === i) break;
        [this.items[i], this.items[largest]] = [this.items[largest], this.items[i]];
        i = largest;
      }
    }
    return top;
  }
}

// merges k already-sorted (most-recent-first) timelines into one feed
function mergeTimelines(timelines: Post[][], limit: number = Infinity): Post[] {
  const heap = new MaxHeap<HeapEntry>((a, b) => a.post.postedAtMs - b.post.postedAtMs);

  timelines.forEach((timeline, timelineIndex) => {
    if (timeline.length > 0) {
      heap.push({ post: timeline[0], timelineIndex, postIndex: 0 });
    }
  });

  const merged: Post[] = [];
  while (heap.size > 0 && merged.length < limit) {
    const entry = heap.pop()!;
    merged.push(entry.post);

    const nextIndex = entry.postIndex + 1;
    const timeline = timelines[entry.timelineIndex];
    if (nextIndex < timeline.length) {
      heap.push({ post: timeline[nextIndex], timelineIndex: entry.timelineIndex, postIndex: nextIndex });
    }
  }
  return merged;
}

// usage scenarios

const alice: Post[] = [
  { authorId: "alice", postedAtMs: 900, text: "alice post 3" },
  { authorId: "alice", postedAtMs: 500, text: "alice post 2" },
  { authorId: "alice", postedAtMs: 100, text: "alice post 1" },
];
const bob: Post[] = [
  { authorId: "bob", postedAtMs: 800, text: "bob post 2" },
  { authorId: "bob", postedAtMs: 200, text: "bob post 1" },
];
const carol: Post[] = [{ authorId: "carol", postedAtMs: 1000, text: "carol post 1" }];

const feed = mergeTimelines([alice, bob, carol]);
logp("merged feed, most recent first:");
feed.forEach((post) => logp(`  t=${post.postedAtMs} [${post.authorId}] ${post.text}`));

const timestamps = feed.map((p) => p.postedAtMs);
const isDescending = timestamps.every((t, i) => i === 0 || timestamps[i - 1] >= t);
logp(`feed is correctly sorted most-recent-first: ${isDescending}`);
logp(`total posts merged: ${feed.length} (expected 6)`);

const top3 = mergeTimelines([alice, bob, carol], 3);
logp(`top 3 most recent across all users: ${top3.map((p) => `${p.authorId}@${p.postedAtMs}`).join(", ")}`);
