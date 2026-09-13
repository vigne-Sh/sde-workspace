/*
https://leetcode.com/problems/distinct-subsequences/

status - completed
*/

import { logp } from "../utils/logger";

function numDistinct(s: string, t: string): number {
  const m = s.length;
  const n = t.length;
  const dp: number[][] = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(0));

  for (let i = 0; i <= m; i++) dp[i][0] = 1;

  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      dp[i][j] = dp[i - 1][j];
      if (s[i - 1] === t[j - 1]) {
        dp[i][j] += dp[i - 1][j - 1];
      }
    }
  }

  return dp[m][n];
}

logp(`input s="rabbbit" t="rabbit" expected 3 actual ${numDistinct("rabbbit", "rabbit")}`);
logp(`input s="babgbag" t="bag" expected 5 actual ${numDistinct("babgbag", "bag")}`);
