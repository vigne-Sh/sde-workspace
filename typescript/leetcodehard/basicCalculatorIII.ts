/*
https://leetcode.com/problems/basic-calculator-iii/

status - completed
*/

import { logp } from "../utils/logger";

function calculate(s: string): number {
  let pos = 0;

  function peek(): string {
    return s[pos];
  }

  function parseExpression(): number {
    let result = parseTerm();

    while (pos < s.length && (peek() === "+" || peek() === "-")) {
      const op = peek();
      pos++;
      const term = parseTerm();
      result = op === "+" ? result + term : result - term;
    }

    return result;
  }

  function parseTerm(): number {
    let result = parseFactor();

    while (pos < s.length && (peek() === "*" || peek() === "/")) {
      const op = peek();
      pos++;
      const factor = parseFactor();
      result = op === "*" ? result * factor : Math.trunc(result / factor);
    }

    return result;
  }

  function parseFactor(): number {
    while (pos < s.length && peek() === " ") pos++;

    let sign = 1;
    if (peek() === "+" || peek() === "-") {
      sign = peek() === "-" ? -1 : 1;
      pos++;
      while (pos < s.length && peek() === " ") pos++;
    }

    let value: number;
    if (peek() === "(") {
      pos++;
      value = parseExpression();
      while (pos < s.length && peek() === " ") pos++;
      pos++;
    } else {
      let numStr = "";
      while (pos < s.length && /[0-9]/.test(peek())) {
        numStr += peek();
        pos++;
      }
      value = Number(numStr);
    }

    while (pos < s.length && peek() === " ") pos++;
    return sign * value;
  }

  return parseExpression();
}

logp(`input "1+1" expected 2 actual ${calculate("1+1")}`);
logp(`input "6-4/2" expected 4 actual ${calculate("6-4/2")}`);
logp(`input "2*(5+5*2)/3+(6/2+8)" expected 21 actual ${calculate("2*(5+5*2)/3+(6/2+8)")}`);
