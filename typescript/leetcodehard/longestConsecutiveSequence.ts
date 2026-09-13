/*
https://leetcode.com/problems/longest-consecutive-sequence/

status - completed
*/

import { logp } from "../utils/logger";

function longestConsecutive(nums: number[]): number {
  const numSet = new Set(nums);
  let longest = 0;

  for (const num of numSet) {
    if (numSet.has(num - 1)) continue;

    let length = 1;
    let current = num;
    while (numSet.has(current + 1)) {
      current++;
      length++;
    }

    longest = Math.max(longest, length);
  }

  return longest;
}

logp(`input [100,4,200,1,3,2] expected 4 actual ${longestConsecutive([100, 4, 200, 1, 3, 2])}`);
logp(
  `input [0,3,7,2,5,8,4,6,0,1] expected 9 actual ${longestConsecutive([0, 3, 7, 2, 5, 8, 4, 6, 0, 1])}`,
);
