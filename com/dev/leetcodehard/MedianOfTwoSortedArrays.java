/*
https://leetcode.com/problems/median-of-two-sorted-arrays/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class MedianOfTwoSortedArrays extends basePrinter{

    static double findMedianSortedArrays(int[] nums1, int[] nums2){
        int[] a = nums1;
        int[] b = nums2;
        if (a.length > b.length){
            a = nums2;
            b = nums1;
        }

        int m = a.length;
        int n = b.length;
        int low = 0, high = m;
        int half = (m + n + 1) / 2;

        while (low <= high){
            int cutA = (low + high) / 2;
            int cutB = half - cutA;

            int leftA = (cutA == 0) ? Integer.MIN_VALUE : a[cutA - 1];
            int leftB = (cutB == 0) ? Integer.MIN_VALUE : b[cutB - 1];
            int rightA = (cutA == m) ? Integer.MAX_VALUE : a[cutA];
            int rightB = (cutB == n) ? Integer.MAX_VALUE : b[cutB];

            if (leftA <= rightB && leftB <= rightA){
                if ((m + n) % 2 == 0){
                    return (Math.max(leftA, leftB) + Math.min(rightA, rightB)) / 2.0;
                }
                else{
                    return Math.max(leftA, leftB);
                }
            }
            else if (leftA > rightB){
                high = cutA - 1;
            }
            else{
                low = cutA + 1;
            }
        }

        return 0.0;
    }

    public static void main(String[] args) {
        int[] nums1 = {1, 3};
        int[] nums2 = {2};
        logp("input [1,3] [2] expected 2.0 actual " + findMedianSortedArrays(nums1, nums2));

        int[] nums3 = {1, 2};
        int[] nums4 = {3, 4};
        logp("input [1,2] [3,4] expected 2.5 actual " + findMedianSortedArrays(nums3, nums4));

        int[] nums5 = {};
        int[] nums6 = {1};
        logp("input [] [1] expected 1.0 actual " + findMedianSortedArrays(nums5, nums6));
    }
}
