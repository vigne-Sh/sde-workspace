/*
https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class KthSmallestInSortedMatrix extends basePrinter{

    static int kthSmallest(int[][] matrix, int k){
        int n = matrix.length;
        int low = matrix[0][0];
        int high = matrix[n - 1][n - 1];

        while (low < high){
            int mid = low + (high - low) / 2;
            int count = countLessEqual(matrix, mid);

            if (count < k){
                low = mid + 1;
            }
            else{
                high = mid;
            }
        }

        return low;
    }

    static int countLessEqual(int[][] matrix, int target){
        int n = matrix.length;
        int count = 0;
        int row = n - 1;
        int col = 0;

        while (row >= 0 && col < n){
            if (matrix[row][col] <= target){
                count += row + 1;
                col++;
            }
            else{
                row--;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[][] matrix1 = {
            {1,5,9},
            {10,11,13},
            {12,13,15}
        };
        logp("input matrix1 k=8 expected 13 actual " + kthSmallest(matrix1, 8));

        int[][] matrix2 = {{-5}};
        logp("input matrix2 [[-5]] k=1 expected -5 actual " + kthSmallest(matrix2, 1));

        int[][] matrix3 = {
            {1,2},
            {1,3}
        };
        logp("input matrix3 k=2 expected 1 actual " + kthSmallest(matrix3, 2));
    }
}
