/*
https://leetcode.com/problems/wildcard-matching/

status - completed
*/

import { logp } from "../utils/logger";

function isMatch(s: string, p: string): boolean {
  const m = s.length;
  const n = p.length;
  const dp: boolean[][] = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(false));
  dp[0][0] = true;

  for (let j = 1; j <= n; j++) {
    if (p[j - 1] === "*") {
      dp[0][j] = dp[0][j - 1];
    }
  }

  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      if (p[j - 1] === "*") {
        dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
      } else if (p[j - 1] === "?" || p[j - 1] === s[i - 1]) {
        dp[i][j] = dp[i - 1][j - 1];
      }
    }
  }

  return dp[m][n];
}

logp(`input s="aa" p="a" expected false actual ${isMatch("aa", "a")}`);
logp(`input s="adceb" p="*a*b" expected true actual ${isMatch("adceb", "*a*b")}`);
logp(`input s="cb" p="?a" expected false actual ${isMatch("cb", "?a")}`);
