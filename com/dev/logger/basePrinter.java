package com.dev.logger;

public class basePrinter {

    public static void logp(String formatValue){
        System.out.println(formatValue);
    }

    public static void main(String[] args){
        logp("hey there " + 2);
    }
}
