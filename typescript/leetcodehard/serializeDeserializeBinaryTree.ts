/*
https://leetcode.com/problems/serialize-and-deserialize-binary-tree/

status - completed
*/

import { logp } from "../utils/logger";

class TreeNode {
  val: number;
  left: TreeNode | null;
  right: TreeNode | null;

  constructor(val: number, left: TreeNode | null = null, right: TreeNode | null = null) {
    this.val = val;
    this.left = left;
    this.right = right;
  }
}

function serialize(root: TreeNode | null): string {
  const parts: string[] = [];

  function traverse(node: TreeNode | null): void {
    if (!node) {
      parts.push("#");
      return;
    }
    parts.push(String(node.val));
    traverse(node.left);
    traverse(node.right);
  }

  traverse(root);
  return parts.join(",");
}

function deserialize(data: string): TreeNode | null {
  const values = data.split(",");
  let index = 0;

  function build(): TreeNode | null {
    const token = values[index];
    index++;
    if (token === "#") return null;

    const node = new TreeNode(Number(token));
    node.left = build();
    node.right = build();
    return node;
  }

  return build();
}

const root = new TreeNode(1, new TreeNode(2), new TreeNode(3, new TreeNode(4), new TreeNode(5)));
const serialized = serialize(root);
const roundTripped = serialize(deserialize(serialized));
logp(`input tree [1,2,3,null,null,4,5] serialized ${serialized}`);
logp(`round trip match expected true actual ${serialized === roundTripped}`);

const nullSerialized = serialize(null);
logp(`input null expected # actual ${nullSerialized}`);
logp(`round trip null match expected true actual ${deserialize(nullSerialized) === null}`);
