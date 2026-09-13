/*
Trie-based prefix autocomplete that returns the top matching words for a given
prefix, common system design / data structure interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dev.logger.basePrinter;

class TrieAutocomplete extends basePrinter{

    private static class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isWordEnd = false;
        int frequency = 0;
    }

    private final TrieNode root;

    TrieAutocomplete(){
        this.root = new TrieNode();
    }

    void insert(String word){
        insert(word, 1);
    }

    void insert(String word, int weight){
        TrieNode current = root;
        for (char c : word.toCharArray()){
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isWordEnd = true;
        current.frequency += weight;
    }

    private TrieNode findPrefixNode(String prefix){
        TrieNode current = root;
        for (char c : prefix.toCharArray()){
            current = current.children.get(c);
            if (current == null){
                return null;
            }
        }
        return current;
    }

    private void collectWords(TrieNode node, StringBuilder path, List<String[]> results){
        if (node.isWordEnd){
            results.add(new String[]{path.toString(), String.valueOf(node.frequency)});
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()){
            path.append(entry.getKey());
            collectWords(entry.getValue(), path, results);
            path.deleteCharAt(path.length() - 1);
        }
    }

    List<String> topMatches(String prefix, int topK){
        TrieNode prefixNode = findPrefixNode(prefix);
        List<String> output = new ArrayList<>();
        if (prefixNode == null){
            return output;
        }

        List<String[]> results = new ArrayList<>();
        collectWords(prefixNode, new StringBuilder(prefix), results);

        results.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        for (int i = 0; i < Math.min(topK, results.size()); i++){
            output.add(results.get(i)[0]);
        }
        return output;
    }

    public static void main(String[] args) {
        TrieAutocomplete trie = new TrieAutocomplete();
        trie.insert("car", 10);
        trie.insert("cart", 5);
        trie.insert("care", 8);
        trie.insert("careful", 2);
        trie.insert("cat", 20);
        trie.insert("dog", 15);

        logp("top 3 matches for 'car' -> " + trie.topMatches("car", 3));
        logp("top 2 matches for 'ca' -> " + trie.topMatches("ca", 2));
        logp("matches for 'do' -> " + trie.topMatches("do", 5));
        logp("matches for 'xyz' (none) -> " + trie.topMatches("xyz", 5));
    }
}
