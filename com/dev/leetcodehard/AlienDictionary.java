/*
https://leetcode.com/problems/alien-dictionary/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class AlienDictionary extends basePrinter{

    static String alienOrder(String[] words){
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();

        for (String w : words){
            for (char c : w.toCharArray()){
                graph.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }

        for (int i = 0; i < words.length - 1; i++){
            String first = words[i];
            String second = words[i + 1];
            int minLen = Math.min(first.length(), second.length());

            if (first.length() > second.length() && first.substring(0, minLen).equals(second.substring(0, minLen))){
                return "";
            }

            for (int j = 0; j < minLen; j++){
                char c1 = first.charAt(j);
                char c2 = second.charAt(j);
                if (c1 != c2){
                    if (!graph.get(c1).contains(c2)){
                        graph.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    break;
                }
            }
        }

        Queue<Character> queue = new LinkedList<>();
        for (Character c : inDegree.keySet()){
            if (inDegree.get(c) == 0){
                queue.add(c);
            }
        }

        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()){
            char c = queue.poll();
            result.append(c);
            for (char next : graph.get(c)){
                inDegree.put(next, inDegree.get(next) - 1);
                if (inDegree.get(next) == 0){
                    queue.add(next);
                }
            }
        }

        if (result.length() != inDegree.size()){
            return "";
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String[] words1 = {"wrt","wrf","er","ett","rftt"};
        logp("input [wrt,wrf,er,ett,rftt] expected wertf actual " + alienOrder(words1));

        String[] words2 = {"z","x"};
        logp("input [z,x] expected zx actual " + alienOrder(words2));

        String[] words3 = {"z","x","z"};
        logp("input [z,x,z] expected (empty, cycle) actual '" + alienOrder(words3) + "'");
    }
}
