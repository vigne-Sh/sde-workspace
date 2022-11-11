/*
https://leetcode.com/problems/remove-duplicates-from-sorted-array/
status - completed
 */

package com.dev.leetcodeeasy;

import com.dev.logger.basePrinter;


public class RemoveDuplicates extends basePrinter{

    static int filterArray(int[] nums){
        int temp = nums[0];
        int ord = 0;
        int unique = 0;
        String storage = new String();
        for(int i:nums){
            logp("parsing " + i + " temp " + temp + " ord " + ord);
            if (temp == i) {
                logp("found " + i);
                if (storage.contains(String.format("%s",i))){
                    logp("dup => " + i);
                }
                else{
                    logp("writing " + i + "at address" + ord);
                    nums[unique] = i;
                    unique += 1;
                    logp("increase ord" + unique);
                    storage += i;
                }
            }

            ord += 1;
            if (ord == nums.length){
                logp("reached end and order count is "  + ord);
                break;
            }
            temp = nums[ord];
        }
        logp("nums " + nums[0] + nums[1] + nums[3]);
        return unique;
    }

    public static void main(String[] args) {
        int[] ip1 = {1,2,2,2,3};
        int[] ip2 = {1,1,2};
        int[] ip3 = {2,2,2,3,4,4,5,7,5,4,4,3,4,2};
        int[] ip4 = {3,1,0,0,0,-3};
        int value = filterArray(ip4);
        logp("final array k => " + value);
    }
}