"""
https://leetcode.com/problems/word-break-ii/

status - completed
"""

from base_logger.logging_event import create_logger


class Solution:

    log = create_logger(__name__)

    def wordBreak(self, s, wordDict):
        word_set = set(wordDict)
        memo = {}
        return self.backtrack(s, word_set, memo)

    def backtrack(self, s, word_set, memo):
        if s in memo:
            return memo[s]
        if not s:
            return [""]

        results = []
        for end in range(1, len(s) + 1):
            prefix = s[:end]
            if prefix in word_set:
                rest_sentences = self.backtrack(s[end:], word_set, memo)
                for sentence in rest_sentences:
                    if sentence:
                        results.append(prefix + " " + sentence)
                    else:
                        results.append(prefix)

        memo[s] = results
        return results


if __name__ == "__main__":
    test_input_1 = "catsanddog"
    test_dict_1 = ["cat", "cats", "and", "sand", "dog"]
    output = Solution().wordBreak(test_input_1, test_dict_1)
    Solution().log.info("Output is %s", output)

    test_input_2 = "pineapplepenapple"
    test_dict_2 = ["apple", "pen", "applepen", "pine", "pineapple"]
    output = Solution().wordBreak(test_input_2, test_dict_2)
    Solution().log.info("Output is %s", output)
