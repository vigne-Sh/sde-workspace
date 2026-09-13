/*
https://leetcode.com/problems/median-of-two-sorted-arrays/

status - completed
*/

import { logp } from "../utils/logger";

function findMedianSortedArrays(nums1: number[], nums2: number[]): number {
  let a = nums1;
  let b = nums2;
  if (a.length > b.length) {
    a = nums2;
    b = nums1;
  }

  const m = a.length;
  const n = b.length;
  const half = Math.floor((m + n + 1) / 2);
  let low = 0;
  let high = m;

  while (low <= high) {
    const cutA = Math.floor((low + high) / 2);
    const cutB = half - cutA;

    const leftA = cutA === 0 ? -Infinity : a[cutA - 1];
    const leftB = cutB === 0 ? -Infinity : b[cutB - 1];
    const rightA = cutA === m ? Infinity : a[cutA];
    const rightB = cutB === n ? Infinity : b[cutB];

    if (leftA <= rightB && leftB <= rightA) {
      if ((m + n) % 2 === 0) {
        return (Math.max(leftA, leftB) + Math.min(rightA, rightB)) / 2;
      }
      return Math.max(leftA, leftB);
    } else if (leftA > rightB) {
      high = cutA - 1;
    } else {
      low = cutA + 1;
    }
  }

  throw new Error("input arrays are not sorted");
}

logp(`input [1,3] [2] expected 2 actual ${findMedianSortedArrays([1, 3], [2])}`);
logp(`input [1,2] [3,4] expected 2.5 actual ${findMedianSortedArrays([1, 2], [3, 4])}`);
logp(`input [] [1] expected 1 actual ${findMedianSortedArrays([], [1])}`);
