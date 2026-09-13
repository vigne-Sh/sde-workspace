"""
https://leetcode.com/problems/wildcard-matching/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def isMatch(self, s, p):
        rows = len(s)
        cols = len(p)
        dp = [[False] * (cols + 1) for _ in range(rows + 1)]
        dp[0][0] = True

        for j in range(1, cols + 1):
            if p[j - 1] == "*":
                dp[0][j] = dp[0][j - 1]

        for i in range(1, rows + 1):
            for j in range(1, cols + 1):
                if p[j - 1] == "*":
                    dp[i][j] = dp[i - 1][j] or dp[i][j - 1]
                elif p[j - 1] == "?" or p[j - 1] == s[i - 1]:
                    dp[i][j] = dp[i - 1][j - 1]

        return dp[rows][cols]


if __name__ == "__main__":
    test_input_1 = ("aa", "*")
    output = Solution().isMatch(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("cb", "?a")
    output = Solution().isMatch(*test_input_2)
    Solution().log.info("Output is %s", output)
