"""
Delayed job scheduler: jobs are scheduled to run after a delay and
kept in a min-heap ordered by their execution time, so the scheduler
can cheaply peek and pop the next job that's due without scanning the
whole queue.

status - completed
"""

import heapq
import itertools
import time

from base_logger.logging_event import create_logger


class DelayedJobScheduler:

    log = create_logger(__name__)

    def __init__(self, clock=time.monotonic):
        self.clock = clock
        self.heap = []
        self._counter = itertools.count()

    def schedule(self, job, delay_seconds):
        run_at = self.clock() + delay_seconds
        entry_id = next(self._counter)
        heapq.heappush(self.heap, (run_at, entry_id, job))
        return entry_id

    def next_run_at(self):
        return self.heap[0][0] if self.heap else None

    def run_due_jobs(self):
        now = self.clock()
        results = []
        while self.heap and self.heap[0][0] <= now:
            run_at, entry_id, job = heapq.heappop(self.heap)
            results.append(job())
        return results

    def pending_count(self):
        return len(self.heap)


if __name__ == "__main__":
    log = create_logger("delayed_job_scheduler_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    scheduler = DelayedJobScheduler(clock=fake_clock)

    scheduler.schedule(lambda: "job-A (delay 10s)", delay_seconds=10)
    scheduler.schedule(lambda: "job-B (delay 3s)", delay_seconds=3)
    scheduler.schedule(lambda: "job-C (delay 7s)", delay_seconds=7)

    log.info("3 jobs scheduled, pending=%d, next_run_at=%s",
             scheduler.pending_count(), scheduler.next_run_at())

    fake_time[0] = 4.0
    ran = scheduler.run_due_jobs()
    log.info("at t=4s, jobs run: %s (expected only job-B, delay 3s)", ran)

    fake_time[0] = 8.0
    ran = scheduler.run_due_jobs()
    log.info("at t=8s, jobs run: %s (expected only job-C, delay 7s)", ran)

    fake_time[0] = 11.0
    ran = scheduler.run_due_jobs()
    log.info("at t=11s, jobs run: %s (expected job-A, delay 10s)", ran)

    log.info("pending jobs remaining: %d (expected 0)", scheduler.pending_count())
