"""
https://leetcode.com/problems/burst-balloons/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def maxCoins(self, nums):
        balloons = [1] + nums + [1]
        n = len(balloons)
        dp = [[0] * n for _ in range(n)]

        for length in range(2, n):
            for left in range(0, n - length):
                right = left + length
                best = 0
                for k in range(left + 1, right):
                    coins = balloons[left] * balloons[k] * balloons[right]
                    coins += dp[left][k] + dp[k][right]
                    best = max(best, coins)
                dp[left][right] = best

        return dp[0][n - 1]


if __name__ == "__main__":
    test_input_1 = [3, 1, 5, 8]
    output = Solution().maxCoins(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [1, 5]
    output = Solution().maxCoins(test_input_2)
    Solution().log.info("Output is %s", output)
