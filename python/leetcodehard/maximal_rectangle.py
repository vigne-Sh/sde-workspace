"""
https://leetcode.com/problems/maximal-rectangle/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def maximalRectangle(self, matrix):
        if not matrix or not matrix[0]:
            return 0

        cols = len(matrix[0])
        heights = [0] * cols
        max_area = 0

        for row in matrix:
            for c in range(cols):
                heights[c] = heights[c] + 1 if row[c] == "1" else 0
            max_area = max(max_area, self.largestRectangleArea(heights))

        return max_area

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
    test_input_1 = [
        ["1", "0", "1", "0", "0"],
        ["1", "0", "1", "1", "1"],
        ["1", "1", "1", "1", "1"],
        ["1", "0", "0", "1", "0"],
    ]
    output = Solution().maximalRectangle(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [["0"]]
    output = Solution().maximalRectangle(test_input_2)
    Solution().log.info("Output is %s", output)
