"""
Circuit breaker state machine (closed / open / half-open) wrapping a
flaky downstream call. Trips to open after a run of consecutive
failures to stop hammering a failing dependency, waits out a cooldown,
then probes with a half-open trial before fully closing again.

status - completed
"""

import time

from base_logger.logging_event import create_logger


class CircuitOpenError(Exception):
    pass


class CircuitBreaker:

    log = create_logger(__name__)

    CLOSED = "CLOSED"
    OPEN = "OPEN"
    HALF_OPEN = "HALF_OPEN"

    def __init__(self, failure_threshold=3, cooldown_seconds=10, clock=time.monotonic):
        self.failure_threshold = failure_threshold
        self.cooldown_seconds = cooldown_seconds
        self.clock = clock
        self.state = self.CLOSED
        self.consecutive_failures = 0
        self.opened_at = None

    def _maybe_transition_to_half_open(self):
        if self.state == self.OPEN and self.clock() - self.opened_at >= self.cooldown_seconds:
            self.state = self.HALF_OPEN
            self.log.info("cooldown elapsed, transitioning OPEN -> HALF_OPEN")

    def call(self, func, *args, **kwargs):
        self._maybe_transition_to_half_open()

        if self.state == self.OPEN:
            raise CircuitOpenError("circuit is open, call rejected")

        try:
            result = func(*args, **kwargs)
        except Exception:
            self._on_failure()
            raise
        else:
            self._on_success()
            return result

    def _on_success(self):
        if self.state == self.HALF_OPEN:
            self.log.info("trial call succeeded, transitioning HALF_OPEN -> CLOSED")
        self.state = self.CLOSED
        self.consecutive_failures = 0
        self.opened_at = None

    def _on_failure(self):
        if self.state == self.HALF_OPEN:
            self.log.info("trial call failed, transitioning HALF_OPEN -> OPEN")
            self.state = self.OPEN
            self.opened_at = self.clock()
            return

        self.consecutive_failures += 1
        if self.consecutive_failures >= self.failure_threshold:
            self.log.info("failure threshold reached, transitioning CLOSED -> OPEN")
            self.state = self.OPEN
            self.opened_at = self.clock()


if __name__ == "__main__":
    log = create_logger("circuit_breaker_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    breaker = CircuitBreaker(failure_threshold=3, cooldown_seconds=5, clock=fake_clock)

    call_should_fail = [True]

    def flaky_dependency():
        if call_should_fail[0]:
            raise RuntimeError("downstream error")
        return "ok"

    for i in range(3):
        try:
            breaker.call(flaky_dependency)
        except RuntimeError:
            log.info("call %d failed, state=%s", i + 1, breaker.state)

    log.info("after 3 consecutive failures, state=%s (expected OPEN)", breaker.state)

    try:
        breaker.call(flaky_dependency)
    except CircuitOpenError:
        log.info("call rejected immediately while OPEN, dependency not invoked")

    fake_time[0] = 6.0
    call_should_fail[0] = False
    result = breaker.call(flaky_dependency)
    log.info("after cooldown, trial call result=%s, state=%s (expected CLOSED)",
             result, breaker.state)

    result = breaker.call(flaky_dependency)
    log.info("subsequent call result=%s, state=%s", result, breaker.state)
