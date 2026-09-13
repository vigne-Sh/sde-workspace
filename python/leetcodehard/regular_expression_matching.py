"""
https://leetcode.com/problems/regular-expression-matching/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def isMatch(self, s, p):
        memo = {}

        def dp(i, j):
            if (i, j) in memo:
                return memo[(i, j)]

            if j == len(p):
                result = i == len(s)
            else:
                first_match = i < len(s) and p[j] in (s[i], ".")

                if j + 1 < len(p) and p[j + 1] == "*":
                    result = dp(i, j + 2) or (first_match and dp(i + 1, j))
                else:
                    result = first_match and dp(i + 1, j + 1)

            memo[(i, j)] = result
            return result

        return dp(0, 0)


if __name__ == "__main__":
    test_input_1 = ("aa", "a*")
    output = Solution().isMatch(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("mississippi", "mis*is*p*.")
    output = Solution().isMatch(*test_input_2)
    Solution().log.info("Output is %s", output)
