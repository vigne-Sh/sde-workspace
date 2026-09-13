/*
https://leetcode.com/problems/longest-consecutive-sequence/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.HashSet;
import java.util.Set;

class LongestConsecutiveSequence extends basePrinter{

    static int longestConsecutive(int[] nums){
        Set<Integer> set = new HashSet<>();
        for (int n : nums){
            set.add(n);
        }

        int longest = 0;

        for (int n : set){
            if (!set.contains(n - 1)){
                int length = 1;
                int curr = n;
                while (set.contains(curr + 1)){
                    curr++;
                    length++;
                }
                longest = Math.max(longest, length);
            }
        }

        return longest;
    }

    public static void main(String[] args) {
        int[] nums1 = {100,4,200,1,3,2};
        logp("input [100,4,200,1,3,2] expected 4 actual " + longestConsecutive(nums1));

        int[] nums2 = {0,3,7,2,5,8,4,6,0,1};
        logp("input [0,3,7,2,5,8,4,6,0,1] expected 9 actual " + longestConsecutive(nums2));

        int[] nums3 = {};
        logp("input [] expected 0 actual " + longestConsecutive(nums3));
    }
}
