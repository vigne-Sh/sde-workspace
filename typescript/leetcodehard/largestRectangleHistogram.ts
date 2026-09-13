/*
https://leetcode.com/problems/largest-rectangle-in-histogram/

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

logp(`input [2,1,5,6,2,3] expected 10 actual ${largestRectangleArea([2, 1, 5, 6, 2, 3])}`);
logp(`input [2,4] expected 4 actual ${largestRectangleArea([2, 4])}`);
