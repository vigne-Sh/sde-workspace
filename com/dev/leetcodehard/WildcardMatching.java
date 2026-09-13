/*
https://leetcode.com/problems/wildcard-matching/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class WildcardMatching extends basePrinter{

    static boolean isMatch(String s, String p){
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int j = 1; j <= n; j++){
            if (p.charAt(j - 1) == '*'){
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++){
            for (int j = 1; j <= n; j++){
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc == '?' || pc == sc){
                    dp[i][j] = dp[i - 1][j - 1];
                }
                else if (pc == '*'){
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                }
                else{
                    dp[i][j] = false;
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        logp("input s=aa p=a expected false actual " + isMatch("aa", "a"));
        logp("input s=aa p=* expected true actual " + isMatch("aa", "*"));
        logp("input s=cb p=?a expected false actual " + isMatch("cb", "?a"));
        logp("input s=adceb p=*a*b expected true actual " + isMatch("adceb", "*a*b"));
    }
}
