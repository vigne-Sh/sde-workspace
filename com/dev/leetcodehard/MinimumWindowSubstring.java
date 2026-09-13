/*
https://leetcode.com/problems/minimum-window-substring/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.HashMap;
import java.util.Map;

class MinimumWindowSubstring extends basePrinter{

    static String minWindow(String s, String t){
        if (s.length() == 0 || t.length() == 0){
            return "";
        }

        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()){
            need.put(c, need.getOrDefault(c, 0) + 1);
        }

        Map<Character, Integer> window = new HashMap<>();
        int required = need.size();
        int formed = 0;

        int left = 0;
        int bestLen = Integer.MAX_VALUE;
        int bestStart = 0;

        for (int right = 0; right < s.length(); right++){
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);

            if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()){
                formed++;
            }

            while (formed == required){
                if (right - left + 1 < bestLen){
                    bestLen = right - left + 1;
                    bestStart = left;
                }

                char leftChar = s.charAt(left);
                window.put(leftChar, window.get(leftChar) - 1);
                if (need.containsKey(leftChar) && window.get(leftChar) < need.get(leftChar)){
                    formed--;
                }
                left++;
            }
        }

        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    public static void main(String[] args) {
        logp("input s=ADOBECODEBANC t=ABC expected BANC actual " + minWindow("ADOBECODEBANC", "ABC"));
        logp("input s=a t=a expected a actual " + minWindow("a", "a"));
        logp("input s=a t=aa expected (empty) actual '" + minWindow("a", "aa") + "'");
    }
}
