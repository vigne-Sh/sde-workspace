/*
https://leetcode.com/explore/learn/card/hash-table/183/combination-with-other-algorithms/1105/

status - completed
 */

import static java.lang.Math.*;

import com.dev.logger.basePrinter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


class IntersectTwoArray extends basePrinter{

    static int[] getIntersectedValue(int[] arr1, int[] arr2){
        Set<Integer> finalValue = new LinkedHashSet<Integer>();
        int count = 0;

        for (int i=0; i<arr1.length; i++){
            logp("value i => " + arr1[i]);
            for (int j=0; j<arr2.length; j++){
                logp("value j =>" + arr2[j]);
                if (arr1[i] == arr2[j] && ! finalValue.contains(arr2[j])) {
                    logp("match is " + arr2[j]);
                    finalValue.add(arr2[j]);
                    count += 1;
                }
            }
        }
        int[] b = new int[count];
        int counter = 0;
       for (int z:finalValue){
           b[counter++] = z;
       }
        logp("value is " + finalValue);
    return b;
    }

    public static void main(String[] args) {
        int[] arr1 = {61,24,20,58,95,53,17,32,45,85,70,20,83,62,35,89,5,95,12,86,58,77,30,64,46,13,5,92,67,40,20,38,31,18,89,85,7,30,67,34,62,35,47,98,3,41,53,26,66,40,54,44,57,46,70,60,4,63,82,42,65,59,17,98,29,72,1,96,82,66,98,6,92,31,43,81,88,60,10,55,66,82,0,79,11,81};
        int[] arr2 = {5,25,4,39,57,49,93,79,7,8,49,89,2,7,73,88,45,15,34,92,84,38,85,34,16,6,99,0,2,36,68,52,73,50,77,44,61,48};
        getIntersectedValue(arr1, arr2);
    }
}