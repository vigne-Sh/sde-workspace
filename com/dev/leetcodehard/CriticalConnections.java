/*
https://leetcode.com/problems/critical-connections-in-a-network/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.ArrayList;
import java.util.List;

class CriticalConnections extends basePrinter{

    static int timer;
    static List<List<Integer>> graph;
    static int[] disc;
    static int[] low;
    static boolean[] visited;
    static List<List<Integer>> result;

    static List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections){
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++){
            graph.add(new ArrayList<>());
        }
        for (List<Integer> conn : connections){
            int a = conn.get(0), b = conn.get(1);
            graph.get(a).add(b);
            graph.get(b).add(a);
        }

        disc = new int[n];
        low = new int[n];
        visited = new boolean[n];
        result = new ArrayList<>();
        timer = 0;

        for (int i = 0; i < n; i++){
            if (!visited[i]){
                dfs(i, -1);
            }
        }

        return result;
    }

    static void dfs(int node, int parent){
        visited[node] = true;
        disc[node] = low[node] = timer++;

        for (int neighbor : graph.get(node)){
            if (neighbor == parent){
                continue;
            }
            if (!visited[neighbor]){
                dfs(neighbor, node);
                low[node] = Math.min(low[node], low[neighbor]);
                if (low[neighbor] > disc[node]){
                    List<Integer> bridge = new ArrayList<>();
                    bridge.add(node);
                    bridge.add(neighbor);
                    result.add(bridge);
                }
            }
            else{
                low[node] = Math.min(low[node], disc[neighbor]);
            }
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> connections1 = new ArrayList<>();
        connections1.add(List.of(0,1));
        connections1.add(List.of(1,2));
        connections1.add(List.of(2,0));
        connections1.add(List.of(1,3));
        logp("input n=4 connections=[[0,1],[1,2],[2,0],[1,3]] expected [[1,3]] actual " + criticalConnections(4, connections1));

        List<List<Integer>> connections2 = new ArrayList<>();
        connections2.add(List.of(0,1));
        connections2.add(List.of(0,2));
        connections2.add(List.of(1,2));
        logp("input n=3 connections=[[0,1],[0,2],[1,2]] expected [] actual " + criticalConnections(3, connections2));
    }
}
