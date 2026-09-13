"""
Least-connections load balancer: routes each new request to whichever
healthy server currently has the fewest active connections, and
connections are released when a request finishes.

status - completed
"""

from base_logger.logging_event import create_logger


class LeastConnectionsLoadBalancer:

    log = create_logger(__name__)

    def __init__(self, servers):
        self.servers = list(servers)
        self.active_connections = {server: 0 for server in self.servers}
        self.healthy = {server: True for server in self.servers}

    def mark_unhealthy(self, server):
        self.healthy[server] = False

    def mark_healthy(self, server):
        self.healthy[server] = True

    def acquire(self):
        candidates = [s for s in self.servers if self.healthy[s]]
        if not candidates:
            return None
        chosen = min(candidates, key=lambda s: (self.active_connections[s], self.servers.index(s)))
        self.active_connections[chosen] += 1
        return chosen

    def release(self, server):
        if self.active_connections.get(server, 0) > 0:
            self.active_connections[server] -= 1


if __name__ == "__main__":
    log = create_logger("least_connections_lb_demo")

    lb = LeastConnectionsLoadBalancer(["s1", "s2", "s3"])

    s1 = lb.acquire()
    s2 = lb.acquire()
    log.info("acquired for req1=%s, req2=%s -> %s", s1, s2, lb.active_connections)

    s3 = lb.acquire()
    log.info("acquired for req3=%s (expected s3, still 0 connections)", s3)
    log.info("connection counts: %s", lb.active_connections)

    lb.release(s1)
    log.info("released %s, counts now: %s", s1, lb.active_connections)

    s4 = lb.acquire()
    log.info("acquired for req4=%s (expected %s, tied for fewest connections)", s4, s1)
    log.info("final connection counts: %s", lb.active_connections)

    lb.mark_unhealthy("s2")
    lb.mark_unhealthy("s3")
    lb.mark_unhealthy("s1")
    log.info("all servers unhealthy -> acquire() = %s (expected None)", lb.acquire())
