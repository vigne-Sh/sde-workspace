/*
https://leetcode.com/problems/critical-connections-in-a-network/

status - completed
*/

import { logp } from "../utils/logger";

function criticalConnections(n: number, connections: number[][]): number[][] {
  const graph: number[][] = Array.from({ length: n }, () => []);
  for (const [a, b] of connections) {
    graph[a].push(b);
    graph[b].push(a);
  }

  const disc = new Array(n).fill(-1);
  const low = new Array(n).fill(-1);
  const bridges: number[][] = [];
  let timer = 0;

  function dfs(node: number, parent: number): void {
    disc[node] = timer;
    low[node] = timer;
    timer++;

    for (const neighbor of graph[node]) {
      if (neighbor === parent) continue;

      if (disc[neighbor] === -1) {
        dfs(neighbor, node);
        low[node] = Math.min(low[node], low[neighbor]);
        if (low[neighbor] > disc[node]) {
          bridges.push([node, neighbor]);
        }
      } else {
        low[node] = Math.min(low[node], disc[neighbor]);
      }
    }
  }

  for (let i = 0; i < n; i++) {
    if (disc[i] === -1) dfs(i, -1);
  }

  return bridges;
}

logp(
  `input n=4 connections=[[0,1],[1,2],[2,0],[1,3]] expected [[1,3]] actual ${JSON.stringify(
    criticalConnections(4, [[0, 1], [1, 2], [2, 0], [1, 3]]),
  )}`,
);
logp(`input n=2 connections=[[0,1]] expected [[0,1]] actual ${JSON.stringify(criticalConnections(2, [[0, 1]]))}`);
