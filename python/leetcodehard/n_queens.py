"""
https://leetcode.com/problems/n-queens/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def solveNQueens(self, n):
        results = []
        columns = set()
        diagonals = set()
        anti_diagonals = set()
        placement = []

        def backtrack(row):
            if row == n:
                board = []
                for col in placement:
                    row_str = "." * col + "Q" + "." * (n - col - 1)
                    board.append(row_str)
                results.append(board)
                return

            for col in range(n):
                if col in columns or (row - col) in diagonals or (row + col) in anti_diagonals:
                    continue

                columns.add(col)
                diagonals.add(row - col)
                anti_diagonals.add(row + col)
                placement.append(col)

                backtrack(row + 1)

                columns.remove(col)
                diagonals.remove(row - col)
                anti_diagonals.remove(row + col)
                placement.pop()

        backtrack(0)
        return results


if __name__ == "__main__":
    test_input_1 = 4
    output = Solution().solveNQueens(test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = 1
    output = Solution().solveNQueens(test_input_2)
    Solution().log.info("Output is %s", output)
