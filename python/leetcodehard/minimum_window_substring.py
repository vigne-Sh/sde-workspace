"""
https://leetcode.com/problems/minimum-window-substring/

status - completed
"""

from collections import Counter

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def minWindow(self, s, t):
        if not s or not t:
            return ""

        need = Counter(t)
        missing = len(t)
        left = 0
        best_left, best_right = 0, 0

        for right, char in enumerate(s, 1):
            if need[char] > 0:
                missing -= 1
            need[char] -= 1

            if missing == 0:
                while need[s[left]] < 0:
                    need[s[left]] += 1
                    left += 1

                if best_right == 0 or right - left < best_right - best_left:
                    best_left, best_right = left, right

        return s[best_left:best_right]


if __name__ == "__main__":
    test_input_1 = ("ADOBECODEBANC", "ABC")
    output = Solution().minWindow(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("a", "a")
    output = Solution().minWindow(*test_input_2)
    Solution().log.info("Output is %s", output)
