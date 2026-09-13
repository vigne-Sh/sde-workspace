/*
https://leetcode.com/problems/remove-invalid-parentheses/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class RemoveInvalidParentheses extends basePrinter{

    static List<String> removeInvalidParentheses(String s){
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(s);
        visited.add(s);
        boolean found = false;

        while (!queue.isEmpty()){
            String curr = queue.poll();

            if (isValid(curr)){
                result.add(curr);
                found = true;
            }

            if (found){
                continue;
            }

            for (int i = 0; i < curr.length(); i++){
                char c = curr.charAt(i);
                if (c != '(' && c != ')'){
                    continue;
                }
                String next = curr.substring(0, i) + curr.substring(i + 1);
                if (!visited.contains(next)){
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return result;
    }

    static boolean isValid(String s){
        int count = 0;
        for (char c : s.toCharArray()){
            if (c == '(') count++;
            else if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    public static void main(String[] args) {
        List<String> result1 = removeInvalidParentheses("()())()");
        logp("input ()())() expected [(())(), ()()()] (order may vary) actual " + result1);

        List<String> result2 = removeInvalidParentheses("(a)())()");
        logp("input (a)())() expected [(a())(), (a)()()] (order may vary) actual " + result2);

        List<String> result3 = removeInvalidParentheses(")(");
        logp("input )( expected [] actual " + result3);
    }
}
