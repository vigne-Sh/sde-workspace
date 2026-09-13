/*
https://leetcode.com/problems/burst-balloons/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class BurstBalloons extends basePrinter{

    static int maxCoins(int[] nums){
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = 1;
        balloons[n + 1] = 1;
        for (int i = 0; i < n; i++){
            balloons[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 1; len <= n; len++){
            for (int left = 1; left + len - 1 <= n; left++){
                int right = left + len - 1;
                int best = 0;
                for (int k = left; k <= right; k++){
                    int coins = balloons[left - 1] * balloons[k] * balloons[right + 1]
                            + dp[left][k - 1] + dp[k + 1][right];
                    best = Math.max(best, coins);
                }
                dp[left][right] = best;
            }
        }

        return dp[1][n];
    }

    public static void main(String[] args) {
        int[] nums1 = {3,1,5,8};
        logp("input [3,1,5,8] expected 167 actual " + maxCoins(nums1));

        int[] nums2 = {1,5};
        logp("input [1,5] expected 10 actual " + maxCoins(nums2));

        int[] nums3 = {7};
        logp("input [7] expected 7 actual " + maxCoins(nums3));
    }
}
