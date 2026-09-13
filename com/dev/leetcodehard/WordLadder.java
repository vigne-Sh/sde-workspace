/*
https://leetcode.com/problems/word-ladder/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

class WordLadder extends basePrinter{

    static int ladderLength(String beginWord, String endWord, List<String> wordList){
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)){
            return 0;
        }

        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);

        int level = 1;

        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++){
                String word = queue.poll();
                if (word.equals(endWord)){
                    return level;
                }

                char[] chars = word.toCharArray();
                for (int pos = 0; pos < chars.length; pos++){
                    char original = chars[pos];
                    for (char c = 'a'; c <= 'z'; c++){
                        if (c == original) continue;
                        chars[pos] = c;
                        String next = new String(chars);
                        if (dict.contains(next) && !visited.contains(next)){
                            visited.add(next);
                            queue.add(next);
                        }
                    }
                    chars[pos] = original;
                }
            }
            level++;
        }

        return 0;
    }

    public static void main(String[] args) {
        List<String> wordList1 = List.of("hot","dot","dog","lot","log","cog");
        logp("input hit->cog expected 5 actual " + ladderLength("hit", "cog", wordList1));

        List<String> wordList2 = List.of("hot","dot","dog","lot","log");
        logp("input hit->cog (no cog in dict) expected 0 actual " + ladderLength("hit", "cog", wordList2));

        List<String> wordList3 = List.of("a","b","c");
        logp("input a->c expected 2 actual " + ladderLength("a", "c", wordList3));
    }
}
