"""
https://leetcode.com/problems/course-schedule-iii/

status - completed
"""

import heapq

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def scheduleCourse(self, courses):
        courses.sort(key=lambda course: course[1])
        taken = []
        total_time = 0

        for duration, last_day in courses:
            heapq.heappush(taken, -duration)
            total_time += duration

            if total_time > last_day:
                longest = -heapq.heappop(taken)
                total_time -= longest

        return len(taken)


if __name__ == "__main__":
    test_input_1 = [[100, 200], [200, 1300], [1000, 1250], [2000, 3200]]
    output = Solution().scheduleCourse(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [[1, 2]]
    output = Solution().scheduleCourse(test_input_2)
    Solution().log.info("Output is %s", output)
