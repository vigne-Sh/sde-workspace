"""
Twitter-snowflake-style unique ID generator: packs a millisecond
timestamp, worker id, and per-millisecond sequence number into a
single 64-bit integer, so IDs are unique, roughly time-sortable, and
generatable without central coordination.

status - completed
"""

import threading
import time

from base_logger.logging_event import create_logger


class SnowflakeIdGenerator:

    log = create_logger(__name__)

    WORKER_ID_BITS = 10
    SEQUENCE_BITS = 12
    MAX_WORKER_ID = (1 << WORKER_ID_BITS) - 1
    MAX_SEQUENCE = (1 << SEQUENCE_BITS) - 1
    CUSTOM_EPOCH_MS = 1700000000000  # arbitrary fixed epoch

    def __init__(self, worker_id: int, clock=time.time):
        if worker_id < 0 or worker_id > self.MAX_WORKER_ID:
            raise ValueError(f"worker_id must be between 0 and {self.MAX_WORKER_ID}")
        self.worker_id = worker_id
        self.clock = clock
        self.sequence = 0
        self.last_timestamp_ms = -1
        self.lock = threading.Lock()

    def _now_ms(self):
        return int(self.clock() * 1000)

    def next_id(self) -> int:
        with self.lock:
            timestamp_ms = self._now_ms()

            if timestamp_ms < self.last_timestamp_ms:
                raise RuntimeError("clock moved backwards, refusing to generate id")

            if timestamp_ms == self.last_timestamp_ms:
                self.sequence = (self.sequence + 1) & self.MAX_SEQUENCE
                if self.sequence == 0:
                    while timestamp_ms <= self.last_timestamp_ms:
                        timestamp_ms = self._now_ms()
            else:
                self.sequence = 0

            self.last_timestamp_ms = timestamp_ms

            shift = self.WORKER_ID_BITS + self.SEQUENCE_BITS
            id_value = (timestamp_ms - self.CUSTOM_EPOCH_MS) << shift
            id_value |= self.worker_id << self.SEQUENCE_BITS
            id_value |= self.sequence
            return id_value


if __name__ == "__main__":
    log = create_logger("snowflake_id_demo")

    generator = SnowflakeIdGenerator(worker_id=5)

    ids = [generator.next_id() for _ in range(10)]
    log.info("generated 10 ids: %s", ids)
    log.info("all unique: %s", len(set(ids)) == len(ids))
    log.info("monotonically increasing: %s", ids == sorted(ids))

    other_worker = SnowflakeIdGenerator(worker_id=6)
    from_generator = [generator.next_id() for _ in range(5)]
    from_other_worker = [other_worker.next_id() for _ in range(5)]
    cross_worker_ids = from_generator + from_other_worker
    log.info("ids from two workers, all unique: %s",
             len(set(cross_worker_ids)) == len(cross_worker_ids))
