/*
https://leetcode.com/problems/trapping-rain-water/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class TrappingRainWater extends basePrinter{

    static int trap(int[] height){
        if (height.length == 0){
            return 0;
        }

        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int total = 0;

        while (left < right){
            if (height[left] < height[right]){
                if (height[left] >= leftMax){
                    leftMax = height[left];
                }
                else{
                    total += leftMax - height[left];
                }
                left++;
            }
            else{
                if (height[right] >= rightMax){
                    rightMax = height[right];
                }
                else{
                    total += rightMax - height[right];
                }
                right--;
            }
        }

        return total;
    }

    public static void main(String[] args) {
        int[] h1 = {0,1,0,2,1,0,1,3,2,1,2,1};
        logp("input [0,1,0,2,1,0,1,3,2,1,2,1] expected 6 actual " + trap(h1));

        int[] h2 = {4,2,0,3,2,5};
        logp("input [4,2,0,3,2,5] expected 9 actual " + trap(h2));

        int[] h3 = {};
        logp("input [] expected 0 actual " + trap(h3));
    }
}
