"""
https://leetcode.com/problems/sudoku-solver/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def solveSudoku(self, board):
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]
        empty_cells = []

        for r in range(9):
            for c in range(9):
                value = board[r][c]
                if value == ".":
                    empty_cells.append((r, c))
                else:
                    rows[r].add(value)
                    cols[c].add(value)
                    boxes[self.box_index(r, c)].add(value)

        self.backtrack(board, empty_cells, 0, rows, cols, boxes)

    def box_index(self, r, c):
        return (r // 3) * 3 + (c // 3)

    def backtrack(self, board, empty_cells, pos, rows, cols, boxes):
        if pos == len(empty_cells):
            return True

        r, c = empty_cells[pos]
        box = self.box_index(r, c)

        for digit in "123456789":
            if digit in rows[r] or digit in cols[c] or digit in boxes[box]:
                continue

            board[r][c] = digit
            rows[r].add(digit)
            cols[c].add(digit)
            boxes[box].add(digit)

            if self.backtrack(board, empty_cells, pos + 1, rows, cols, boxes):
                return True

            board[r][c] = "."
            rows[r].remove(digit)
            cols[c].remove(digit)
            boxes[box].remove(digit)

        return False


if __name__ == "__main__":
    test_input_1 = [
        ["5", "3", ".", ".", "7", ".", ".", ".", "."],
        ["6", ".", ".", "1", "9", "5", ".", ".", "."],
        [".", "9", "8", ".", ".", ".", ".", "6", "."],
        ["8", ".", ".", ".", "6", ".", ".", ".", "3"],
        ["4", ".", ".", "8", ".", "3", ".", ".", "1"],
        ["7", ".", ".", ".", "2", ".", ".", ".", "6"],
        [".", "6", ".", ".", ".", ".", "2", "8", "."],
        [".", ".", ".", "4", "1", "9", ".", ".", "5"],
        [".", ".", ".", ".", "8", ".", ".", "7", "9"],
    ]
    Solution().solveSudoku(test_input_1)
    Solution().log.info("Output is %s", test_input_1)

    test_input_2 = [
        ["."] * 9,
        [".", ".", ".", ".", ".", ".", ".", "3", "."],
        [".", ".", "2", ".", ".", ".", ".", ".", "."],
        [".", ".", ".", ".", ".", ".", "5", ".", "4"],
        [".", ".", ".", ".", ".", "1", ".", ".", "."],
        [".", "4", ".", ".", ".", ".", ".", ".", "."],
        [".", ".", ".", "1", ".", ".", ".", ".", "."],
        [".", ".", ".", ".", ".", ".", ".", ".", "."],
        [".", ".", ".", ".", ".", ".", ".", ".", "."],
    ]
    Solution().solveSudoku(test_input_2)
    Solution().log.info("Output is %s", test_input_2)
