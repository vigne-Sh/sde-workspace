"""
Rate limiter using a sliding time window: tracks a count of requests
in the current and previous fixed windows and interpolates a weighted
estimate, avoiding both the burst-at-boundary problem of fixed windows
and the memory cost of storing every timestamp.

status - completed
"""

import time

from base_logger.logging_event import create_logger


class SlidingWindowRateLimiter:

    log = create_logger(__name__)

    def __init__(self, max_requests: int, window_size_sec: float, clock=time.monotonic):
        self.max_requests = max_requests
        self.window_size_sec = window_size_sec
        self.clock = clock
        self.previous_count = 0
        self.current_count = 0
        self.current_window_start = self._window_start(self.clock())

    def _window_start(self, now):
        return (now // self.window_size_sec) * self.window_size_sec

    def _roll_window(self, now):
        window_start = self._window_start(now)
        if window_start == self.current_window_start:
            return
        windows_elapsed = (window_start - self.current_window_start) / self.window_size_sec
        if windows_elapsed >= 2:
            self.previous_count = 0
        else:
            self.previous_count = self.current_count
        self.current_count = 0
        self.current_window_start = window_start

    def allow_request(self) -> bool:
        now = self.clock()
        self._roll_window(now)

        elapsed_in_current = now - self.current_window_start
        remaining_fraction = (self.window_size_sec - elapsed_in_current) / self.window_size_sec
        weight_of_previous = max(0.0, remaining_fraction)
        estimated_count = self.previous_count * weight_of_previous + self.current_count

        if estimated_count >= self.max_requests:
            return False

        self.current_count += 1
        return True


if __name__ == "__main__":
    log = create_logger("sliding_window_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    limiter = SlidingWindowRateLimiter(max_requests=5, window_size_sec=10.0, clock=fake_clock)

    results = [limiter.allow_request() for _ in range(5)]
    log.info("5 requests in first window (limit 5): %s", results)
    log.info("6th request immediately after -> %s (expected False)", limiter.allow_request())

    fake_time[0] = 10.0
    log.info("at t=10s (new window boundary) request -> %s (expected False, "
             "weighted estimate of previous window's 5 requests still saturates the limit)",
             limiter.allow_request())

    fake_time[0] = 15.0
    log.info("at t=15s (halfway through the window, previous weight drops to 0.5)")
    for _ in range(4):
        log.info("allow_request() -> %s", limiter.allow_request())
    log.info("expected True, True, True, False as the weighted estimate reaches the limit")
