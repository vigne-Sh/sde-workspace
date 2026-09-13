/*
https://leetcode.com/problems/burst-balloons/

status - completed
*/

import { logp } from "../utils/logger";

function maxCoins(nums: number[]): number {
  const balloons = [1, ...nums, 1];
  const n = balloons.length;
  const dp: number[][] = Array.from({ length: n }, () => new Array(n).fill(0));

  for (let length = 2; length < n; length++) {
    for (let left = 0; left + length < n; left++) {
      const right = left + length;
      for (let k = left + 1; k < right; k++) {
        const coins = balloons[left] * balloons[k] * balloons[right] + dp[left][k] + dp[k][right];
        dp[left][right] = Math.max(dp[left][right], coins);
      }
    }
  }

  return dp[0][n - 1];
}

logp(`input [3,1,5,8] expected 167 actual ${maxCoins([3, 1, 5, 8])}`);
logp(`input [1,5] expected 10 actual ${maxCoins([1, 5])}`);
