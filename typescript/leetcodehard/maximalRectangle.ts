/*
https://leetcode.com/problems/maximal-rectangle/

status - completed
*/

import { logp } from "../utils/logger";

function largestRectangleArea(heights: number[]): number {
  const stack: number[] = [];
  let maxArea = 0;

  for (let i = 0; i <= heights.length; i++) {
    const h = i === heights.length ? 0 : heights[i];

    while (stack.length > 0 && heights[stack[stack.length - 1]] >= h) {
      const height = heights[stack.pop()!];
      const width = stack.length === 0 ? i : i - stack[stack.length - 1] - 1;
      maxArea = Math.max(maxArea, height * width);
    }

    stack.push(i);
  }

  return maxArea;
}

function maximalRectangle(matrix: string[][]): number {
  if (matrix.length === 0 || matrix[0].length === 0) return 0;

  const cols = matrix[0].length;
  const heights = new Array(cols).fill(0);
  let maxArea = 0;

  for (const row of matrix) {
    for (let c = 0; c < cols; c++) {
      heights[c] = row[c] === "1" ? heights[c] + 1 : 0;
    }
    maxArea = Math.max(maxArea, largestRectangleArea(heights));
  }

  return maxArea;
}

const matrix1 = [
  ["1", "0", "1", "0", "0"],
  ["1", "0", "1", "1", "1"],
  ["1", "1", "1", "1", "1"],
  ["1", "0", "0", "1", "0"],
];
logp(`input matrix1 expected 6 actual ${maximalRectangle(matrix1)}`);
logp(`input [["0"]] expected 0 actual ${maximalRectangle([["0"]])}`);
