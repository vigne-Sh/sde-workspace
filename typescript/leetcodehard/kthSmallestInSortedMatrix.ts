/*
https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/

status - completed
*/

import { logp } from "../utils/logger";

function kthSmallest(matrix: number[][], k: number): number {
  const n = matrix.length;
  let low = matrix[0][0];
  let high = matrix[n - 1][n - 1];

  function countLessEqual(target: number): number {
    let count = 0;
    let row = n - 1;
    let col = 0;

    while (row >= 0 && col < n) {
      if (matrix[row][col] <= target) {
        count += row + 1;
        col++;
      } else {
        row--;
      }
    }

    return count;
  }

  while (low < high) {
    const mid = low + Math.floor((high - low) / 2);
    if (countLessEqual(mid) < k) {
      low = mid + 1;
    } else {
      high = mid;
    }
  }

  return low;
}

logp(
  `input matrix=[[1,5,9],[10,11,13],[12,13,15]] k=8 expected 13 actual ${kthSmallest(
    [[1, 5, 9], [10, 11, 13], [12, 13, 15]],
    8,
  )}`,
);
logp(`input matrix=[[-5]] k=1 expected -5 actual ${kthSmallest([[-5]], 1)}`);
