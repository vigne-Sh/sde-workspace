/*
https://practice.geeksforgeeks.org/problems/primes-sum5827/1

status => completed
 */

package com.dev.gfgproblems;

import com.dev.logger.basePrinter;

import java.io.DataInput;
import java.util.Arrays;

public class SumOfTwoPrime extends basePrinter{

    static String isSumOfTwo(int N){
        for(int i=2; i <= N/2; i++) {
            logp("i is " + i + " n is " + N);
            if (isPrime(i) && isPrime(N - i)) {
                    logp(N + "=" + i + " +" + (N-i));
                    return "Yes";
                }
        }

        return "No";
    }

    static boolean isPrime(int n){
        if (n == 0 || n == 1){
            return false;
        }

        for(int i=2;i <= Math.sqrt(n); i++){
            logp("i =>" + i);
            if(n%i == 0){
                logp("is " + n + " prime " + n%i + " No");
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        int[] validatingInputs = {7, 4, 8, 34, 34, 222, 331, 234, 33, 22, 10, 56, 31, 6977};
        for (int i: validatingInputs){
            String value = isSumOfTwo(i);
            logp("Parsing input " + i + " => " + value);
        }

    }
}
