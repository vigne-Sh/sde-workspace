"""
https://leetcode.com/problems/longest-increasing-path-in-a-matrix/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def longestIncreasingPath(self, matrix):
        if not matrix or not matrix[0]:
            return 0

        rows = len(matrix)
        cols = len(matrix[0])
        memo = [[0] * cols for _ in range(rows)]

        def dfs(r, c):
            if memo[r][c]:
                return memo[r][c]

            best = 1
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and matrix[nr][nc] > matrix[r][c]:
                    best = max(best, 1 + dfs(nr, nc))

            memo[r][c] = best
            return best

        longest = 0
        for r in range(rows):
            for c in range(cols):
                longest = max(longest, dfs(r, c))

        return longest


if __name__ == "__main__":
    test_input_1 = [[9, 9, 4], [6, 6, 8], [2, 1, 1]]
    output = Solution().longestIncreasingPath(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [[3, 4, 5], [3, 2, 6], [2, 2, 1]]
    output = Solution().longestIncreasingPath(test_input_2)
    Solution().log.info("Output is %s", output)
