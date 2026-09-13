/*
LRU cache safe under concurrent access using synchronization around the underlying
HashMap + doubly linked list structure, demonstrated with multiple threads hammering
it at once, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.dev.logger.basePrinter;

class ThreadSafeLRUCache extends basePrinter{

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
    private final Node head;
    private final Node tail;
    private final Object lock = new Object();

    ThreadSafeLRUCache(int capacity){
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
        synchronized (lock){
            if (!map.containsKey(key)){
                return -1;
            }
            Node node = map.get(key);
            remove(node);
            insertAtFront(node);
            return node.value;
        }
    }

    void put(int key, int value){
        synchronized (lock){
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
    }

    int size(){
        synchronized (lock){
            return map.size();
        }
    }

    public static void main(String[] args) {
        ThreadSafeLRUCache cache = new ThreadSafeLRUCache(50);

        int threadCount = 8;
        int putsPerThread = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++){
            final int threadId = t;
            executor.submit(() -> {
                for (int i = 0; i < putsPerThread; i++){
                    int key = (threadId * putsPerThread + i) % 100;
                    cache.put(key, threadId * 100000 + i);
                    cache.get(key % 50);
                }
            });
        }

        executor.shutdown();
        boolean finished = false;
        try {
            finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logp("all threads finished within timeout -> " + finished);

        logp("cache size after concurrent load (should never exceed capacity 50) -> " + cache.size());
        logp("size within capacity bound -> " + (cache.size() <= 50));

        // sanity check cache still behaves correctly for simple sequential ops afterward
        ThreadSafeLRUCache small = new ThreadSafeLRUCache(2);
        small.put(1, 10);
        small.put(2, 20);
        small.get(1);
        small.put(3, 30); // evicts key 2
        logp("sequential check, get(2) after eviction -> " + small.get(2)); // -1
        logp("sequential check, get(1) still present -> " + small.get(1)); // 10
        logp("sequential check, get(3) still present -> " + small.get(3)); // 30
    }
}
