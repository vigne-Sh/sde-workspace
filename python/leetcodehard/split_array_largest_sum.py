"""
https://leetcode.com/problems/split-array-largest-sum/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def splitArray(self, nums, m):
        low, high = max(nums), sum(nums)

        while low < high:
            mid = (low + high) // 2
            if self.count_splits(nums, mid) <= m:
                high = mid
            else:
                low = mid + 1

        return low

    def count_splits(self, nums, max_sum):
        splits = 1
        current_sum = 0

        for num in nums:
            if current_sum + num > max_sum:
                splits += 1
                current_sum = num
            else:
                current_sum += num

        return splits


if __name__ == "__main__":
    test_input_1 = ([7, 2, 5, 10, 8], 2)
    output = Solution().splitArray(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ([1, 2, 3, 4, 5], 2)
    output = Solution().splitArray(*test_input_2)
    Solution().log.info("Output is %s", output)
