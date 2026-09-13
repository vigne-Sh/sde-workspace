/*
https://leetcode.com/problems/longest-increasing-path-in-a-matrix/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class LongestIncreasingPathMatrix extends basePrinter{

    static int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    static int longestIncreasingPath(int[][] matrix){
        if (matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] memo = new int[rows][cols];
        int best = 0;

        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                best = Math.max(best, dfs(matrix, r, c, memo));
            }
        }

        return best;
    }

    static int dfs(int[][] matrix, int r, int c, int[][] memo){
        if (memo[r][c] != 0){
            return memo[r][c];
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int best = 1;

        for (int[] d : dirs){
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && matrix[nr][nc] > matrix[r][c]){
                best = Math.max(best, 1 + dfs(matrix, nr, nc, memo));
            }
        }

        memo[r][c] = best;
        return best;
    }

    public static void main(String[] args) {
        int[][] matrix1 = {
            {9,9,4},
            {6,6,8},
            {2,1,1}
        };
        logp("input matrix1 expected 4 actual " + longestIncreasingPath(matrix1));

        int[][] matrix2 = {
            {3,4,5},
            {3,2,6},
            {2,2,1}
        };
        logp("input matrix2 expected 4 actual " + longestIncreasingPath(matrix2));

        int[][] matrix3 = {{1}};
        logp("input matrix3 expected 1 actual " + longestIncreasingPath(matrix3));
    }
}
