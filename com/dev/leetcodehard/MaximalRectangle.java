/*
https://leetcode.com/problems/maximal-rectangle/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.Stack;

class MaximalRectangle extends basePrinter{

    static int maximalRectangle(char[][] matrix){
        if (matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }

        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (char[] row : matrix){
            for (int c = 0; c < cols; c++){
                if (row[c] == '1'){
                    heights[c]++;
                }
                else{
                    heights[c] = 0;
                }
            }
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }

        return maxArea;
    }

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
        char[][] matrix1 = {
            {'1','0','1','0','0'},
            {'1','0','1','1','1'},
            {'1','1','1','1','1'},
            {'1','0','0','1','0'}
        };
        logp("input matrix1 expected 6 actual " + maximalRectangle(matrix1));

        char[][] matrix2 = {{'0'}};
        logp("input matrix2 [0] expected 0 actual " + maximalRectangle(matrix2));

        char[][] matrix3 = {{'1'}};
        logp("input matrix3 [1] expected 1 actual " + maximalRectangle(matrix3));
    }
}
