/*
Consistent hashing ring with virtual nodes for even key distribution and minimal
key remapping when nodes are added or removed, common system design question.

status - completed
 */

package com.dev.systemdesign;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map;
import com.dev.logger.basePrinter;

class ConsistentHashingRing extends basePrinter{

    private final int virtualNodesPerServer;
    private final TreeMap<Integer, String> ring;

    ConsistentHashingRing(int virtualNodesPerServer){
        this.virtualNodesPerServer = virtualNodesPerServer;
        this.ring = new TreeMap<>();
    }

    private int hash(String value){
        // String.hashCode() alone clusters similar strings (e.g. "key0" vs "key1") close
        // together, so run it through a murmur3-style finalizer for a proper avalanche
        // effect and even spread across the ring.
        int h = value.hashCode();
        h ^= (h >>> 16);
        h *= 0x85ebca6b;
        h ^= (h >>> 13);
        h *= 0xc2b2ae35;
        h ^= (h >>> 16);
        return h;
    }

    void addServer(String serverName){
        for (int i = 0; i < virtualNodesPerServer; i++){
            String virtualKey = serverName + "#VN" + i;
            ring.put(hash(virtualKey), serverName);
        }
    }

    void removeServer(String serverName){
        for (int i = 0; i < virtualNodesPerServer; i++){
            String virtualKey = serverName + "#VN" + i;
            ring.remove(hash(virtualKey));
        }
    }

    String getServerForKey(String key){
        if (ring.isEmpty()){
            return null;
        }
        int h = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(h);
        if (entry == null){
            entry = ring.firstEntry(); // wrap around the ring
        }
        return entry.getValue();
    }

    public static void main(String[] args) {
        ConsistentHashingRing hashRing = new ConsistentHashingRing(100);
        hashRing.addServer("serverA");
        hashRing.addServer("serverB");
        hashRing.addServer("serverC");

        String[] keys = new String[50];
        for (int i = 0; i < keys.length; i++){
            keys[i] = "key" + i;
        }

        HashMap<String, String> before = new HashMap<>();
        for (String key : keys){
            before.put(key, hashRing.getServerForKey(key));
        }

        logp("added serverD to the ring, checking remapping...");
        hashRing.addServer("serverD");

        int remapped = 0;
        for (String key : keys){
            String newServer = hashRing.getServerForKey(key);
            if (!newServer.equals(before.get(key))){
                remapped++;
            }
        }
        logp("keys remapped after adding a 4th server out of " + keys.length + " -> " + remapped);
        logp("(consistent hashing keeps this small, roughly total/serverCount, not a full reshuffle)");

        HashMap<String, String> afterAdd = new HashMap<>();
        for (String key : keys){
            afterAdd.put(key, hashRing.getServerForKey(key));
        }

        logp("removed serverB from the ring, checking remapping...");
        hashRing.removeServer("serverB");

        int remappedAfterRemove = 0;
        for (String key : keys){
            String newServer = hashRing.getServerForKey(key);
            if (!newServer.equals(afterAdd.get(key))){
                remappedAfterRemove++;
            }
        }
        logp("keys remapped after removing serverB out of " + keys.length + " -> " + remappedAfterRemove);
    }
}
