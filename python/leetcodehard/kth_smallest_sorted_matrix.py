"""
https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def kthSmallest(self, matrix, k):
        n = len(matrix)
        low, high = matrix[0][0], matrix[n - 1][n - 1]

        while low < high:
            mid = (low + high) // 2
            count = self.count_less_equal(matrix, mid, n)

            if count < k:
                low = mid + 1
            else:
                high = mid

        return low

    def count_less_equal(self, matrix, value, n):
        count = 0
        row = n - 1
        col = 0

        while row >= 0 and col < n:
            if matrix[row][col] <= value:
                count += row + 1
                col += 1
            else:
                row -= 1

        return count


if __name__ == "__main__":
    test_input_1 = ([[1, 5, 9], [10, 11, 13], [12, 13, 15]], 8)
    output = Solution().kthSmallest(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ([[-5]], 1)
    output = Solution().kthSmallest(*test_input_2)
    Solution().log.info("Output is %s", output)
