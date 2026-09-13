"""
https://leetcode.com/problems/serialize-and-deserialize-bst/

status - completed
"""

from base_logger.logging_event import create_logger


class TreeNode:

    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Codec:

    log = create_logger(__name__)

    def serialize(self, root):
        values = []

        def preorder(node):
            if node:
                values.append(str(node.val))
                preorder(node.left)
                preorder(node.right)

        preorder(root)
        return ",".join(values)

    def deserialize(self, data):
        if not data:
            return None

        values = [int(v) for v in data.split(",")]
        self.index = 0

        def build(lower, upper):
            if self.index == len(values):
                return None
            val = values[self.index]
            if val < lower or val > upper:
                return None

            self.index += 1
            node = TreeNode(val)
            node.left = build(lower, val)
            node.right = build(val, upper)
            return node

        return build(float("-inf"), float("inf"))

    def tree_to_list(self, node):
        if not node:
            return []
        return self.tree_to_list(node.left) + [node.val] + self.tree_to_list(node.right)


if __name__ == "__main__":
    codec = Codec()

    root_1 = TreeNode(2, TreeNode(1), TreeNode(3))
    serialized = codec.serialize(root_1)
    deserialized = codec.deserialize(serialized)
    output = (serialized, codec.tree_to_list(deserialized))
    codec.log.info("Output is %s", output)

    left_2 = TreeNode(2, TreeNode(1), TreeNode(3))
    right_2 = TreeNode(6, TreeNode(5), TreeNode(7))
    root_2 = TreeNode(4, left_2, right_2)
    serialized = codec.serialize(root_2)
    deserialized = codec.deserialize(serialized)
    output = (serialized, codec.tree_to_list(deserialized))
    codec.log.info("Output is %s", output)
