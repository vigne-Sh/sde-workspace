/*
https://leetcode.com/problems/shortest-path-in-binary-matrix/

status - completed
*/

import { logp } from "../utils/logger";

function shortestPathBinaryMatrix(grid: number[][]): number {
  const n = grid.length;
  if (grid[0][0] !== 0 || grid[n - 1][n - 1] !== 0) return -1;

  const directions = [
    [-1, -1], [-1, 0], [-1, 1],
    [0, -1], [0, 1],
    [1, -1], [1, 0], [1, 1],
  ];

  const visited: boolean[][] = Array.from({ length: n }, () => new Array(n).fill(false));
  visited[0][0] = true;
  const queue: Array<[number, number, number]> = [[0, 0, 1]];

  while (queue.length > 0) {
    const [row, col, dist] = queue.shift()!;
    if (row === n - 1 && col === n - 1) return dist;

    for (const [dr, dc] of directions) {
      const nr = row + dr;
      const nc = col + dc;
      if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;
      if (visited[nr][nc] || grid[nr][nc] !== 0) continue;

      visited[nr][nc] = true;
      queue.push([nr, nc, dist + 1]);
    }
  }

  return -1;
}

logp(`input [[0,1],[1,0]] expected 2 actual ${shortestPathBinaryMatrix([[0, 1], [1, 0]])}`);
logp(
  `input [[0,0,0],[1,1,0],[1,1,0]] expected 4 actual ${shortestPathBinaryMatrix([
    [0, 0, 0],
    [1, 1, 0],
    [1, 1, 0],
  ])}`,
);
logp(`input [[1,0,0],[1,1,0],[1,1,0]] expected -1 actual ${shortestPathBinaryMatrix([[1, 0, 0], [1, 1, 0], [1, 1, 0]])}`);
