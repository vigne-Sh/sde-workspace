/*
https://leetcode.com/problems/word-ladder/

status - completed
*/

import { logp } from "../utils/logger";

function ladderLength(beginWord: string, endWord: string, wordList: string[]): number {
  const words = new Set(wordList);
  if (!words.has(endWord)) return 0;

  const queue: Array<[string, number]> = [[beginWord, 1]];
  const visited = new Set<string>([beginWord]);
  const alphabet = "abcdefghijklmnopqrstuvwxyz";

  while (queue.length > 0) {
    const [word, steps] = queue.shift()!;
    if (word === endWord) return steps;

    for (let i = 0; i < word.length; i++) {
      for (const ch of alphabet) {
        if (ch === word[i]) continue;
        const candidate = word.slice(0, i) + ch + word.slice(i + 1);
        if (words.has(candidate) && !visited.has(candidate)) {
          visited.add(candidate);
          queue.push([candidate, steps + 1]);
        }
      }
    }
  }

  return 0;
}

logp(
  `input hit->cog [hot,dot,dog,lot,log,cog] expected 5 actual ${ladderLength(
    "hit",
    "cog",
    ["hot", "dot", "dog", "lot", "log", "cog"],
  )}`,
);
logp(
  `input hit->cog [hot,dot,dog,lot,log] expected 0 actual ${ladderLength(
    "hit",
    "cog",
    ["hot", "dot", "dog", "lot", "log"],
  )}`,
);
