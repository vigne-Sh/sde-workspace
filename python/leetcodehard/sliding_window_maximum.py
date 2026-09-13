"""
https://leetcode.com/problems/sliding-window-maximum/

status - completed
"""

from collections import deque

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def maxSlidingWindow(self, nums, k):
        window = deque()
        result = []

        for i, num in enumerate(nums):
            while window and nums[window[-1]] < num:
                window.pop()
            window.append(i)

            if window[0] <= i - k:
                window.popleft()

            if i >= k - 1:
                result.append(nums[window[0]])

        return result


if __name__ == "__main__":
    test_input_1 = ([1, 3, -1, -3, 5, 3, 6, 7], 3)
    output = Solution().maxSlidingWindow(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ([1], 1)
    output = Solution().maxSlidingWindow(*test_input_2)
    Solution().log.info("Output is %s", output)
