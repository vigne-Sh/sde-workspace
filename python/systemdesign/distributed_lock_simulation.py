"""
Simulation of a distributed lock service using lease-based locks with
expiry, similar in spirit to a Redis/Zookeeper-backed mutex. A client
can only acquire a lock if it's free or its lease has expired, and
only the current lease holder can release or renew it, preventing two
clients from holding the same lock at once.

status - completed
"""

from base_logger.logging_event import create_logger


class LockDeniedError(Exception):
    pass


class DistributedLockService:

    log = create_logger(__name__)

    def __init__(self, clock):
        self.clock = clock
        self.locks = {}

    def _is_expired(self, resource):
        entry = self.locks.get(resource)
        return entry is not None and self.clock() >= entry["expires_at"]

    def acquire(self, resource, client_id, lease_seconds):
        entry = self.locks.get(resource)
        if entry is not None and not self._is_expired(resource) and entry["client_id"] != client_id:
            raise LockDeniedError(
                f"resource '{resource}' held by another client until {entry['expires_at']}"
            )

        self.locks[resource] = {
            "client_id": client_id,
            "expires_at": self.clock() + lease_seconds,
        }
        return True

    def release(self, resource, client_id):
        entry = self.locks.get(resource)
        if entry is None:
            return False
        if entry["client_id"] != client_id and not self._is_expired(resource):
            raise LockDeniedError(f"client {client_id} does not hold the lock on '{resource}'")
        del self.locks[resource]
        return True

    def is_locked(self, resource):
        return resource in self.locks and not self._is_expired(resource)


if __name__ == "__main__":
    log = create_logger("distributed_lock_demo")

    fake_time = [0.0]

    def fake_clock():
        return fake_time[0]

    lock_service = DistributedLockService(clock=fake_clock)

    lock_service.acquire("resource-1", client_id="client-A", lease_seconds=5)
    log.info("client-A acquired resource-1, is_locked=%s", lock_service.is_locked("resource-1"))

    try:
        lock_service.acquire("resource-1", client_id="client-B", lease_seconds=5)
    except LockDeniedError as exc:
        log.info("client-B denied while lease active: %s", exc)

    fake_time[0] = 6.0
    log.info("after lease expires (t=6), client-B acquires: %s",
             lock_service.acquire("resource-1", client_id="client-B", lease_seconds=5))

    try:
        lock_service.release("resource-1", client_id="client-A")
    except LockDeniedError as exc:
        log.info("client-A cannot release a lock it no longer holds: %s", exc)

    released = lock_service.release("resource-1", client_id="client-B")
    log.info("client-B releases resource-1: %s, is_locked=%s",
             released, lock_service.is_locked("resource-1"))
