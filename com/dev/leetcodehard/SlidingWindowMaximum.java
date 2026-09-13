/*
https://leetcode.com/problems/sliding-window-maximum/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.ArrayDeque;
import java.util.Deque;

class SlidingWindowMaximum extends basePrinter{

    static int[] maxSlidingWindow(int[] nums, int k){
        if (nums.length == 0 || k == 0){
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++){
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1){
                deque.pollFirst();
            }

            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]){
                deque.pollLast();
            }

            deque.offerLast(i);

            if (i >= k - 1){
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums1 = {1,3,-1,-3,5,3,6,7};
        logp("input [1,3,-1,-3,5,3,6,7] k=3 expected [3,3,5,5,6,7] actual " + arrToString(maxSlidingWindow(nums1, 3)));

        int[] nums2 = {1};
        logp("input [1] k=1 expected [1] actual " + arrToString(maxSlidingWindow(nums2, 1)));

        int[] nums3 = {9,11};
        logp("input [9,11] k=2 expected [11] actual " + arrToString(maxSlidingWindow(nums3, 2)));
    }

    static String arrToString(int[] arr){
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++){
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
