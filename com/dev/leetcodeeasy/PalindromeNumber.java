/*
https://leetcode.com/problems/palindrome-number/

status - completed
 */

import com.dev.logger.basePrinter;


class PalindromeNumber extends basePrinter{

    static boolean isThisNumberPalindrome(int x){
        int summing =0, temp_value, iterate;

        // return false for `-` numbers
        if (x<0){
            logp("negative value detected...");
            return false;
        }

        temp_value = x;
        while(x>0){
            iterate = x%10;
            summing = (summing*10) + iterate;
            x = x/10;
        }

        if (temp_value == summing){
            return true;
        }
        else{
            return false;
        }
    }

    public static void main(String[] args) {
        int[] test_inputs = {121, -121, 0, 12, 10, 1001, 12221, 12231};
                for (int value: test_inputs) {
                    boolean final_out = isThisNumberPalindrome(value);
                    logp(value + " Palindrome or not " + final_out);
                }
    }
}