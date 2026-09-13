"""
https://leetcode.com/problems/word-search-ii/

status - completed
"""

from base_logger.logging_event import create_logger


class TrieNode:

    def __init__(self):
        self.children = {}
        self.word = None


class Solution:

    log = create_logger(__name__)

    def findWords(self, board, words):
        root = TrieNode()
        for word in words:
            node = root
            for char in word:
                node = node.children.setdefault(char, TrieNode())
            node.word = word

        rows = len(board)
        cols = len(board[0]) if rows else 0
        results = []

        def backtrack(r, c, node):
            char = board[r][c]
            if char not in node.children:
                return

            next_node = node.children[char]
            if next_node.word is not None:
                results.append(next_node.word)
                next_node.word = None

            board[r][c] = "#"
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and board[nr][nc] != "#":
                    backtrack(nr, nc, next_node)
            board[r][c] = char

            if not next_node.children:
                del node.children[char]

        for r in range(rows):
            for c in range(cols):
                backtrack(r, c, root)

        return results


if __name__ == "__main__":
    test_board_1 = [
        ["o", "a", "a", "n"],
        ["e", "t", "a", "e"],
        ["i", "h", "k", "r"],
        ["i", "f", "l", "v"],
    ]
    test_words_1 = ["oath", "pea", "eat", "rain"]
    output = Solution().findWords(test_board_1, test_words_1)
    Solution().log.info("Output is %s", output)

    test_board_2 = [["a", "b"], ["c", "d"]]
    test_words_2 = ["abcb"]
    output = Solution().findWords(test_board_2, test_words_2)
    Solution().log.info("Output is %s", output)
