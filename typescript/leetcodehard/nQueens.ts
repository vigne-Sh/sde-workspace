/*
https://leetcode.com/problems/n-queens-ii/

status - completed
*/

import { logp } from "../utils/logger";

function totalNQueens(n: number): number {
  const cols = new Set<number>();
  const diag1 = new Set<number>();
  const diag2 = new Set<number>();
  let count = 0;

  function backtrack(row: number): void {
    if (row === n) {
      count++;
      return;
    }

    for (let col = 0; col < n; col++) {
      const d1 = row - col;
      const d2 = row + col;
      if (cols.has(col) || diag1.has(d1) || diag2.has(d2)) continue;

      cols.add(col);
      diag1.add(d1);
      diag2.add(d2);

      backtrack(row + 1);

      cols.delete(col);
      diag1.delete(d1);
      diag2.delete(d2);
    }
  }

  backtrack(0);
  return count;
}

logp(`input n=4 expected 2 actual ${totalNQueens(4)}`);
logp(`input n=1 expected 1 actual ${totalNQueens(1)}`);
logp(`input n=8 expected 92 actual ${totalNQueens(8)}`);
