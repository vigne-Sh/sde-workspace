"""
https://leetcode.com/problems/distinct-subsequences/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def numDistinct(self, s, t):
        rows = len(s)
        cols = len(t)
        dp = [[0] * (cols + 1) for _ in range(rows + 1)]

        for i in range(rows + 1):
            dp[i][0] = 1

        for i in range(1, rows + 1):
            for j in range(1, cols + 1):
                dp[i][j] = dp[i - 1][j]
                if s[i - 1] == t[j - 1]:
                    dp[i][j] += dp[i - 1][j - 1]

        return dp[rows][cols]


if __name__ == "__main__":
    test_input_1 = ("rabbbit", "rabbit")
    output = Solution().numDistinct(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("babgbag", "bag")
    output = Solution().numDistinct(*test_input_2)
    Solution().log.info("Output is %s", output)
