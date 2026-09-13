"""
https://leetcode.com/problems/longest-valid-parentheses/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def longestValidParentheses(self, s):
        max_length = 0
        stack = [-1]

        for index, char in enumerate(s):
            if char == "(":
                stack.append(index)
            else:
                stack.pop()
                if not stack:
                    stack.append(index)
                else:
                    max_length = max(max_length, index - stack[-1])

        return max_length


if __name__ == "__main__":
    test_input_1 = "(()"
    output = Solution().longestValidParentheses(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ")()())"
    output = Solution().longestValidParentheses(test_input_2)
    Solution().log.info("Output is %s", output)
