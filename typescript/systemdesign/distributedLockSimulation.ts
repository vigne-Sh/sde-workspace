/*
Simulates a distributed lock service with lease/expiry semantics: a client
acquires a named lock with a time-limited lease (fencing token included),
must renew before it expires to keep holding it, and an expired lease can
be stolen by another client - preventing two clients from holding the same
lock at once.

status - completed
*/

import { logp } from "../utils/logger";

interface Lease {
  ownerId: string;
  fencingToken: number;
  expiresAtMs: number;
}

class LockAcquisitionError extends Error {}

class DistributedLockSimulation {
  private readonly leasesByLockName: Map<string, Lease> = new Map();
  private nextFencingToken = 1;

  constructor(private readonly nowFn: () => number = Date.now) {}

  private isExpired(lease: Lease): boolean {
    return this.nowFn() >= lease.expiresAtMs;
  }

  acquire(lockName: string, clientId: string, leaseDurationMs: number): number {
    const existing = this.leasesByLockName.get(lockName);
    if (existing && !this.isExpired(existing) && existing.ownerId !== clientId) {
      throw new LockAcquisitionError(`lock "${lockName}" is held by "${existing.ownerId}"`);
    }

    const fencingToken = this.nextFencingToken++;
    this.leasesByLockName.set(lockName, {
      ownerId: clientId,
      fencingToken,
      expiresAtMs: this.nowFn() + leaseDurationMs,
    });
    return fencingToken;
  }

  renew(lockName: string, clientId: string, leaseDurationMs: number): void {
    const existing = this.leasesByLockName.get(lockName);
    if (!existing || this.isExpired(existing) || existing.ownerId !== clientId) {
      throw new LockAcquisitionError(`"${clientId}" does not hold a valid lease on "${lockName}"`);
    }
    existing.expiresAtMs = this.nowFn() + leaseDurationMs;
  }

  release(lockName: string, clientId: string): void {
    const existing = this.leasesByLockName.get(lockName);
    if (existing && existing.ownerId === clientId) {
      this.leasesByLockName.delete(lockName);
    }
  }

  currentOwner(lockName: string): string | undefined {
    const existing = this.leasesByLockName.get(lockName);
    if (!existing || this.isExpired(existing)) {
      return undefined;
    }
    return existing.ownerId;
  }
}

// usage scenarios: fake clock, two competing clients

let simulatedNow = 0;
const lockService = new DistributedLockSimulation(() => simulatedNow);

const tokenA = lockService.acquire("job-runner", "client-A", 500);
logp(`client-A acquired lock with fencing token ${tokenA}`);

try {
  lockService.acquire("job-runner", "client-B", 500);
} catch (err) {
  logp(`client-B failed to acquire while A holds it: ${(err as Error).message}`);
}

logp(`current owner -> ${lockService.currentOwner("job-runner")}`);

logp("advancing clock by 200ms, client-A renews before expiry");
simulatedNow += 200;
lockService.renew("job-runner", "client-A", 500);
logp(`current owner still -> ${lockService.currentOwner("job-runner")}`);

logp("advancing clock by 600ms without renewal, lease expires");
simulatedNow += 600;
logp(`current owner after expiry -> ${lockService.currentOwner("job-runner")} (expected undefined)`);

const tokenB = lockService.acquire("job-runner", "client-B", 500);
logp(`client-B acquired expired lock, new fencing token ${tokenB} (must be greater than A's token ${tokenA}: ${tokenB > tokenA})`);

try {
  lockService.renew("job-runner", "client-A", 500);
} catch (err) {
  logp(`client-A can no longer renew, lock stolen: ${(err as Error).message}`);
}
