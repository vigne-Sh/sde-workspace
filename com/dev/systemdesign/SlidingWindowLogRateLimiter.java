/*
Rate limiter that stores a log of exact request timestamps per client and evicts
entries falling outside the rolling window on every check, the classic "sliding
window log" algorithm, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.LinkedList;
import java.util.HashMap;
import com.dev.logger.basePrinter;

class SlidingWindowLogRateLimiter extends basePrinter{

    private final int maxRequestsPerWindow;
    private final long windowSizeMillis;
    private final HashMap<String, LinkedList<Long>> requestLogs;

    SlidingWindowLogRateLimiter(int maxRequestsPerWindow, long windowSizeMillis){
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSizeMillis = windowSizeMillis;
        this.requestLogs = new HashMap<>();
    }

    synchronized boolean isAllowed(String clientId, long now){
        LinkedList<Long> log = requestLogs.computeIfAbsent(clientId, k -> new LinkedList<>());

        evictOutsideWindow(log, now);

        if (log.size() < maxRequestsPerWindow){
            log.addLast(now);
            return true;
        }
        return false;
    }

    private void evictOutsideWindow(LinkedList<Long> log, long now){
        while (!log.isEmpty() && now - log.getFirst() >= windowSizeMillis){
            log.removeFirst();
        }
    }

    synchronized int currentLogSize(String clientId, long now){
        LinkedList<Long> log = requestLogs.get(clientId);
        if (log == null){
            return 0;
        }
        evictOutsideWindow(log, now);
        return log.size();
    }

    public static void main(String[] args) {
        // allow at most 4 requests per 1000ms rolling window
        SlidingWindowLogRateLimiter limiter = new SlidingWindowLogRateLimiter(4, 1000);
        String client = "clientX";

        long[] timestamps = {0, 200, 400, 600, 700, 1100, 1250};
        for (long t : timestamps){
            boolean allowed = limiter.isAllowed(client, t);
            logp("t=" + t + " allowed=" + allowed + " logSize=" + limiter.currentLogSize(client, t));
        }
        // t=0,200,400,600 -> allowed (4 total)
        // t=700 -> denied, window still has 4 entries within [t-1000, t]
        // t=1100 -> t=0 falls out of window (1100-0>=1000), allowed
        // t=1250 -> t=200 falls out too, allowed
    }
}
