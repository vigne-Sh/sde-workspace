/*
Twitter-Snowflake-style ID generator: packs a millisecond timestamp, a
worker id, and a per-millisecond sequence number into a single 64-bit id
that is roughly time-sortable. Uses bigint since 64-bit values don't fit
safely in a JS number.

status - completed
*/

import { logp } from "../utils/logger";

const WORKER_ID_BITS = 10n;
const SEQUENCE_BITS = 12n;
const MAX_WORKER_ID = (1n << WORKER_ID_BITS) - 1n;
const MAX_SEQUENCE = (1n << SEQUENCE_BITS) - 1n;
const WORKER_ID_SHIFT = SEQUENCE_BITS;
const TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
// arbitrary custom epoch (2024-01-01T00:00:00Z) so timestamps stay small
const CUSTOM_EPOCH_MS = 1704067200000n;

class SnowflakeIdGenerator {
  private readonly workerId: bigint;
  private lastTimestampMs = -1n;
  private sequence = 0n;

  constructor(workerId: number, private readonly nowFn: () => number = Date.now) {
    const workerIdBig = BigInt(workerId);
    if (workerIdBig < 0n || workerIdBig > MAX_WORKER_ID) {
      throw new Error(`workerId must be between 0 and ${MAX_WORKER_ID}`);
    }
    this.workerId = workerIdBig;
  }

  private currentTimeMs(): bigint {
    return BigInt(this.nowFn());
  }

  nextId(): bigint {
    let timestamp = this.currentTimeMs();

    if (timestamp < this.lastTimestampMs) {
      throw new Error("clock moved backwards, refusing to generate id");
    }

    if (timestamp === this.lastTimestampMs) {
      this.sequence = (this.sequence + 1n) & MAX_SEQUENCE;
      if (this.sequence === 0n) {
        // sequence exhausted for this millisecond, spin to the next one
        while (timestamp <= this.lastTimestampMs) {
          timestamp = this.currentTimeMs();
        }
      }
    } else {
      this.sequence = 0n;
    }

    this.lastTimestampMs = timestamp;

    const relativeTimestamp = timestamp - CUSTOM_EPOCH_MS;
    return (relativeTimestamp << TIMESTAMP_SHIFT) | (this.workerId << WORKER_ID_SHIFT) | this.sequence;
  }

  static decompose(id: bigint): { timestampMs: bigint; workerId: bigint; sequence: bigint } {
    const sequence = id & MAX_SEQUENCE;
    const workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    const timestampMs = (id >> TIMESTAMP_SHIFT) + CUSTOM_EPOCH_MS;
    return { timestampMs, workerId, sequence };
  }
}

// usage scenarios

const generator = new SnowflakeIdGenerator(7);

const ids: bigint[] = [];
for (let i = 0; i < 5; i++) {
  ids.push(generator.nextId());
}
logp(`generated ids: ${ids.join(", ")}`);

const sorted = [...ids].sort((a, b) => (a < b ? -1 : a > b ? 1 : 0));
const isSortedAsGenerated = ids.every((id, i) => id === sorted[i]);
logp(`ids are monotonically increasing as generated: ${isSortedAsGenerated}`);

const decoded = SnowflakeIdGenerator.decompose(ids[0]);
logp(`decoded first id -> workerId=${decoded.workerId}, sequence=${decoded.sequence}, timestampMs=${decoded.timestampMs}`);

const otherWorker = new SnowflakeIdGenerator(3);
const idFromOtherWorker = otherWorker.nextId();
logp(`id from a different worker: ${idFromOtherWorker}, unique from generator 7's ids: ${!ids.includes(idFromOtherWorker)}`);
