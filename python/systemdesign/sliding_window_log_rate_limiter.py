"""
Rate limiter storing the actual timestamp log of each request per
client, evicting entries that have fallen outside the trailing window
before deciding whether a new request is allowed. More memory-hungry
than a counter-based approach but exact rather than approximated.

status - completed
"""

from collections import defaultdict, deque

from base_logger.logging_event import create_logger


class SlidingWindowLogRateLimiter:

    log = create_logger(__name__)

    def __init__(self, max_requests: int, window_size_sec: float, clock=None):
        self.max_requests = max_requests
        self.window_size_sec = window_size_sec
        self.clock = clock
        self.logs = defaultdict(deque)

    def _evict_old(self, client_id, now):
        log_deque = self.logs[client_id]
        cutoff = now - self.window_size_sec
        while log_deque and log_deque[0] <= cutoff:
            log_deque.popleft()

    def allow_request(self, client_id, now):
        self._evict_old(client_id, now)
        log_deque = self.logs[client_id]

        if len(log_deque) < self.max_requests:
            log_deque.append(now)
            return True
        return False

    def request_count(self, client_id, now):
        self._evict_old(client_id, now)
        return len(self.logs[client_id])


if __name__ == "__main__":
    log = create_logger("sliding_window_log_demo")

    limiter = SlidingWindowLogRateLimiter(max_requests=3, window_size_sec=10.0)

    results = [limiter.allow_request("client-1", now=t) for t in [0, 1, 2]]
    log.info("3 requests at t=0,1,2 (limit 3): %s", results)

    log.info("4th request at t=3 -> %s (expected False, window full)",
             limiter.allow_request("client-1", now=3))

    log.info("request at t=11 (t=0 entry now outside window) -> %s (expected True)",
             limiter.allow_request("client-1", now=11))

    log.info("current count for client-1 at t=11: %d", limiter.request_count("client-1", now=11))

    other_client_result = limiter.allow_request("client-2", now=11)
    log.info("client-2's independent window, request at t=11 -> %s (expected True)",
             other_client_result)
