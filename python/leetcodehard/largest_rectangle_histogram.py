"""
https://leetcode.com/problems/largest-rectangle-in-histogram/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def largestRectangleArea(self, heights):
        stack = []
        max_area = 0

        for i, height in enumerate(heights + [0]):
            while stack and heights[stack[-1]] >= height:
                top = stack.pop()
                width = i if not stack else i - stack[-1] - 1
                max_area = max(max_area, heights[top] * width)
            stack.append(i)

        return max_area


if __name__ == "__main__":
    test_input_1 = [2, 1, 5, 6, 2, 3]
    output = Solution().largestRectangleArea(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [2, 4]
    output = Solution().largestRectangleArea(test_input_2)
    Solution().log.info("Output is %s", output)
