/*
Design an LFU cache with O(1) get/put that evicts the least-frequently-used entry,
breaking ties by least recently used, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import com.dev.logger.basePrinter;

class LFUCache extends basePrinter{

    private final int capacity;
    private int minFreq;
    private final HashMap<Integer, Integer> keyToValue;
    private final HashMap<Integer, Integer> keyToFreq;
    private final HashMap<Integer, LinkedHashSet<Integer>> freqToKeys;

    LFUCache(int capacity){
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToValue = new HashMap<>();
        this.keyToFreq = new HashMap<>();
        this.freqToKeys = new HashMap<>();
    }

    private void bumpFrequency(int key){
        int freq = keyToFreq.get(key);
        freqToKeys.get(freq).remove(key);
        if (freqToKeys.get(freq).isEmpty()){
            freqToKeys.remove(freq);
            if (minFreq == freq){
                minFreq++;
            }
        }
        int newFreq = freq + 1;
        keyToFreq.put(key, newFreq);
        freqToKeys.computeIfAbsent(newFreq, k -> new LinkedHashSet<>()).add(key);
    }

    int get(int key){
        if (!keyToValue.containsKey(key)){
            return -1;
        }
        bumpFrequency(key);
        return keyToValue.get(key);
    }

    void put(int key, int value){
        if (capacity <= 0){
            return;
        }

        if (keyToValue.containsKey(key)){
            keyToValue.put(key, value);
            bumpFrequency(key);
            return;
        }

        if (keyToValue.size() == capacity){
            LinkedHashSet<Integer> leastFreqKeys = freqToKeys.get(minFreq);
            Iterator<Integer> it = leastFreqKeys.iterator();
            int evictKey = it.next();
            it.remove();
            if (leastFreqKeys.isEmpty()){
                freqToKeys.remove(minFreq);
            }
            keyToValue.remove(evictKey);
            keyToFreq.remove(evictKey);
        }

        keyToValue.put(key, value);
        keyToFreq.put(key, 1);
        freqToKeys.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        logp("get(1) -> " + cache.get(1)); // 10, freq(1)=2, freq(2)=1

        cache.put(3, 30); // capacity full, evict key 2 (freq 1, least frequent)
        logp("get(2) after eviction -> " + cache.get(2)); // -1
        logp("get(3) -> " + cache.get(3)); // 30

        // now both key 1 and key 3 are at freq 2 (tied), key 1 was touched first so it
        // is the least recently used of the tie and gets evicted
        cache.put(4, 40);
        logp("get(1) after put(4) -> " + cache.get(1)); // -1, evicted on the freq tie
        logp("get(3) after put(4) -> " + cache.get(3)); // 30
        logp("get(4) -> " + cache.get(4)); // 40
    }
}
