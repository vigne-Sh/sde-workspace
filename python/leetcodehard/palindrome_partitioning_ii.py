"""
https://leetcode.com/problems/palindrome-partitioning-ii/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def minCut(self, s):
        n = len(s)
        is_palindrome = [[False] * n for _ in range(n)]

        for end in range(n):
            for start in range(end + 1):
                if s[start] == s[end] and (end - start <= 2 or is_palindrome[start + 1][end - 1]):
                    is_palindrome[start][end] = True

        cuts = [0] * n
        for end in range(n):
            if is_palindrome[0][end]:
                cuts[end] = 0
                continue

            best = end
            for start in range(1, end + 1):
                if is_palindrome[start][end]:
                    best = min(best, cuts[start - 1] + 1)
            cuts[end] = best

        return cuts[n - 1] if n else 0


if __name__ == "__main__":
    test_input_1 = "aab"
    output = Solution().minCut(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = "a"
    output = Solution().minCut(test_input_2)
    Solution().log.info("Output is %s", output)
