/*
https://leetcode.com/problems/palindrome-partitioning-ii/

status - completed
*/

import { logp } from "../utils/logger";

function minCut(s: string): number {
  const n = s.length;
  const isPalindrome: boolean[][] = Array.from({ length: n }, () => new Array(n).fill(false));

  for (let end = 0; end < n; end++) {
    for (let start = 0; start <= end; start++) {
      if (s[start] === s[end] && (end - start <= 2 || isPalindrome[start + 1][end - 1])) {
        isPalindrome[start][end] = true;
      }
    }
  }

  const dp = new Array(n).fill(Infinity);
  for (let end = 0; end < n; end++) {
    if (isPalindrome[0][end]) {
      dp[end] = 0;
      continue;
    }
    for (let start = 1; start <= end; start++) {
      if (isPalindrome[start][end]) {
        dp[end] = Math.min(dp[end], dp[start - 1] + 1);
      }
    }
  }

  return dp[n - 1];
}

logp(`input "aab" expected 1 actual ${minCut("aab")}`);
logp(`input "a" expected 0 actual ${minCut("a")}`);
logp(`input "ab" expected 1 actual ${minCut("ab")}`);
