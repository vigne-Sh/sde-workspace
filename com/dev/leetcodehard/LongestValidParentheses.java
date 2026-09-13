/*
https://leetcode.com/problems/longest-valid-parentheses/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.Stack;

class LongestValidParentheses extends basePrinter{

    static int longestValidParentheses(String s){
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (c == '('){
                stack.push(i);
            }
            else{
                stack.pop();
                if (stack.isEmpty()){
                    stack.push(i);
                }
                else{
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }

    public static void main(String[] args) {
        logp("input (() expected 2 actual " + longestValidParentheses("(()"));
        logp("input )()()) expected 4 actual " + longestValidParentheses(")()())"));
        logp("input (empty) expected 0 actual " + longestValidParentheses(""));
        logp("input ()(()) expected 6 actual " + longestValidParentheses("()(())"));
    }
}
