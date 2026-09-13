/*
https://leetcode.com/problems/sudoku-solver/

status - completed
*/

import { logp } from "../utils/logger";

function solveSudoku(board: string[][]): void {
  function isValid(row: number, col: number, ch: string): boolean {
    for (let i = 0; i < 9; i++) {
      if (board[row][i] === ch) return false;
      if (board[i][col] === ch) return false;
      const boxRow = 3 * Math.floor(row / 3) + Math.floor(i / 3);
      const boxCol = 3 * Math.floor(col / 3) + (i % 3);
      if (board[boxRow][boxCol] === ch) return false;
    }
    return true;
  }

  function backtrack(): boolean {
    for (let row = 0; row < 9; row++) {
      for (let col = 0; col < 9; col++) {
        if (board[row][col] !== ".") continue;

        for (let d = 1; d <= 9; d++) {
          const ch = String(d);
          if (!isValid(row, col, ch)) continue;

          board[row][col] = ch;
          if (backtrack()) return true;
          board[row][col] = ".";
        }
        return false;
      }
    }
    return true;
  }

  backtrack();
}

function boardToString(board: string[][]): string {
  return board.map((row) => row.join("")).join("|");
}

const board = [
  ["5", "3", ".", ".", "7", ".", ".", ".", "."],
  ["6", ".", ".", "1", "9", "5", ".", ".", "."],
  [".", "9", "8", ".", ".", ".", ".", "6", "."],
  ["8", ".", ".", ".", "6", ".", ".", ".", "3"],
  ["4", ".", ".", "8", ".", "3", ".", ".", "1"],
  ["7", ".", ".", ".", "2", ".", ".", ".", "6"],
  [".", "6", ".", ".", ".", ".", "2", "8", "."],
  [".", ".", ".", "4", "1", "9", ".", ".", "5"],
  [".", ".", ".", ".", "8", ".", ".", "7", "9"],
];

logp(`before solving: ${boardToString(board)}`);
solveSudoku(board);
logp(`after solving: ${boardToString(board)}`);

const expected =
  "534678912" +
  "672195348" +
  "198342567" +
  "859761423" +
  "426853791" +
  "713924856" +
  "961537284" +
  "287419635" +
  "345286179";
const actual = board.map((row) => row.join("")).join("");
logp(`expected solved rows joined ${expected} actual ${actual} match ${expected === actual}`);
