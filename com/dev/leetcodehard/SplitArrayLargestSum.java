/*
https://leetcode.com/problems/split-array-largest-sum/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class SplitArrayLargestSum extends basePrinter{

    static int splitArray(int[] nums, int m){
        int low = 0, high = 0;
        for (int n : nums){
            low = Math.max(low, n);
            high += n;
        }

        while (low < high){
            int mid = low + (high - low) / 2;
            if (canSplit(nums, m, mid)){
                high = mid;
            }
            else{
                low = mid + 1;
            }
        }

        return low;
    }

    static boolean canSplit(int[] nums, int m, int maxSum){
        int pieces = 1;
        int currSum = 0;

        for (int n : nums){
            if (currSum + n > maxSum){
                pieces++;
                currSum = n;
                if (pieces > m){
                    return false;
                }
            }
            else{
                currSum += n;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        int[] nums1 = {7,2,5,10,8};
        logp("input [7,2,5,10,8] m=2 expected 18 actual " + splitArray(nums1, 2));

        int[] nums2 = {1,2,3,4,5};
        logp("input [1,2,3,4,5] m=2 expected 9 actual " + splitArray(nums2, 2));

        int[] nums3 = {1,4,4};
        logp("input [1,4,4] m=3 expected 4 actual " + splitArray(nums3, 3));
    }
}
