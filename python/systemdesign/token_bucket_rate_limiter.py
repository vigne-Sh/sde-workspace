"""
Token bucket rate limiter: a bucket holds up to capacity tokens,
refills at a fixed rate, and each request consumes one token. Requests
are allowed only while tokens are available, giving bursty-but-bounded
throughput.

status - completed
"""

import time

from base_logger.logging_event import create_logger


class TokenBucketRateLimiter:

    log = create_logger(__name__)

    def __init__(self, capacity: int, refill_rate_per_sec: float, clock=time.monotonic):
        self.capacity = capacity
        self.refill_rate_per_sec = refill_rate_per_sec
        self.clock = clock
        self.tokens = float(capacity)
        self.last_refill = self.clock()

    def _refill(self):
        now = self.clock()
        elapsed = now - self.last_refill
        if elapsed <= 0:
            return
        self.tokens = min(self.capacity, self.tokens + elapsed * self.refill_rate_per_sec)
        self.last_refill = now

    def allow_request(self) -> bool:
        self._refill()
        if self.tokens >= 1:
            self.tokens -= 1
            return True
        return False


if __name__ == "__main__":
    log = create_logger("token_bucket_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    limiter = TokenBucketRateLimiter(capacity=3, refill_rate_per_sec=1.0, clock=fake_clock)

    results = []
    for i in range(5):
        results.append(limiter.allow_request())
    log.info("5 immediate requests against capacity 3: %s", results)
    log.info("expected first 3 True (burst), last 2 False (bucket empty)")

    fake_time[0] += 2.0
    log.info("advance fake clock by 2s -> refill ~2 tokens")
    log.info("allow_request() -> %s", limiter.allow_request())
    log.info("allow_request() -> %s", limiter.allow_request())
    log.info("allow_request() -> %s (expected False, exhausted again)", limiter.allow_request())
