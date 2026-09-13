"""
Round-robin load balancer distributing requests evenly across a pool
of servers, skipping any server currently marked unhealthy.

status - completed
"""

from base_logger.logging_event import create_logger


class RoundRobinLoadBalancer:

    log = create_logger(__name__)

    def __init__(self, servers):
        self.servers = list(servers)
        self.healthy = {server: True for server in self.servers}
        self._next_index = 0

    def mark_unhealthy(self, server):
        self.healthy[server] = False

    def mark_healthy(self, server):
        self.healthy[server] = True

    def get_next_server(self):
        total = len(self.servers)
        for _ in range(total):
            server = self.servers[self._next_index]
            self._next_index = (self._next_index + 1) % total
            if self.healthy[server]:
                return server
        return None


if __name__ == "__main__":
    log = create_logger("round_robin_lb_demo")

    lb = RoundRobinLoadBalancer(["s1", "s2", "s3"])

    picks = [lb.get_next_server() for _ in range(6)]
    log.info("6 picks across 3 healthy servers: %s", picks)
    log.info("expected s1,s2,s3,s1,s2,s3 cycling evenly")

    lb.mark_unhealthy("s2")
    picks_with_unhealthy = [lb.get_next_server() for _ in range(4)]
    log.info("s2 marked unhealthy, next 4 picks: %s", picks_with_unhealthy)
    log.info("expected s2 to never appear")

    lb.mark_unhealthy("s1")
    lb.mark_unhealthy("s3")
    log.info("all servers unhealthy -> get_next_server() = %s (expected None)",
             lb.get_next_server())

    lb.mark_healthy("s2")
    log.info("s2 recovers -> get_next_server() = %s", lb.get_next_server())
