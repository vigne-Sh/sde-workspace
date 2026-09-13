/*
Least-connections load balancer: routes each new request to whichever
healthy server currently has the fewest active connections, and expects
the caller to release the connection when the request finishes.

status - completed
*/

import { logp } from "../utils/logger";

class LeastConnectionsLoadBalancer {
  private readonly activeConnections: Map<string, number> = new Map();
  private readonly healthy: Set<string>;

  constructor(servers: string[]) {
    if (servers.length === 0) {
      throw new Error("server pool cannot be empty");
    }
    for (const server of servers) {
      this.activeConnections.set(server, 0);
    }
    this.healthy = new Set(servers);
  }

  markUnhealthy(server: string): void {
    this.healthy.delete(server);
  }

  markHealthy(server: string): void {
    if (this.activeConnections.has(server)) {
      this.healthy.add(server);
    }
  }

  acquire(): string {
    let chosen: string | undefined;
    let lowest = Infinity;
    for (const server of this.healthy) {
      const count = this.activeConnections.get(server)!;
      if (count < lowest) {
        lowest = count;
        chosen = server;
      }
    }
    if (!chosen) {
      throw new Error("no healthy servers available");
    }
    this.activeConnections.set(chosen, lowest + 1);
    return chosen;
  }

  release(server: string): void {
    const count = this.activeConnections.get(server);
    if (count !== undefined && count > 0) {
      this.activeConnections.set(server, count - 1);
    }
  }

  connectionCounts(): Record<string, number> {
    return Object.fromEntries(this.activeConnections);
  }
}

// usage scenarios

const balancer = new LeastConnectionsLoadBalancer(["server-a", "server-b", "server-c"]);

// server-a gets loaded up with long-running connections first
const busyOnA = [balancer.acquire(), balancer.acquire(), balancer.acquire()];
logp(`after 3 acquires (all go to least-loaded, initially tied): ${JSON.stringify(balancer.connectionCounts())}`);
logp(`those 3 requests landed on: ${busyOnA.join(", ")}`);

// release two of them so their server is free again
balancer.release(busyOnA[0]);
balancer.release(busyOnA[1]);
logp(`after releasing 2 connections: ${JSON.stringify(balancer.connectionCounts())}`);

const next = balancer.acquire();
logp(`next request routed to "${next}" (the server with fewest active connections)`);
logp(`final connection counts: ${JSON.stringify(balancer.connectionCounts())}`);
