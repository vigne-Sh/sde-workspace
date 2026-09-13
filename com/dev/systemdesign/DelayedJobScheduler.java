/*
Schedule jobs to run after a delay using a priority queue ordered by execution time,
a worker loop pulls and runs jobs once their time has arrived, common system design
interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.PriorityQueue;
import com.dev.logger.basePrinter;

class DelayedJobScheduler extends basePrinter{

    private static class ScheduledJob {
        final String name;
        final long executeAtMillis;
        final Runnable task;

        ScheduledJob(String name, long executeAtMillis, Runnable task){
            this.name = name;
            this.executeAtMillis = executeAtMillis;
            this.task = task;
        }
    }

    private final PriorityQueue<ScheduledJob> queue;

    DelayedJobScheduler(){
        this.queue = new PriorityQueue<>((a, b) -> Long.compare(a.executeAtMillis, b.executeAtMillis));
    }

    synchronized void schedule(String name, long delayMillis, Runnable task){
        long executeAt = System.currentTimeMillis() + delayMillis;
        queue.add(new ScheduledJob(name, executeAt, task));
    }

    // runs all jobs that are currently due; returns count of jobs executed
    synchronized int runDueJobs(){
        int executed = 0;
        long now = System.currentTimeMillis();
        while (!queue.isEmpty() && queue.peek().executeAtMillis <= now){
            ScheduledJob job = queue.poll();
            job.task.run();
            executed++;
        }
        return executed;
    }

    synchronized int pendingCount(){
        return queue.size();
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        DelayedJobScheduler scheduler = new DelayedJobScheduler();

        scheduler.schedule("jobC", 300, () -> logp("running jobC (delay 300ms)"));
        scheduler.schedule("jobA", 50, () -> logp("running jobA (delay 50ms)"));
        scheduler.schedule("jobB", 150, () -> logp("running jobB (delay 150ms)"));

        logp("pending jobs right after scheduling -> " + scheduler.pendingCount());

        sleep(100);
        logp("polling at t=100ms, jobs due -> " + scheduler.runDueJobs()); // should run jobA only

        sleep(100);
        logp("polling at t=200ms, jobs due -> " + scheduler.runDueJobs()); // should run jobB

        sleep(150);
        logp("polling at t=350ms, jobs due -> " + scheduler.runDueJobs()); // should run jobC

        logp("pending jobs remaining -> " + scheduler.pendingCount());
    }
}
