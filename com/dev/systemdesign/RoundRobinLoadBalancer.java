/*
Distribute requests across a pool of servers round-robin, skipping servers currently
marked unhealthy, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.dev.logger.basePrinter;

class RoundRobinLoadBalancer extends basePrinter{

    private final List<String> servers;
    private final Set<String> unhealthyServers;
    private int nextIndex = 0;

    RoundRobinLoadBalancer(List<String> servers){
        this.servers = new ArrayList<>(servers);
        this.unhealthyServers = new HashSet<>();
    }

    void markUnhealthy(String server){
        unhealthyServers.add(server);
    }

    void markHealthy(String server){
        unhealthyServers.remove(server);
    }

    String getNextServer(){
        if (servers.isEmpty() || unhealthyServers.size() == servers.size()){
            return null; // no healthy servers available
        }

        for (int attempts = 0; attempts < servers.size(); attempts++){
            String candidate = servers.get(nextIndex);
            nextIndex = (nextIndex + 1) % servers.size();
            if (!unhealthyServers.contains(candidate)){
                return candidate;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer(
                List.of("server1", "server2", "server3"));

        logp("routing 6 requests with all servers healthy:");
        for (int i = 0; i < 6; i++){
            logp("request " + i + " -> " + lb.getNextServer());
        }

        lb.markUnhealthy("server2");
        logp("server2 marked unhealthy, routing 6 more requests:");
        for (int i = 0; i < 6; i++){
            logp("request " + i + " -> " + lb.getNextServer());
        }

        lb.markHealthy("server2");
        logp("server2 recovered, routing 3 more requests:");
        for (int i = 0; i < 3; i++){
            logp("request " + i + " -> " + lb.getNextServer());
        }
    }
}
