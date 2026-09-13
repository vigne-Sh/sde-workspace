"""
https://leetcode.com/problems/alien-dictionary/

status - completed
"""

from collections import defaultdict, deque

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def alienOrder(self, words):
        graph = defaultdict(set)
        in_degree = {char: 0 for word in words for char in word}

        for first, second in zip(words, words[1:]):
            min_len = min(len(first), len(second))
            if first[:min_len] == second[:min_len] and len(first) > len(second):
                return ""

            for a, b in zip(first, second):
                if a != b:
                    if b not in graph[a]:
                        graph[a].add(b)
                        in_degree[b] += 1
                    break

        queue = deque([char for char in in_degree if in_degree[char] == 0])
        order = []

        while queue:
            char = queue.popleft()
            order.append(char)
            for neighbor in graph[char]:
                in_degree[neighbor] -= 1
                if in_degree[neighbor] == 0:
                    queue.append(neighbor)

        if len(order) != len(in_degree):
            return ""

        return "".join(order)


if __name__ == "__main__":
    test_input_1 = ["wrt", "wrf", "er", "ett", "rftt"]
    output = Solution().alienOrder(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ["z", "x"]
    output = Solution().alienOrder(test_input_2)
    Solution().log.info("Output is %s", output)
