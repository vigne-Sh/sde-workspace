/*
https://leetcode.com/problems/longest-increasing-path-in-a-matrix/

status - completed
*/

import { logp } from "../utils/logger";

function longestIncreasingPath(matrix: number[][]): number {
  const rows = matrix.length;
  const cols = matrix[0].length;
  const memo: number[][] = Array.from({ length: rows }, () => new Array(cols).fill(0));
  const directions = [
    [1, 0],
    [-1, 0],
    [0, 1],
    [0, -1],
  ];

  function dfs(row: number, col: number): number {
    if (memo[row][col] !== 0) return memo[row][col];

    let best = 1;
    for (const [dr, dc] of directions) {
      const nr = row + dr;
      const nc = col + dc;
      if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
      if (matrix[nr][nc] <= matrix[row][col]) continue;
      best = Math.max(best, 1 + dfs(nr, nc));
    }

    memo[row][col] = best;
    return best;
  }

  let result = 0;
  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
      result = Math.max(result, dfs(row, col));
    }
  }

  return result;
}

logp(
  `input [[9,9,4],[6,6,8],[2,1,1]] expected 4 actual ${longestIncreasingPath([
    [9, 9, 4],
    [6, 6, 8],
    [2, 1, 1],
  ])}`,
);
logp(
  `input [[3,4,5],[3,2,6],[2,2,1]] expected 4 actual ${longestIncreasingPath([
    [3, 4, 5],
    [3, 2, 6],
    [2, 2, 1],
  ])}`,
);
