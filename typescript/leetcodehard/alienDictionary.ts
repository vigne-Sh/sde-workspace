/*
https://leetcode.com/problems/alien-dictionary/

status - completed
*/

import { logp } from "../utils/logger";

function alienOrder(words: string[]): string {
  const graph = new Map<string, Set<string>>();
  const indegree = new Map<string, number>();

  for (const word of words) {
    for (const ch of word) {
      if (!graph.has(ch)) {
        graph.set(ch, new Set());
        indegree.set(ch, 0);
      }
    }
  }

  for (let i = 0; i < words.length - 1; i++) {
    const first = words[i];
    const second = words[i + 1];
    const minLen = Math.min(first.length, second.length);

    if (first.length > second.length && first.slice(0, minLen) === second.slice(0, minLen)) {
      return "";
    }

    for (let j = 0; j < minLen; j++) {
      if (first[j] !== second[j]) {
        const edges = graph.get(first[j])!;
        if (!edges.has(second[j])) {
          edges.add(second[j]);
          indegree.set(second[j], (indegree.get(second[j]) ?? 0) + 1);
        }
        break;
      }
    }
  }

  const queue: string[] = [];
  for (const [ch, deg] of indegree) {
    if (deg === 0) queue.push(ch);
  }

  const order: string[] = [];
  while (queue.length > 0) {
    const ch = queue.shift()!;
    order.push(ch);
    for (const next of graph.get(ch)!) {
      indegree.set(next, indegree.get(next)! - 1);
      if (indegree.get(next) === 0) queue.push(next);
    }
  }

  return order.length === indegree.size ? order.join("") : "";
}

logp(`input [wrt,wrf,er,ett,rftt] expected wertf actual ${alienOrder(["wrt", "wrf", "er", "ett", "rftt"])}`);
logp(`input [z,x] expected zx actual ${alienOrder(["z", "x"])}`);
logp(`input [z,x,z] expected "" actual "${alienOrder(["z", "x", "z"])}"`);
