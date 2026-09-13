"""
Trie-based prefix autocomplete. Words are inserted into a trie with
frequency counts at terminal nodes, and lookups walk to the prefix
node then collect the top matches ranked by frequency then
alphabetically.

status - completed
"""

from base_logger.logging_event import create_logger


class TrieNode:

    def __init__(self):
        self.children = {}
        self.is_word = False
        self.frequency = 0


class TrieAutocomplete:

    log = create_logger(__name__)

    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str, frequency: int = 1):
        node = self.root
        for ch in word:
            node = node.children.setdefault(ch, TrieNode())
        node.is_word = True
        node.frequency += frequency

    def _collect_words(self, node, prefix, results):
        if node.is_word:
            results.append((prefix, node.frequency))
        for ch, child in node.children.items():
            self._collect_words(child, prefix + ch, results)

    def top_matches(self, prefix: str, k: int = 5):
        node = self.root
        for ch in prefix:
            if ch not in node.children:
                return []
            node = node.children[ch]

        results = []
        self._collect_words(node, prefix, results)
        results.sort(key=lambda pair: (-pair[1], pair[0]))
        return [word for word, _ in results[:k]]


if __name__ == "__main__":
    log = create_logger("trie_autocomplete_demo")

    autocomplete = TrieAutocomplete()
    words = [
        ("cat", 5), ("car", 10), ("card", 2), ("care", 7),
        ("careful", 1), ("dog", 3), ("dodge", 4),
    ]
    for word, freq in words:
        autocomplete.insert(word, freq)

    log.info("top 3 matches for 'car': %s", autocomplete.top_matches("car", k=3))
    log.info("expected ['car', 'care', 'card'] ordered by frequency descending")

    log.info("top 5 matches for 'do': %s", autocomplete.top_matches("do", k=5))
    log.info("matches for 'xyz': %s (expected empty, no such prefix)",
             autocomplete.top_matches("xyz"))
