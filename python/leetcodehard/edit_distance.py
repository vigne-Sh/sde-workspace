"""
https://leetcode.com/problems/edit-distance/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def minDistance(self, word1, word2):
        rows = len(word1)
        cols = len(word2)
        dp = [[0] * (cols + 1) for _ in range(rows + 1)]

        for i in range(rows + 1):
            dp[i][0] = i
        for j in range(cols + 1):
            dp[0][j] = j

        for i in range(1, rows + 1):
            for j in range(1, cols + 1):
                if word1[i - 1] == word2[j - 1]:
                    dp[i][j] = dp[i - 1][j - 1]
                else:
                    dp[i][j] = 1 + min(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])

        return dp[rows][cols]


if __name__ == "__main__":
    test_input_1 = ("horse", "ros")
    output = Solution().minDistance(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("intention", "execution")
    output = Solution().minDistance(*test_input_2)
    Solution().log.info("Output is %s", output)
