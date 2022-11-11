/*
https://practice.geeksforgeeks.org/problems/second-largest3735/1?page=1&difficulty[]=-2&sortBy=submissions

status => pending
 */

package com.dev.gfgproblems;

import com.dev.logger.basePrinter;

class SecondLargest extends basePrinter{

    static int print2Largest(int arr[], int n){
        logp("Array => " + arr);
        int temp = arr[0];
        int greater = 0;
        for(int i: arr){
            logp("temp is " + temp);
            if(temp > i){
                logp("hey");
            }
            temp = i;
            logp("Ite
        }
        return arr[0];
    }

    public static void main(String[] args){
        int[] arr = new int[]{4,8,11,33};
        print2Largest(arr, 3);
    }
}