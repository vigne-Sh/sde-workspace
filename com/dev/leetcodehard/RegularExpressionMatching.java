/*
https://leetcode.com/problems/regular-expression-matching/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class RegularExpressionMatching extends basePrinter{

    static boolean isMatch(String s, String p){
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int j = 1; j <= n; j++){
            if (p.charAt(j - 1) == '*'){
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++){
            for (int j = 1; j <= n; j++){
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc == '.' || pc == sc){
                    dp[i][j] = dp[i - 1][j - 1];
                }
                else if (pc == '*'){
                    char prev = p.charAt(j - 2);
                    dp[i][j] = dp[i][j - 2];
                    if (prev == '.' || prev == sc){
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
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
        logp("input s=aa p=a* expected true actual " + isMatch("aa", "a*"));
        logp("input s=ab p=.* expected true actual " + isMatch("ab", ".*"));
        logp("input s=mississippi p=mis*is*p*. expected false actual " + isMatch("mississippi", "mis*is*p*."));
    }
}
