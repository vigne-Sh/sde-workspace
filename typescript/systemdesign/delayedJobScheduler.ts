/*
Delayed job scheduler: jobs are enqueued with an execution timestamp and
run in that order via a hand-rolled binary min-heap keyed by execution
time, so the next-due job is always found/removed in O(log n).

status - completed
*/

import { logp } from "../utils/logger";

interface ScheduledJob {
  id: number;
  runAtMs: number;
  name: string;
}

class MinHeap<T> {
  private readonly items: T[] = [];

  constructor(private readonly compare: (a: T, b: T) => number) {}

  get size(): number {
    return this.items.length;
  }

  peek(): T | undefined {
    return this.items[0];
  }

  push(item: T): void {
    this.items.push(item);
    this.bubbleUp(this.items.length - 1);
  }

  pop(): T | undefined {
    if (this.items.length === 0) {
      return undefined;
    }
    const top = this.items[0];
    const last = this.items.pop()!;
    if (this.items.length > 0) {
      this.items[0] = last;
      this.bubbleDown(0);
    }
    return top;
  }

  private bubbleUp(index: number): void {
    let i = index;
    while (i > 0) {
      const parent = (i - 1) >> 1;
      if (this.compare(this.items[i], this.items[parent]) >= 0) {
        break;
      }
      [this.items[i], this.items[parent]] = [this.items[parent], this.items[i]];
      i = parent;
    }
  }

  private bubbleDown(index: number): void {
    let i = index;
    const n = this.items.length;
    while (true) {
      const left = 2 * i + 1;
      const right = 2 * i + 2;
      let smallest = i;
      if (left < n && this.compare(this.items[left], this.items[smallest]) < 0) {
        smallest = left;
      }
      if (right < n && this.compare(this.items[right], this.items[smallest]) < 0) {
        smallest = right;
      }
      if (smallest === i) {
        break;
      }
      [this.items[i], this.items[smallest]] = [this.items[smallest], this.items[i]];
      i = smallest;
    }
  }
}

class DelayedJobScheduler {
  private readonly heap = new MinHeap<ScheduledJob>((a, b) => a.runAtMs - b.runAtMs);
  private nextId = 1;

  schedule(name: string, runAtMs: number): number {
    const id = this.nextId++;
    this.heap.push({ id, runAtMs, name });
    return id;
  }

  // runs every job whose runAtMs has arrived by nowMs, earliest first
  runDueJobs(nowMs: number): ScheduledJob[] {
    const executed: ScheduledJob[] = [];
    while (this.heap.size > 0 && this.heap.peek()!.runAtMs <= nowMs) {
      executed.push(this.heap.pop()!);
    }
    return executed;
  }

  pendingCount(): number {
    return this.heap.size;
  }
}

// usage scenarios

const scheduler = new DelayedJobScheduler();
scheduler.schedule("send-welcome-email", 300);
scheduler.schedule("expire-session", 100);
scheduler.schedule("send-reminder", 200);
scheduler.schedule("cleanup-temp-files", 500);

logp(`pending jobs before any run: ${scheduler.pendingCount()}`);

const dueAt250 = scheduler.runDueJobs(250);
logp(`jobs due by t=250: ${dueAt250.map((j) => `${j.name}@${j.runAtMs}`).join(", ")}`);
logp("expect expire-session(100) then send-reminder(200), in that time order");

logp(`pending jobs remaining: ${scheduler.pendingCount()}`);

const dueAt1000 = scheduler.runDueJobs(1000);
logp(`jobs due by t=1000: ${dueAt1000.map((j) => `${j.name}@${j.runAtMs}`).join(", ")}`);
logp(`pending jobs remaining: ${scheduler.pendingCount()}`);
