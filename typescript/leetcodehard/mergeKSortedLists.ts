/*
https://leetcode.com/problems/merge-k-sorted-lists/

status - completed
*/

import { logp } from "../utils/logger";

class ListNode {
  val: number;
  next: ListNode | null;

  constructor(val: number, next: ListNode | null = null) {
    this.val = val;
    this.next = next;
  }
}

class MinHeap {
  private items: ListNode[] = [];

  get size(): number {
    return this.items.length;
  }

  push(node: ListNode): void {
    this.items.push(node);
    let i = this.items.length - 1;
    while (i > 0) {
      const parent = Math.floor((i - 1) / 2);
      if (this.items[parent].val <= this.items[i].val) break;
      [this.items[parent], this.items[i]] = [this.items[i], this.items[parent]];
      i = parent;
    }
  }

  pop(): ListNode | undefined {
    if (this.items.length === 0) return undefined;
    const top = this.items[0];
    const last = this.items.pop()!;
    if (this.items.length > 0) {
      this.items[0] = last;
      let i = 0;
      while (true) {
        const left = 2 * i + 1;
        const right = 2 * i + 2;
        let smallest = i;
        if (left < this.items.length && this.items[left].val < this.items[smallest].val) {
          smallest = left;
        }
        if (right < this.items.length && this.items[right].val < this.items[smallest].val) {
          smallest = right;
        }
        if (smallest === i) break;
        [this.items[smallest], this.items[i]] = [this.items[i], this.items[smallest]];
        i = smallest;
      }
    }
    return top;
  }
}

function mergeKLists(lists: Array<ListNode | null>): ListNode | null {
  const heap = new MinHeap();
  for (const node of lists) {
    if (node) heap.push(node);
  }

  const dummy = new ListNode(0);
  let tail = dummy;

  while (heap.size > 0) {
    const smallest = heap.pop()!;
    tail.next = smallest;
    tail = smallest;
    if (smallest.next) heap.push(smallest.next);
  }

  return dummy.next;
}

function fromArray(values: number[]): ListNode | null {
  let head: ListNode | null = null;
  for (let i = values.length - 1; i >= 0; i--) {
    head = new ListNode(values[i], head);
  }
  return head;
}

function toArray(node: ListNode | null): number[] {
  const result: number[] = [];
  while (node) {
    result.push(node.val);
    node = node.next;
  }
  return result;
}

const lists1 = [fromArray([1, 4, 5]), fromArray([1, 3, 4]), fromArray([2, 6])];
logp(
  `input [[1,4,5],[1,3,4],[2,6]] expected [1,1,2,3,4,4,5,6] actual ${JSON.stringify(
    toArray(mergeKLists(lists1)),
  )}`,
);

const lists2: Array<ListNode | null> = [];
logp(`input [] expected [] actual ${JSON.stringify(toArray(mergeKLists(lists2)))}`);
