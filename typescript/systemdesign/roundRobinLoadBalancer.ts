/*
Round-robin load balancer: cycles through a pool of servers in order,
skipping any currently marked unhealthy, so traffic keeps flowing evenly
around outages.

status - completed
*/

import { logp } from "../utils/logger";

class RoundRobinLoadBalancer {
  private readonly servers: string[];
  private readonly healthy: Set<string>;
  private cursor = 0;

  constructor(servers: string[]) {
    if (servers.length === 0) {
      throw new Error("server pool cannot be empty");
    }
    this.servers = [...servers];
    this.healthy = new Set(servers);
  }

  markUnhealthy(server: string): void {
    this.healthy.delete(server);
  }

  markHealthy(server: string): void {
    if (this.servers.includes(server)) {
      this.healthy.add(server);
    }
  }

  next(): string {
    if (this.healthy.size === 0) {
      throw new Error("no healthy servers available");
    }
    for (let attempts = 0; attempts < this.servers.length; attempts++) {
      const candidate = this.servers[this.cursor];
      this.cursor = (this.cursor + 1) % this.servers.length;
      if (this.healthy.has(candidate)) {
        return candidate;
      }
    }
    throw new Error("no healthy servers available");
  }
}

// usage scenarios

const balancer = new RoundRobinLoadBalancer(["server-a", "server-b", "server-c"]);

const firstRound = Array.from({ length: 6 }, () => balancer.next());
logp(`round robin over 6 requests, all healthy: ${firstRound.join(", ")}`);

balancer.markUnhealthy("server-b");
const secondRound = Array.from({ length: 6 }, () => balancer.next());
logp(`server-b marked unhealthy, next 6 requests skip it: ${secondRound.join(", ")}`);
logp(`server-b appears in that batch: ${secondRound.includes("server-b")} (expected false)`);

balancer.markHealthy("server-b");
const thirdRound = Array.from({ length: 3 }, () => balancer.next());
logp(`server-b marked healthy again, rejoins rotation: ${thirdRound.join(", ")}`);
