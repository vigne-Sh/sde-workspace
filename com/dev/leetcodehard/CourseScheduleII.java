/*
https://leetcode.com/problems/course-schedule-ii/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CourseScheduleII extends basePrinter{

    static int[] findOrder(int numCourses, int[][] prerequisites){
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++){
            graph.add(new ArrayList<>());
        }

        for (int[] p : prerequisites){
            int course = p[0];
            int pre = p[1];
            graph.get(pre).add(course);
            inDegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++){
            if (inDegree[i] == 0){
                queue.add(i);
            }
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()){
            int course = queue.poll();
            order[idx++] = course;
            for (int next : graph.get(course)){
                inDegree[next]--;
                if (inDegree[next] == 0){
                    queue.add(next);
                }
            }
        }

        if (idx != numCourses){
            return new int[0];
        }

        return order;
    }

    public static void main(String[] args) {
        int[][] prereq1 = {{1,0}};
        logp("input numCourses=2 prereq=[[1,0]] expected [0,1] actual " + arrToString(findOrder(2, prereq1)));

        int[][] prereq2 = {{1,0},{2,0},{3,1},{3,2}};
        logp("input numCourses=4 prereq=[[1,0],[2,0],[3,1],[3,2]] expected order starting with 0 then 3 last actual " + arrToString(findOrder(4, prereq2)));

        int[][] prereq3 = {{1,0},{0,1}};
        logp("input numCourses=2 prereq=[[1,0],[0,1]] (cycle) expected [] actual " + arrToString(findOrder(2, prereq3)));
    }

    static String arrToString(int[] arr){
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++){
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
