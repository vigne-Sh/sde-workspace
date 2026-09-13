/*
https://leetcode.com/problems/serialize-and-deserialize-binary-tree/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.LinkedList;
import java.util.Queue;

class SerializeDeserializeBinaryTree extends basePrinter{

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val){
            this.val = val;
        }
    }

    static String serialize(TreeNode root){
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    static void serializeHelper(TreeNode node, StringBuilder sb){
        if (node == null){
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    static TreeNode deserialize(String data){
        Queue<String> queue = new LinkedList<>();
        for (String token : data.split(",")){
            queue.add(token);
        }
        return deserializeHelper(queue);
    }

    static TreeNode deserializeHelper(Queue<String> queue){
        String token = queue.poll();
        if (token.equals("#")){
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    static String inorder(TreeNode root){
        if (root == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        inorderHelper(root, sb);
        return sb.toString().trim();
    }

    static void inorderHelper(TreeNode node, StringBuilder sb){
        if (node == null) return;
        inorderHelper(node.left, sb);
        sb.append(node.val).append(" ");
        inorderHelper(node.right, sb);
    }

    public static void main(String[] args) {
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.right.left = new TreeNode(4);
        root1.right.right = new TreeNode(5);

        String serialized1 = serialize(root1);
        TreeNode deserialized1 = deserialize(serialized1);
        logp("input tree [1,2,3,null,null,4,5] serialized=" + serialized1);
        logp("expected inorder 2 1 4 3 5 actual " + inorder(deserialized1));

        TreeNode root2 = null;
        String serialized2 = serialize(root2);
        TreeNode deserialized2 = deserialize(serialized2);
        logp("input tree null serialized=" + serialized2 + " expected inorder (empty) actual '" + inorder(deserialized2) + "'");

        TreeNode root3 = new TreeNode(42);
        String serialized3 = serialize(root3);
        TreeNode deserialized3 = deserialize(serialized3);
        logp("input single node 42 expected inorder 42 actual " + inorder(deserialized3));
    }
}
