/*
Design an LRU cache with O(1) get/put using a HashMap plus a doubly linked list to
track recency, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import com.dev.logger.basePrinter;

class LRUCache extends basePrinter{

    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value){
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final HashMap<Integer, Node> map;
    private final Node head; // most recently used side
    private final Node tail; // least recently used side

    LRUCache(int capacity){
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node){
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtFront(Node node){
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    int get(int key){
        if (!map.containsKey(key)){
            return -1;
        }
        Node node = map.get(key);
        remove(node);
        insertAtFront(node);
        return node.value;
    }

    void put(int key, int value){
        if (map.containsKey(key)){
            Node existing = map.get(key);
            existing.value = value;
            remove(existing);
            insertAtFront(existing);
            return;
        }

        if (map.size() == capacity){
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }

        Node node = new Node(key, value);
        insertAtFront(node);
        map.put(key, node);
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 100);
        cache.put(2, 200);
        logp("get(1) -> " + cache.get(1)); // 100, makes 1 most recently used

        cache.put(3, 300); // evicts key 2 (least recently used)
        logp("get(2) after eviction -> " + cache.get(2)); // -1

        cache.put(4, 400); // evicts key 1
        logp("get(1) after eviction -> " + cache.get(1)); // -1
        logp("get(3) -> " + cache.get(3)); // 300
        logp("get(4) -> " + cache.get(4)); // 400
    }
}
