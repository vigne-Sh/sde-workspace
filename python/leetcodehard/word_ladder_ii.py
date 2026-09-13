"""
https://leetcode.com/problems/word-ladder-ii/

status - completed
"""

import string
from collections import deque

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def findLadders(self, beginWord, endWord, wordList):
        word_set = set(wordList)
        if endWord not in word_set:
            return []

        layers = {beginWord: [[beginWord]]}
        queue = deque([beginWord])
        word_set.discard(beginWord)
        found = False

        while queue and not found:
            next_words_used = set()
            for _ in range(len(queue)):
                word = queue.popleft()
                paths = layers[word]

                for i in range(len(word)):
                    for letter in string.ascii_lowercase:
                        if letter == word[i]:
                            continue
                        candidate = word[:i] + letter + word[i + 1:]

                        if candidate not in word_set:
                            continue

                        if candidate == endWord:
                            found = True

                        if candidate not in layers:
                            layers[candidate] = []
                            queue.append(candidate)

                        for path in paths:
                            layers[candidate].append(path + [candidate])

                        next_words_used.add(candidate)

            word_set -= next_words_used

        return layers.get(endWord, [])


if __name__ == "__main__":
    test_input_1 = ("hit", "cog", ["hot", "dot", "dog", "lot", "log", "cog"])
    output = Solution().findLadders(*test_input_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = ("hit", "cog", ["hot", "dot", "dog", "lot", "log"])
    output = Solution().findLadders(*test_input_2)
    Solution().log.info("Output is %s", output)
