/*
https://leetcode.com/problems/word-search-ii/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.ArrayList;
import java.util.List;

class WordSearchII extends basePrinter{

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    static List<String> findWords(char[][] board, String[] words){
        TrieNode root = new TrieNode();
        for (String w : words){
            TrieNode node = root;
            for (char c : w.toCharArray()){
                int idx = c - 'a';
                if (node.children[idx] == null){
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = w;
        }

        List<String> result = new ArrayList<>();
        int rows = board.length;
        int cols = board[0].length;

        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                dfs(board, r, c, root, result);
            }
        }

        return result;
    }

    static void dfs(char[][] board, int r, int c, TrieNode node, List<String> result){
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length){
            return;
        }
        char ch = board[r][c];
        if (ch == '#'){
            return;
        }
        TrieNode next = node.children[ch - 'a'];
        if (next == null){
            return;
        }

        if (next.word != null){
            result.add(next.word);
            next.word = null;
        }

        board[r][c] = '#';
        dfs(board, r + 1, c, next, result);
        dfs(board, r - 1, c, next, result);
        dfs(board, r, c + 1, next, result);
        dfs(board, r, c - 1, next, result);
        board[r][c] = ch;
    }

    public static void main(String[] args) {
        char[][] board1 = {
            {'o','a','a','n'},
            {'e','t','a','e'},
            {'i','h','k','r'},
            {'i','f','l','v'}
        };
        String[] words1 = {"oath","pea","eat","rain"};
        List<String> result1 = findWords(board1, words1);
        logp("input board1 words=[oath,pea,eat,rain] expected [oath,eat] actual " + result1);

        char[][] board2 = {
            {'a','b'},
            {'c','d'}
        };
        String[] words2 = {"abcb"};
        List<String> result2 = findWords(board2, words2);
        logp("input board2 words=[abcb] expected [] actual " + result2);
    }
}
