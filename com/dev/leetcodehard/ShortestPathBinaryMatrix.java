/*
https://leetcode.com/problems/shortest-path-in-binary-matrix/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.LinkedList;
import java.util.Queue;

class ShortestPathBinaryMatrix extends basePrinter{

    static int[][] dirs = {
        {-1,-1},{-1,0},{-1,1},
        {0,-1},{0,1},
        {1,-1},{1,0},{1,1}
    };

    static int shortestPathBinaryMatrix(int[][] grid){
        int n = grid.length;
        if (n == 0 || grid[0][0] == 1 || grid[n - 1][n - 1] == 1){
            return -1;
        }

        if (n == 1){
            return 1;
        }

        boolean[][] visited = new boolean[n][n];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        visited[0][0] = true;
        int steps = 1;

        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++){
                int[] curr = queue.poll();
                int r = curr[0], c = curr[1];

                if (r == n - 1 && c == n - 1){
                    return steps;
                }

                for (int[] d : dirs){
                    int nr = r + d[0];
                    int nc = c + d[1];
                    if (nr >= 0 && nr < n && nc >= 0 && nc < n && !visited[nr][nc] && grid[nr][nc] == 0){
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
            steps++;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[][] grid1 = {{0,1},{1,0}};
        logp("input [[0,1],[1,0]] expected 2 actual " + shortestPathBinaryMatrix(grid1));

        int[][] grid2 = {{0,0,0},{1,1,0},{1,1,0}};
        logp("input [[0,0,0],[1,1,0],[1,1,0]] expected 4 actual " + shortestPathBinaryMatrix(grid2));

        int[][] grid3 = {{1,0,0},{1,1,0},{1,1,0}};
        logp("input [[1,0,0],[1,1,0],[1,1,0]] expected -1 actual " + shortestPathBinaryMatrix(grid3));
    }
}
