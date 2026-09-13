/*
https://leetcode.com/problems/edit-distance/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class EditDistance extends basePrinter{

    static int minDistance(String word1, String word2){
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++){
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++){
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++){
            for (int j = 1; j <= n; j++){
                if (word1.charAt(i - 1) == word2.charAt(j - 1)){
                    dp[i][j] = dp[i - 1][j - 1];
                }
                else{
                    int replace = dp[i - 1][j - 1];
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    dp[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        logp("input word1=horse word2=ros expected 3 actual " + minDistance("horse", "ros"));
        logp("input word1=intention word2=execution expected 5 actual " + minDistance("intention", "execution"));
        logp("input word1= word2=abc expected 3 actual " + minDistance("", "abc"));
    }
}
