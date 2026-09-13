/*
https://leetcode.com/problems/course-schedule-ii/

status - completed
*/

import { logp } from "../utils/logger";

function findOrder(numCourses: number, prerequisites: number[][]): number[] {
  const graph: number[][] = Array.from({ length: numCourses }, () => []);
  const indegree = new Array(numCourses).fill(0);

  for (const [course, prereq] of prerequisites) {
    graph[prereq].push(course);
    indegree[course]++;
  }

  const queue: number[] = [];
  for (let i = 0; i < numCourses; i++) {
    if (indegree[i] === 0) queue.push(i);
  }

  const order: number[] = [];
  while (queue.length > 0) {
    const course = queue.shift()!;
    order.push(course);
    for (const next of graph[course]) {
      indegree[next]--;
      if (indegree[next] === 0) queue.push(next);
    }
  }

  return order.length === numCourses ? order : [];
}

logp(
  `input numCourses=2 prereq=[[1,0]] expected [0,1] actual ${JSON.stringify(
    findOrder(2, [[1, 0]]),
  )}`,
);
logp(
  `input numCourses=4 prereq=[[1,0],[2,0],[3,1],[3,2]] expected valid order actual ${JSON.stringify(
    findOrder(4, [[1, 0], [2, 0], [3, 1], [3, 2]]),
  )}`,
);
logp(`input numCourses=2 prereq=[[1,0],[0,1]] expected [] actual ${JSON.stringify(findOrder(2, [[1, 0], [0, 1]]))}`);
