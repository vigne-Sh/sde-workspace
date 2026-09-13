/*
https://leetcode.com/problems/trapping-rain-water/

status - completed
*/

import { logp } from "../utils/logger";

function trap(height: number[]): number {
  let left = 0;
  let right = height.length - 1;
  let leftMax = 0;
  let rightMax = 0;
  let total = 0;

  while (left < right) {
    if (height[left] <= height[right]) {
      leftMax = Math.max(leftMax, height[left]);
      total += leftMax - height[left];
      left++;
    } else {
      rightMax = Math.max(rightMax, height[right]);
      total += rightMax - height[right];
      right--;
    }
  }

  return total;
}

logp(
  `input [0,1,0,2,1,0,1,3,2,1,2,1] expected 6 actual ${trap([0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1])}`,
);
logp(`input [4,2,0,3,2,5] expected 9 actual ${trap([4, 2, 0, 3, 2, 5])}`);
