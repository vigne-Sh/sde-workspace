/*
https://leetcode.com/problems/remove-invalid-parentheses/

status - completed
*/

import { logp } from "../utils/logger";

function isValid(s: string): boolean {
  let balance = 0;
  for (const ch of s) {
    if (ch === "(") balance++;
    else if (ch === ")") balance--;
    if (balance < 0) return false;
  }
  return balance === 0;
}

function removeInvalidParentheses(s: string): string[] {
  const visited = new Set<string>([s]);
  let queue = [s];
  const result: string[] = [];
  let found = false;

  while (queue.length > 0 && !found) {
    const nextQueue: string[] = [];

    for (const candidate of queue) {
      if (isValid(candidate)) {
        result.push(candidate);
        found = true;
        continue;
      }

      for (let i = 0; i < candidate.length; i++) {
        if (candidate[i] !== "(" && candidate[i] !== ")") continue;
        const next = candidate.slice(0, i) + candidate.slice(i + 1);
        if (!visited.has(next)) {
          visited.add(next);
          nextQueue.push(next);
        }
      }
    }

    queue = nextQueue;
  }

  return result;
}

logp(
  `input "()())" expected [()(), (())] (order may vary) actual ${JSON.stringify(
    removeInvalidParentheses("()())").sort(),
  )}`,
);
logp(`input "(a)())()" expected [(a)()(), (a())()] (order may vary) actual ${JSON.stringify(
  removeInvalidParentheses("(a)())()").sort(),
)}`);
logp(`input ")(" expected [""] actual ${JSON.stringify(removeInvalidParentheses(")("))}`);
