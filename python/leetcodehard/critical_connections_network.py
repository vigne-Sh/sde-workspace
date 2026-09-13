"""
https://leetcode.com/problems/critical-connections-in-a-network/

status - completed
"""

from collections import defaultdict

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def criticalConnections(self, n, connections):
        graph = defaultdict(list)
        for a, b in connections:
            graph[a].append(b)
            graph[b].append(a)

        discovery = [-1] * n
        low = [0] * n
        bridges = []
        self.timer = 0

        def dfs(node, parent):
            discovery[node] = low[node] = self.timer
            self.timer += 1

            for neighbor in graph[node]:
                if neighbor == parent:
                    continue
                if discovery[neighbor] == -1:
                    dfs(neighbor, node)
                    low[node] = min(low[node], low[neighbor])
                    if low[neighbor] > discovery[node]:
                        bridges.append([node, neighbor])
                else:
                    low[node] = min(low[node], discovery[neighbor])

        for node in range(n):
            if discovery[node] == -1:
                dfs(node, -1)

        return bridges


if __name__ == "__main__":
    test_input_1 = (4, [[0, 1], [1, 2], [2, 0], [1, 3]])
    output = Solution().criticalConnections(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = (2, [[0, 1]])
    output = Solution().criticalConnections(*test_input_2)
    Solution().log.info("Output is %s", output)
