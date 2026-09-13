/*
https://leetcode.com/problems/distinct-subsequences/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class DistinctSubsequences extends basePrinter{

    static int numDistinct(String s, String t){
        int m = s.length(), n = t.length();
        long[][] dp = new long[m + 1][n + 1];

        for (int i = 0; i <= m; i++){
            dp[i][0] = 1;
        }

        for (int i = 1; i <= m; i++){
            for (int j = 1; j <= n; j++){
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1)){
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }

        return (int) dp[m][n];
    }

    public static void main(String[] args) {
        logp("input s=rabbbit t=rabbit expected 3 actual " + numDistinct("rabbbit", "rabbit"));
        logp("input s=babgbag t=bag expected 5 actual " + numDistinct("babgbag", "bag"));
        logp("input s=abc t=abc expected 1 actual " + numDistinct("abc", "abc"));
    }
}
