/*
Trie-based prefix autocomplete: inserts words into a trie, tracks a
frequency/weight per word, and returns the top-K matches for a given prefix
ranked by weight then lexicographically.

status - completed
*/

import { logp } from "../utils/logger";

class TrieNode {
  readonly children: Map<string, TrieNode> = new Map();
  isWord = false;
  weight = 0;
}

class TrieAutocomplete {
  private readonly root = new TrieNode();

  insert(word: string, weight: number = 1): void {
    let node = this.root;
    for (const ch of word) {
      let next = node.children.get(ch);
      if (!next) {
        next = new TrieNode();
        node.children.set(ch, next);
      }
      node = next;
    }
    node.isWord = true;
    node.weight += weight;
  }

  private collectWords(node: TrieNode, prefix: string, out: Array<{ word: string; weight: number }>): void {
    if (node.isWord) {
      out.push({ word: prefix, weight: node.weight });
    }
    for (const [ch, child] of node.children) {
      this.collectWords(child, prefix + ch, out);
    }
  }

  topMatches(prefix: string, limit: number = 5): string[] {
    let node = this.root;
    for (const ch of prefix) {
      const next = node.children.get(ch);
      if (!next) {
        return [];
      }
      node = next;
    }

    const matches: Array<{ word: string; weight: number }> = [];
    this.collectWords(node, prefix, matches);

    matches.sort((a, b) => b.weight - a.weight || a.word.localeCompare(b.word));
    return matches.slice(0, limit).map((m) => m.word);
  }
}

// usage scenarios

const trie = new TrieAutocomplete();
const seed: Array<[string, number]> = [
  ["car", 10],
  ["care", 5],
  ["career", 8],
  ["cart", 3],
  ["cared", 2],
  ["dog", 4],
];
for (const [word, weight] of seed) {
  trie.insert(word, weight);
}

logp(`autocomplete("car") -> ${JSON.stringify(trie.topMatches("car", 3))}`);
logp(`autocomplete("care") -> ${JSON.stringify(trie.topMatches("care", 3))}`);
logp(`autocomplete("d") -> ${JSON.stringify(trie.topMatches("d"))}`);
logp(`autocomplete("zzz") -> ${JSON.stringify(trie.topMatches("zzz"))} (expected empty, no such prefix)`);
