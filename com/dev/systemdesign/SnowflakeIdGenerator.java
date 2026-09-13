/*
Twitter Snowflake style unique ID generator, packs a timestamp, worker id and a
per-millisecond sequence into a single 64-bit long so IDs are roughly time-sortable,
common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import com.dev.logger.basePrinter;

class SnowflakeIdGenerator extends basePrinter{

    private static final long CUSTOM_EPOCH = 1700000000000L; // fixed reference epoch
    private static final long WORKER_ID_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    SnowflakeIdGenerator(long workerId){
        if (workerId < 0 || workerId > MAX_WORKER_ID){
            throw new IllegalArgumentException("workerId out of range: 0.." + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }

    synchronized long nextId(){
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp){
            throw new IllegalStateException("clock moved backwards, refusing to generate id");
        }

        if (timestamp == lastTimestamp){
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0){
                // sequence exhausted for this millisecond, spin to next ms
                while (timestamp <= lastTimestamp){
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator generatorA = new SnowflakeIdGenerator(1);
        SnowflakeIdGenerator generatorB = new SnowflakeIdGenerator(2);

        long previous = -1;
        boolean monotonic = true;
        for (int i = 0; i < 10; i++){
            long id = generatorA.nextId();
            logp("generatorA id " + i + " -> " + id);
            if (id <= previous){
                monotonic = false;
            }
            previous = id;
        }
        logp("ids from generatorA strictly increasing -> " + monotonic);

        long idFromA = generatorA.nextId();
        long idFromB = generatorB.nextId();
        logp("idFromA -> " + idFromA);
        logp("idFromB -> " + idFromB);
        logp("different workers produce different ids -> " + (idFromA != idFromB));
    }
}
