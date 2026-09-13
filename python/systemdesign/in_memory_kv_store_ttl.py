"""
In-memory key-value store with per-key time-to-live expiry. Expired
keys are checked lazily on access and are excluded by an active sweep
helper, avoiding the need for a background thread.

status - completed
"""

import time

from base_logger.logging_event import create_logger


class InMemoryKvStoreTtl:

    log = create_logger(__name__)

    def __init__(self, clock=time.monotonic):
        self.clock = clock
        self.store = {}
        self.expires_at = {}

    def set(self, key, value, ttl_seconds=None):
        self.store[key] = value
        if ttl_seconds is not None:
            self.expires_at[key] = self.clock() + ttl_seconds
        else:
            self.expires_at.pop(key, None)

    def _is_expired(self, key):
        expiry = self.expires_at.get(key)
        return expiry is not None and self.clock() >= expiry

    def get(self, key, default=None):
        if key not in self.store:
            return default
        if self._is_expired(key):
            self.delete(key)
            return default
        return self.store[key]

    def delete(self, key):
        self.store.pop(key, None)
        self.expires_at.pop(key, None)

    def sweep_expired(self):
        expired_keys = [key for key in self.store if self._is_expired(key)]
        for key in expired_keys:
            self.delete(key)
        return expired_keys

    def size(self):
        return len(self.store)


if __name__ == "__main__":
    log = create_logger("kv_store_ttl_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    store = InMemoryKvStoreTtl(clock=fake_clock)
    store.set("session:1", "alice", ttl_seconds=5)
    store.set("session:2", "bob", ttl_seconds=100)
    store.set("permanent", "config-value")

    log.info("get(session:1) -> %s", store.get("session:1"))

    fake_time[0] = 6.0
    log.info("after 6s, get(session:1) -> %s (expected None, expired)", store.get("session:1"))
    log.info("get(session:2) -> %s (still alive)", store.get("session:2"))
    log.info("get(permanent) -> %s (no ttl, never expires)", store.get("permanent"))

    fake_time[0] = 200.0
    expired = store.sweep_expired()
    log.info("sweep_expired() at t=200 -> %s", expired)
    log.info("remaining keys: %d", store.size())
