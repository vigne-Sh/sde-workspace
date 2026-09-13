/*
https://leetcode.com/problems/word-search-ii/

status - completed
*/

import { logp } from "../utils/logger";

interface TrieNode {
  children: Map<string, TrieNode>;
  word: string | null;
}

function createNode(): TrieNode {
  return { children: new Map(), word: null };
}

function findWords(board: string[][], words: string[]): string[] {
  const root = createNode();

  for (const word of words) {
    let node = root;
    for (const ch of word) {
      if (!node.children.has(ch)) {
        node.children.set(ch, createNode());
      }
      node = node.children.get(ch)!;
    }
    node.word = word;
  }

  const rows = board.length;
  const cols = board[0].length;
  const result: string[] = [];

  function backtrack(row: number, col: number, node: TrieNode): void {
    if (row < 0 || row >= rows || col < 0 || col >= cols) return;

    const ch = board[row][col];
    if (ch === "#" || !node.children.has(ch)) return;

    const next = node.children.get(ch)!;
    if (next.word) {
      result.push(next.word);
      next.word = null;
    }

    board[row][col] = "#";
    backtrack(row + 1, col, next);
    backtrack(row - 1, col, next);
    backtrack(row, col + 1, next);
    backtrack(row, col - 1, next);
    board[row][col] = ch;
  }

  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
      backtrack(row, col, root);
    }
  }

  return result;
}

const board1 = [
  ["o", "a", "a", "n"],
  ["e", "t", "a", "e"],
  ["i", "h", "k", "r"],
  ["i", "f", "l", "v"],
];
logp(
  `input words=[oath,pea,eat,rain] expected [oath,eat] (order may vary) actual ${JSON.stringify(
    findWords(board1, ["oath", "pea", "eat", "rain"]).sort(),
  )}`,
);

const board2 = [
  ["a", "b"],
  ["c", "d"],
];
logp(`input words=[abcb] expected [] actual ${JSON.stringify(findWords(board2, ["abcb"]))}`);
