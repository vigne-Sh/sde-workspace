/*
Circuit breaker state machine (closed/open/half-open) wrapping a remote call that can
fail, trips open after too many failures and probes recovery via half-open, common
system design / resiliency interview question.

status - completed
 */

package com.dev.systemdesign;

import com.dev.logger.basePrinter;

class CircuitBreaker extends basePrinter{

    enum State { CLOSED, OPEN, HALF_OPEN }

    interface RemoteCall {
        String call() throws Exception;
    }

    private final int failureThreshold;
    private final long openDurationMillis;

    private State state = State.CLOSED;
    private int consecutiveFailures = 0;
    private long openedAtMillis = 0;

    CircuitBreaker(int failureThreshold, long openDurationMillis){
        this.failureThreshold = failureThreshold;
        this.openDurationMillis = openDurationMillis;
    }

    String execute(RemoteCall remoteCall) throws Exception {
        if (state == State.OPEN){
            long elapsed = System.currentTimeMillis() - openedAtMillis;
            if (elapsed >= openDurationMillis){
                state = State.HALF_OPEN;
                logp("circuit transitioning OPEN -> HALF_OPEN, allowing a trial call");
            } else {
                throw new RuntimeException("circuit OPEN, call short-circuited");
            }
        }

        try {
            String result = remoteCall.call();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }

    private void onSuccess(){
        if (state == State.HALF_OPEN){
            logp("trial call succeeded, circuit HALF_OPEN -> CLOSED");
        }
        state = State.CLOSED;
        consecutiveFailures = 0;
    }

    private void onFailure(){
        if (state == State.HALF_OPEN){
            logp("trial call failed, circuit HALF_OPEN -> OPEN");
            state = State.OPEN;
            openedAtMillis = System.currentTimeMillis();
            return;
        }

        consecutiveFailures++;
        if (consecutiveFailures >= failureThreshold){
            logp("failure threshold reached, circuit CLOSED -> OPEN");
            state = State.OPEN;
            openedAtMillis = System.currentTimeMillis();
        }
    }

    State getState(){
        return state;
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        CircuitBreaker breaker = new CircuitBreaker(3, 300);

        // a flaky remote call that fails while shouldFail is true
        final boolean[] shouldFail = {true};
        RemoteCall flakyRemoteCall = () -> {
            if (shouldFail[0]){
                throw new RuntimeException("simulated remote failure");
            }
            return "success";
        };

        for (int i = 1; i <= 4; i++){
            try {
                breaker.execute(flakyRemoteCall);
                logp("call " + i + " succeeded");
            } catch (Exception e) {
                logp("call " + i + " failed -> " + e.getMessage() + " | state=" + breaker.getState());
            }
        }

        logp("waiting for open duration to elapse...");
        sleep(400);

        shouldFail[0] = false; // remote service recovers
        try {
            String result = breaker.execute(flakyRemoteCall);
            logp("trial call after wait -> " + result + " | state=" + breaker.getState());
        } catch (Exception e) {
            logp("trial call after wait failed -> " + e.getMessage());
        }

        try {
            String result = breaker.execute(flakyRemoteCall);
            logp("call after recovery -> " + result + " | state=" + breaker.getState());
        } catch (Exception e) {
            logp("unexpected failure -> " + e.getMessage());
        }
    }
}
