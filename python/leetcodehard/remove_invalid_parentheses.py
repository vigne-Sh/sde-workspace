"""
https://leetcode.com/problems/remove-invalid-parentheses/

status - completed
"""

from collections import deque

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def removeInvalidParentheses(self, s):
        def is_valid(candidate):
            balance = 0
            for char in candidate:
                if char == "(":
                    balance += 1
                elif char == ")":
                    balance -= 1
                    if balance < 0:
                        return False
            return balance == 0

        visited = {s}
        queue = deque([s])
        results = []
        found = False

        while queue:
            current = queue.popleft()

            if is_valid(current):
                results.append(current)
                found = True

            if found:
                continue

            for i in range(len(current)):
                if current[i] not in ("(", ")"):
                    continue
                candidate = current[:i] + current[i + 1:]
                if candidate not in visited:
                    visited.add(candidate)
                    queue.append(candidate)

        return results if results else [""]


if __name__ == "__main__":
    test_input_1 = "()())()"
    output = Solution().removeInvalidParentheses(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = "(a)())()"
    output = Solution().removeInvalidParentheses(test_input_2)
    Solution().log.info("Output is %s", output)
