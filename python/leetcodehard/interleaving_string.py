"""
https://leetcode.com/problems/interleaving-string/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def isInterleave(self, s1, s2, s3):
        if len(s1) + len(s2) != len(s3):
            return False

        rows = len(s1)
        cols = len(s2)
        dp = [[False] * (cols + 1) for _ in range(rows + 1)]
        dp[0][0] = True

        for i in range(1, rows + 1):
            dp[i][0] = dp[i - 1][0] and s1[i - 1] == s3[i - 1]

        for j in range(1, cols + 1):
            dp[0][j] = dp[0][j - 1] and s2[j - 1] == s3[j - 1]

        for i in range(1, rows + 1):
            for j in range(1, cols + 1):
                take_s1 = dp[i - 1][j] and s1[i - 1] == s3[i + j - 1]
                take_s2 = dp[i][j - 1] and s2[j - 1] == s3[i + j - 1]
                dp[i][j] = take_s1 or take_s2

        return dp[rows][cols]


if __name__ == "__main__":
    test_input_1 = ("aabcc", "dbbca", "aadbbcbcac")
    output = Solution().isInterleave(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("aabcc", "dbbca", "aadbbbaccc")
    output = Solution().isInterleave(*test_input_2)
    Solution().log.info("Output is %s", output)
