/*
Probabilistic set membership structure using multiple independent hash functions over
a shared bit array, no false negatives but possible false positives, common system
design / data structure interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.BitSet;
import com.dev.logger.basePrinter;

class BloomFilter extends basePrinter{

    private final BitSet bits;
    private final int bitArraySize;
    private final int numHashFunctions;

    BloomFilter(int bitArraySize, int numHashFunctions){
        this.bitArraySize = bitArraySize;
        this.numHashFunctions = numHashFunctions;
        this.bits = new BitSet(bitArraySize);
    }

    // double hashing technique: derive numHashFunctions positions from two base hashes
    private int[] getPositions(String value){
        int hash1 = value.hashCode();
        int hash2 = smearHash(hash1);

        int[] positions = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++){
            int combined = hash1 + i * hash2;
            int position = Math.floorMod(combined, bitArraySize);
            positions[i] = position;
        }
        return positions;
    }

    private int smearHash(int h){
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    void add(String value){
        for (int position : getPositions(value)){
            bits.set(position);
        }
    }

    boolean mightContain(String value){
        for (int position : getPositions(value)){
            if (!bits.get(position)){
                return false; // definitely not present
            }
        }
        return true; // probably present (could be a false positive)
    }

    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter(1000, 5);

        String[] added = {"apple", "banana", "cherry", "date", "elderberry"};
        for (String fruit : added){
            filter.add(fruit);
        }

        logp("checking membership for added items:");
        for (String fruit : added){
            logp(fruit + " mightContain -> " + filter.mightContain(fruit)); // all true
        }

        String[] notAdded = {"grape", "kiwi", "mango", "lemon", "peach"};
        int falsePositives = 0;
        logp("checking membership for items never added:");
        for (String fruit : notAdded){
            boolean result = filter.mightContain(fruit);
            logp(fruit + " mightContain -> " + result);
            if (result){
                falsePositives++;
            }
        }
        logp("false positives out of " + notAdded.length + " unadded items -> " + falsePositives);
        logp("(bloom filters never false-negative, may occasionally false-positive)");
    }
}
