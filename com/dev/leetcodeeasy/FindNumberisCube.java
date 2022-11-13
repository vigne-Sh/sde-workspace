/*
https://leetcode.com/problems/power-of-three/

status - completed
 */

package com.dev.leetcodeeasy;

import com.dev.logger.basePrinter;

import static java.lang.Math.*;


class FindIfCube extends basePrinter{

    static boolean isCube(int n){
        if (n <= 0)
            return false;
        return 1162261467 % n == 0;
    }

    public static void main(String[] args) {
        boolean out = isCube(9);
        logp("out is " + out);
    }
}