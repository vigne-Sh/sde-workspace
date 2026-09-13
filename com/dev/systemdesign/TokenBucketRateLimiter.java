/*
Rate limiter using the token bucket algorithm: bucket refills at a fixed rate up to a
max capacity, each request consumes a token, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import com.dev.logger.basePrinter;

class TokenBucketRateLimiter extends basePrinter{

    private final int capacity;
    private final double refillTokensPerMs;
    private double availableTokens;
    private long lastRefillTimestamp;

    TokenBucketRateLimiter(int capacity, double refillTokensPerSecond){
        this.capacity = capacity;
        this.refillTokensPerMs = refillTokensPerSecond / 1000.0;
        this.availableTokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    private synchronized void refill(){
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        if (elapsed <= 0){
            return;
        }
        double tokensToAdd = elapsed * refillTokensPerMs;
        availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
        lastRefillTimestamp = now;
    }

    synchronized boolean allowRequest(){
        refill();
        if (availableTokens >= 1.0){
            availableTokens -= 1.0;
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
        // bucket of 3 tokens, refilling at 5 tokens/sec
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 5);

        for (int i = 1; i <= 5; i++){
            boolean allowed = limiter.allowRequest();
            logp("burst request " + i + " allowed -> " + allowed);
        }
        // first 3 should pass (bucket starts full), rest denied until refill

        logp("sleeping 500ms to let bucket refill...");
        sleep(500);

        boolean allowedAfterWait = limiter.allowRequest();
        logp("request after refill wait allowed -> " + allowedAfterWait); // should be true
    }
}
