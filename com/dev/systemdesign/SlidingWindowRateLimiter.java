/*
Rate limiter using a sliding time window of request timestamps per client, allows at
most N requests in any rolling window of W milliseconds, common system design question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import com.dev.logger.basePrinter;

class SlidingWindowRateLimiter extends basePrinter{

    private final int maxRequests;
    private final long windowMillis;
    private final HashMap<String, Deque<Long>> requestLogs;

    SlidingWindowRateLimiter(int maxRequests, long windowMillis){
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.requestLogs = new HashMap<>();
    }

    synchronized boolean allowRequest(String clientId, long timestamp){
        Deque<Long> log = requestLogs.computeIfAbsent(clientId, k -> new ArrayDeque<>());

        while (!log.isEmpty() && timestamp - log.peekFirst() >= windowMillis){
            log.pollFirst();
        }

        if (log.size() < maxRequests){
            log.addLast(timestamp);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // allow 3 requests per 1000ms window
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 1000);
        String client = "clientA";
        long base = 0;

        logp("t=0 -> " + limiter.allowRequest(client, base + 0));     // true (1)
        logp("t=100 -> " + limiter.allowRequest(client, base + 100)); // true (2)
        logp("t=200 -> " + limiter.allowRequest(client, base + 200)); // true (3)
        logp("t=300 -> " + limiter.allowRequest(client, base + 300)); // false, window full

        logp("t=1050 -> " + limiter.allowRequest(client, base + 1050)); // true, t=0 fell out of window
        logp("t=1080 -> " + limiter.allowRequest(client, base + 1080)); // false still, t=100,200,1050 within window
    }
}
