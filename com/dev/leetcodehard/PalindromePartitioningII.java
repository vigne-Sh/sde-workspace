/*
https://leetcode.com/problems/palindrome-partitioning-ii/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class PalindromePartitioningII extends basePrinter{

    static int minCut(String s){
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];

        for (int right = 0; right < n; right++){
            for (int left = 0; left <= right; left++){
                if (s.charAt(left) == s.charAt(right) && (right - left <= 2 || isPalindrome[left + 1][right - 1])){
                    isPalindrome[left][right] = true;
                }
            }
        }

        int[] cuts = new int[n];
        for (int i = 0; i < n; i++){
            if (isPalindrome[0][i]){
                cuts[i] = 0;
                continue;
            }
            cuts[i] = i;
            for (int j = 1; j <= i; j++){
                if (isPalindrome[j][i]){
                    cuts[i] = Math.min(cuts[i], cuts[j - 1] + 1);
                }
            }
        }

        return cuts[n - 1];
    }

    public static void main(String[] args) {
        logp("input aab expected 1 actual " + minCut("aab"));
        logp("input a expected 0 actual " + minCut("a"));
        logp("input ab expected 1 actual " + minCut("ab"));
        logp("input racecarxyz expected 3 actual " + minCut("racecarxyz"));
    }
}
