"""
https://leetcode.com/problems/basic-calculator/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def calculate(self, s):
        result = 0
        sign = 1
        number = 0
        stack = []

        for char in s:
            if char.isdigit():
                number = number * 10 + int(char)
            elif char in ("+", "-"):
                result += sign * number
                number = 0
                sign = 1 if char == "+" else -1
            elif char == "(":
                stack.append(result)
                stack.append(sign)
                result = 0
                sign = 1
            elif char == ")":
                result += sign * number
                number = 0
                result *= stack.pop()
                result += stack.pop()

        result += sign * number
        return result


if __name__ == "__main__":
    test_input_1 = "1 + 1"
    output = Solution().calculate(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = "(1+(4+5+2)-3)+(6+8)"
    output = Solution().calculate(test_input_2)
    Solution().log.info("Output is %s", output)
