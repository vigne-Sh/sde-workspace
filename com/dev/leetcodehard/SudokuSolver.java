/*
https://leetcode.com/problems/sudoku-solver/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class SudokuSolver extends basePrinter{

    static void solveSudoku(char[][] board){
        solve(board);
    }

    static boolean solve(char[][] board){
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (board[row][col] == '.'){
                    for (char c = '1'; c <= '9'; c++){
                        if (isValid(board, row, col, c)){
                            board[row][col] = c;
                            if (solve(board)){
                                return true;
                            }
                            board[row][col] = '.';
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isValid(char[][] board, int row, int col, char c){
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;

        for (int i = 0; i < 9; i++){
            if (board[row][i] == c) return false;
            if (board[i][col] == c) return false;
            if (board[boxRow + i / 3][boxCol + i % 3] == c) return false;
        }
        return true;
    }

    static void printBoard(char[][] board){
        for (char[] row : board){
            StringBuilder sb = new StringBuilder();
            for (char c : row){
                sb.append(c).append(" ");
            }
            logp(sb.toString());
        }
    }

    public static void main(String[] args) {
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };

        logp("before solving:");
        printBoard(board);

        solveSudoku(board);

        logp("after solving:");
        printBoard(board);

        boolean firstRowCorrect = board[0][2] == '4';
        logp("expected board[0][2]=4 actual " + board[0][2] + " correct=" + firstRowCorrect);

        boolean lastRowCorrect = board[8][0] == '3';
        logp("expected board[8][0]=3 actual " + board[8][0] + " correct=" + lastRowCorrect);
    }
}
