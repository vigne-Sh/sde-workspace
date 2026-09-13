/*
https://leetcode.com/problems/sliding-window-maximum/

status - completed
*/

import { logp } from "../utils/logger";

function maxSlidingWindow(nums: number[], k: number): number[] {
  const deque: number[] = [];
  const result: number[] = [];

  for (let i = 0; i < nums.length; i++) {
    while (deque.length > 0 && deque[0] <= i - k) {
      deque.shift();
    }

    while (deque.length > 0 && nums[deque[deque.length - 1]] < nums[i]) {
      deque.pop();
    }

    deque.push(i);

    if (i >= k - 1) {
      result.push(nums[deque[0]]);
    }
  }

  return result;
}

logp(
  `input nums=[1,3,-1,-3,5,3,6,7] k=3 expected [3,3,5,5,6,7] actual ${JSON.stringify(
    maxSlidingWindow([1, 3, -1, -3, 5, 3, 6, 7], 3),
  )}`,
);
logp(`input nums=[1] k=1 expected [1] actual ${JSON.stringify(maxSlidingWindow([1], 1))}`);
