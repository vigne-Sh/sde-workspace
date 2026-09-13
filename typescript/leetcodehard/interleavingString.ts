/*
https://leetcode.com/problems/interleaving-string/

status - completed
*/

import { logp } from "../utils/logger";

function isInterleave(s1: string, s2: string, s3: string): boolean {
  const m = s1.length;
  const n = s2.length;
  if (m + n !== s3.length) return false;

  const dp: boolean[][] = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(false));
  dp[0][0] = true;

  for (let i = 1; i <= m; i++) {
    dp[i][0] = dp[i - 1][0] && s1[i - 1] === s3[i - 1];
  }
  for (let j = 1; j <= n; j++) {
    dp[0][j] = dp[0][j - 1] && s2[j - 1] === s3[j - 1];
  }

  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      const fromS1 = dp[i - 1][j] && s1[i - 1] === s3[i + j - 1];
      const fromS2 = dp[i][j - 1] && s2[j - 1] === s3[i + j - 1];
      dp[i][j] = fromS1 || fromS2;
    }
  }

  return dp[m][n];
}

logp(`input s1="aabcc" s2="dbbca" s3="aadbbcbcac" expected true actual ${isInterleave("aabcc", "dbbca", "aadbbcbcac")}`);
logp(`input s1="aabcc" s2="dbbca" s3="aadbbbaccc" expected false actual ${isInterleave("aabcc", "dbbca", "aadbbbaccc")}`);
logp(`input s1="" s2="" s3="" expected true actual ${isInterleave("", "", "")}`);
