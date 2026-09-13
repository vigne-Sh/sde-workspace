"""
https://leetcode.com/problems/merge-k-sorted-lists/

status - completed
"""

import heapq

from base_logger.logging_event import create_logger


class ListNode:

    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:

    log = create_logger(__name__)

    def mergeKLists(self, lists):
        heap = []
        for index, node in enumerate(lists):
            if node:
                heapq.heappush(heap, (node.val, index, node))

        dummy = ListNode()
        current = dummy

        while heap:
            val, index, node = heapq.heappop(heap)
            current.next = node
            current = current.next
            if node.next:
                heapq.heappush(heap, (node.next.val, index, node.next))

        return dummy.next

    def build_list(self, values):
        dummy = ListNode()
        current = dummy
        for value in values:
            current.next = ListNode(value)
            current = current.next
        return dummy.next

    def list_to_values(self, node):
        values = []
        while node:
            values.append(node.val)
            node = node.next
        return values


if __name__ == "__main__":
    solution = Solution()
    test_input_1 = [
        solution.build_list([1, 4, 5]),
        solution.build_list([1, 3, 4]),
        solution.build_list([2, 6]),
    ]
    merged = solution.mergeKLists(test_input_1)
    output = solution.list_to_values(merged)
    solution.log.info("Output is %s", output)

    test_input_2 = [solution.build_list([]), solution.build_list([])]
    merged = solution.mergeKLists(test_input_2)
    output = solution.list_to_values(merged)
    solution.log.info("Output is %s", output)
