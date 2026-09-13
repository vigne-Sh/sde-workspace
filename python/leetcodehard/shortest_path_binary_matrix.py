"""
https://leetcode.com/problems/shortest-path-in-binary-matrix/

status - completed
"""

from collections import deque

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def shortestPathBinaryMatrix(self, grid):
        n = len(grid)
        if not n or grid[0][0] != 0 or grid[n - 1][n - 1] != 0:
            return -1

        directions = [(dr, dc) for dr in (-1, 0, 1) for dc in (-1, 0, 1) if (dr, dc) != (0, 0)]
        queue = deque([(0, 0, 1)])
        visited = {(0, 0)}

        while queue:
            r, c, dist = queue.popleft()
            if r == n - 1 and c == n - 1:
                return dist

            for dr, dc in directions:
                nr, nc = r + dr, c + dc
                if 0 <= nr < n and 0 <= nc < n and (nr, nc) not in visited and grid[nr][nc] == 0:
                    visited.add((nr, nc))
                    queue.append((nr, nc, dist + 1))

        return -1


if __name__ == "__main__":
    test_input_1 = [[0, 1], [1, 0]]
    output = Solution().shortestPathBinaryMatrix(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [[0, 0, 0], [1, 1, 0], [1, 1, 0]]
    output = Solution().shortestPathBinaryMatrix(test_input_2)
    Solution().log.info("Output is %s", output)
