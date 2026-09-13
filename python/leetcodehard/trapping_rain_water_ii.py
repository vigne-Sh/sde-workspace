"""
https://leetcode.com/problems/trapping-rain-water-ii/

status - completed
"""

import heapq

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def trapRainWater(self, heightMap):
        if not heightMap or not heightMap[0]:
            return 0

        rows = len(heightMap)
        cols = len(heightMap[0])
        visited = [[False] * cols for _ in range(rows)]
        heap = []

        for r in range(rows):
            for c in range(cols):
                if r == 0 or r == rows - 1 or c == 0 or c == cols - 1:
                    heapq.heappush(heap, (heightMap[r][c], r, c))
                    visited[r][c] = True

        total_water = 0
        while heap:
            height, r, c = heapq.heappop(heap)
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and not visited[nr][nc]:
                    visited[nr][nc] = True
                    total_water += max(0, height - heightMap[nr][nc])
                    heapq.heappush(heap, (max(height, heightMap[nr][nc]), nr, nc))

        return total_water


if __name__ == "__main__":
    test_input_1 = [
        [1, 4, 3, 1, 3, 2],
        [3, 2, 1, 3, 2, 4],
        [2, 3, 3, 2, 3, 1],
    ]
    output = Solution().trapRainWater(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = [
        [3, 3, 3, 3, 3],
        [3, 2, 2, 2, 3],
        [3, 2, 1, 2, 3],
        [3, 2, 2, 2, 3],
        [3, 3, 3, 3, 3],
    ]
    output = Solution().trapRainWater(test_input_2)
    Solution().log.info("Output is %s", output)
