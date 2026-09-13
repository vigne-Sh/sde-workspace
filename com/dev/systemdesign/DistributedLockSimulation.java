/*
Simulate a distributed lock service with lease/expiry semantics, ensuring at most one
client holds a given lock at a time and leases can be renewed or expire on their own,
common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import com.dev.logger.basePrinter;

class DistributedLockSimulation extends basePrinter{

    private static class Lease {
        String ownerClientId;
        long expiresAtMillis;

        Lease(String ownerClientId, long expiresAtMillis){
            this.ownerClientId = ownerClientId;
            this.expiresAtMillis = expiresAtMillis;
        }
    }

    private final HashMap<String, Lease> locks;

    DistributedLockSimulation(){
        this.locks = new HashMap<>();
    }

    private boolean isExpired(Lease lease, long now){
        return now >= lease.expiresAtMillis;
    }

    // attempts to acquire the lock, succeeds if free or the existing lease expired
    synchronized boolean tryAcquire(String lockName, String clientId, long leaseDurationMillis){
        long now = System.currentTimeMillis();
        Lease existing = locks.get(lockName);

        if (existing == null || isExpired(existing, now)){
            locks.put(lockName, new Lease(clientId, now + leaseDurationMillis));
            return true;
        }

        return existing.ownerClientId.equals(clientId); // reentrant for same owner while valid
    }

    synchronized boolean renew(String lockName, String clientId, long leaseDurationMillis){
        long now = System.currentTimeMillis();
        Lease existing = locks.get(lockName);
        if (existing == null || isExpired(existing, now) || !existing.ownerClientId.equals(clientId)){
            return false;
        }
        existing.expiresAtMillis = now + leaseDurationMillis;
        return true;
    }

    synchronized boolean release(String lockName, String clientId){
        Lease existing = locks.get(lockName);
        if (existing != null && existing.ownerClientId.equals(clientId)){
            locks.remove(lockName);
            return true;
        }
        return false;
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        DistributedLockSimulation lockService = new DistributedLockSimulation();

        boolean clientAAcquired = lockService.tryAcquire("resourceX", "clientA", 300);
        logp("clientA acquires resourceX -> " + clientAAcquired); // true

        boolean clientBAcquired = lockService.tryAcquire("resourceX", "clientB", 300);
        logp("clientB tries resourceX while clientA holds it -> " + clientBAcquired); // false, still held

        boolean clientARenewed = lockService.renew("resourceX", "clientA", 300);
        logp("clientA renews its lease -> " + clientARenewed); // true

        boolean clientBReleaseAttempt = lockService.release("resourceX", "clientB");
        logp("clientB tries to release a lock it doesn't own -> " + clientBReleaseAttempt); // false

        boolean clientAReleased = lockService.release("resourceX", "clientA");
        logp("clientA releases resourceX -> " + clientAReleased); // true

        boolean clientBAcquiresAfterRelease = lockService.tryAcquire("resourceX", "clientB", 200);
        logp("clientB acquires resourceX after release -> " + clientBAcquiresAfterRelease); // true

        logp("waiting for clientB's lease to expire...");
        sleep(300);

        boolean clientAAcquiresAfterExpiry = lockService.tryAcquire("resourceX", "clientA", 300);
        logp("clientA acquires resourceX after clientB's lease expired -> " + clientAAcquiresAfterExpiry); // true
    }
}
