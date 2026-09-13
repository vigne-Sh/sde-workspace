/*
https://leetcode.com/problems/largest-rectangle-in-histogram/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.Stack;

class LargestRectangleHistogram extends basePrinter{

    static int largestRectangleArea(int[] heights){
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i <= n; i++){
            int curr = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] >= curr){
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }

        return maxArea;
    }

    public static void main(String[] args) {
        int[] h1 = {2,1,5,6,2,3};
        logp("input [2,1,5,6,2,3] expected 10 actual " + largestRectangleArea(h1));

        int[] h2 = {2,4};
        logp("input [2,4] expected 4 actual " + largestRectangleArea(h2));

        int[] h3 = {1,1,1,1};
        logp("input [1,1,1,1] expected 4 actual " + largestRectangleArea(h3));
    }
}
