/*
In-memory key-value store where entries expire after a TTL, supports lazy expiry on
read plus an active purge sweep, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.dev.logger.basePrinter;

class InMemoryKVStoreWithTTL extends basePrinter{

    private static class Entry {
        String value;
        long expiresAtMillis; // -1 means never expires

        Entry(String value, long expiresAtMillis){
            this.value = value;
            this.expiresAtMillis = expiresAtMillis;
        }

        boolean isExpired(long now){
            return expiresAtMillis != -1 && now >= expiresAtMillis;
        }
    }

    private final HashMap<String, Entry> store;

    InMemoryKVStoreWithTTL(){
        this.store = new HashMap<>();
    }

    void put(String key, String value){
        store.put(key, new Entry(value, -1));
    }

    void put(String key, String value, long ttlMillis){
        store.put(key, new Entry(value, System.currentTimeMillis() + ttlMillis));
    }

    String get(String key){
        Entry entry = store.get(key);
        if (entry == null){
            return null;
        }
        if (entry.isExpired(System.currentTimeMillis())){
            store.remove(key); // lazy expiry
            return null;
        }
        return entry.value;
    }

    int purgeExpired(){
        int removed = 0;
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Entry>> it = store.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Entry> mapEntry = it.next();
            if (mapEntry.getValue().isExpired(now)){
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    int size(){
        return store.size();
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        InMemoryKVStoreWithTTL kvStore = new InMemoryKVStoreWithTTL();
        kvStore.put("permanent", "stays forever");
        kvStore.put("shortLived", "expires soon", 200);

        logp("get(permanent) -> " + kvStore.get("permanent"));
        logp("get(shortLived) immediately -> " + kvStore.get("shortLived"));

        sleep(300);

        logp("get(shortLived) after ttl elapsed (lazy expiry) -> " + kvStore.get("shortLived"));
        logp("get(permanent) still present -> " + kvStore.get("permanent"));

        kvStore.put("anotherShortLived", "also expires", 100);
        logp("size before purge -> " + kvStore.size());
        sleep(200);
        int purged = kvStore.purgeExpired();
        logp("entries purged -> " + purged);
        logp("size after purge -> " + kvStore.size());
    }
}
