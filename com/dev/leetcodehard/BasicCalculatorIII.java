/*
https://leetcode.com/problems/basic-calculator-iii/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

class BasicCalculatorIII extends basePrinter{

    static int pos;
    static String expr;

    static int calculate(String s){
        expr = s.replaceAll("\\s+", "");
        pos = 0;
        return parseExpression();
    }

    static int parseExpression(){
        int result = parseTerm();
        while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')){
            char op = expr.charAt(pos);
            pos++;
            int term = parseTerm();
            if (op == '+'){
                result += term;
            }
            else{
                result -= term;
            }
        }
        return result;
    }

    static int parseTerm(){
        int result = parseFactor();
        while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')){
            char op = expr.charAt(pos);
            pos++;
            int factor = parseFactor();
            if (op == '*'){
                result *= factor;
            }
            else{
                result /= factor;
            }
        }
        return result;
    }

    static int parseFactor(){
        if (expr.charAt(pos) == '-'){
            pos++;
            return -parseFactor();
        }
        if (expr.charAt(pos) == '+'){
            pos++;
            return parseFactor();
        }
        if (expr.charAt(pos) == '('){
            pos++;
            int result = parseExpression();
            pos++;
            return result;
        }

        int start = pos;
        while (pos < expr.length() && Character.isDigit(expr.charAt(pos))){
            pos++;
        }
        return Integer.parseInt(expr.substring(start, pos));
    }

    public static void main(String[] args) {
        logp("input 1+1 expected 2 actual " + calculate("1+1"));
        logp("input 6-4/2 expected 4 actual " + calculate("6-4/2"));
        logp("input 2*(5+5*2)/3+(6/2+8) expected 21 actual " + calculate("2*(5+5*2)/3+(6/2+8)"));
        logp("input (2+6*3+5-(3*14/7+2)*5)+3 expected -12 actual " + calculate("(2+6*3+5-(3*14/7+2)*5)+3"));
    }
}
