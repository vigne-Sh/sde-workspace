"""
https://leetcode.com/explore/learn/card/hash-table/183/combination-with-other-algorithms/1131/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def isHappy(self, n: int) -> bool:
        return self.filterValue(n, {})

    def filterValue(self, n, completed):
        if n == 1:
            return True
        if n in completed:
            return False
        completed[n] = 1
        str_value = str(n)
        splitted_list = list(str_value)
        splitted_list = list(map(int, splitted_list))
        temp_value = 0
        for item in splitted_list:
            temp_value += (item**2)
        return self.filterValue(temp_value, completed)


if __name__ == "__main__":
    test_input = 23
    output = Solution().isHappy(n=test_input)
    Solution().log.info("Output is %s", output)
