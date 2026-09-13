/*
https://leetcode.com/problems/merge-k-sorted-lists/

status - completed
 */

package com.dev.leetcodehard;

import com.dev.logger.basePrinter;

import java.util.PriorityQueue;
import java.util.Comparator;

class MergeKSortedLists extends basePrinter{

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val){
            this.val = val;
        }
    }

    static ListNode mergeKLists(ListNode[] lists){
        PriorityQueue<ListNode> heap = new PriorityQueue<>(new Comparator<ListNode>() {
            public int compare(ListNode a, ListNode b){
                return a.val - b.val;
            }
        });

        for (ListNode node : lists){
            if (node != null){
                heap.add(node);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!heap.isEmpty()){
            ListNode smallest = heap.poll();
            tail.next = smallest;
            tail = tail.next;
            if (smallest.next != null){
                heap.add(smallest.next);
            }
        }

        return dummy.next;
    }

    static ListNode buildList(int[] values){
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int v : values){
            curr.next = new ListNode(v);
            curr = curr.next;
        }
        return dummy.next;
    }

    static String listToString(ListNode head){
        StringBuilder sb = new StringBuilder();
        while (head != null){
            sb.append(head.val);
            if (head.next != null){
                sb.append(",");
            }
            head = head.next;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ListNode[] lists1 = {
            buildList(new int[]{1,4,5}),
            buildList(new int[]{1,3,4}),
            buildList(new int[]{2,6})
        };
        logp("input [[1,4,5],[1,3,4],[2,6]] expected 1,1,2,3,4,4,5,6 actual " + listToString(mergeKLists(lists1)));

        ListNode[] lists2 = {};
        logp("input [] expected (empty) actual " + listToString(mergeKLists(lists2)));

        ListNode[] lists3 = { null, buildList(new int[]{0}) };
        logp("input [[],[0]] expected 0 actual " + listToString(mergeKLists(lists3)));
    }
}
