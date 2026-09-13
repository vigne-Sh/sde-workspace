/*
https://leetcode.com/problems/n-queens/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class NQueens extends basePrinter{

    static int count;

    static int totalNQueens(int n){
        count = 0;
        boolean[] cols = new boolean[n];
        boolean[] diag1 = new boolean[2 * n];
        boolean[] diag2 = new boolean[2 * n];
        solve(0, n, cols, diag1, diag2);
        return count;
    }

    static void solve(int row, int n, boolean[] cols, boolean[] diag1, boolean[] diag2){
        if (row == n){
            count++;
            return;
        }

        for (int col = 0; col < n; col++){
            int d1 = row + col;
            int d2 = row - col + n;
            if (cols[col] || diag1[d1] || diag2[d2]){
                continue;
            }
            cols[col] = true;
            diag1[d1] = true;
            diag2[d2] = true;

            solve(row + 1, n, cols, diag1, diag2);

            cols[col] = false;
            diag1[d1] = false;
            diag2[d2] = false;
        }
    }

    public static void main(String[] args) {
        logp("input n=4 expected 2 actual " + totalNQueens(4));
        logp("input n=1 expected 1 actual " + totalNQueens(1));
        logp("input n=8 expected 92 actual " + totalNQueens(8));
    }
}
