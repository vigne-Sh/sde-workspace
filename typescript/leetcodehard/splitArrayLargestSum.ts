/*
https://leetcode.com/problems/split-array-largest-sum/

status - completed
*/

import { logp } from "../utils/logger";

function splitArray(nums: number[], m: number): number {
  let low = Math.max(...nums);
  let high = nums.reduce((sum, n) => sum + n, 0);

  function countGroups(maxSum: number): number {
    let groups = 1;
    let currentSum = 0;

    for (const num of nums) {
      if (currentSum + num > maxSum) {
        groups++;
        currentSum = num;
      } else {
        currentSum += num;
      }
    }

    return groups;
  }

  while (low < high) {
    const mid = low + Math.floor((high - low) / 2);
    if (countGroups(mid) > m) {
      low = mid + 1;
    } else {
      high = mid;
    }
  }

  return low;
}

logp(`input nums=[7,2,5,10,8] m=2 expected 18 actual ${splitArray([7, 2, 5, 10, 8], 2)}`);
logp(`input nums=[1,2,3,4,5] m=2 expected 9 actual ${splitArray([1, 2, 3, 4, 5], 2)}`);
logp(`input nums=[1,4,4] m=3 expected 4 actual ${splitArray([1, 4, 4], 3)}`);
