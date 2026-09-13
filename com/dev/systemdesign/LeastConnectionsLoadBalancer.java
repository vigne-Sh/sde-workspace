/*
Route each request to the server with the fewest active connections, releasing the
connection once the request completes, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import java.util.List;
import com.dev.logger.basePrinter;

class LeastConnectionsLoadBalancer extends basePrinter{

    private final HashMap<String, Integer> activeConnections;

    LeastConnectionsLoadBalancer(List<String> servers){
        this.activeConnections = new HashMap<>();
        for (String server : servers){
            activeConnections.put(server, 0);
        }
    }

    synchronized String acquireServer(){
        String chosen = null;
        int minConnections = Integer.MAX_VALUE;
        for (var entry : activeConnections.entrySet()){
            if (entry.getValue() < minConnections){
                minConnections = entry.getValue();
                chosen = entry.getKey();
            }
        }
        if (chosen != null){
            activeConnections.put(chosen, activeConnections.get(chosen) + 1);
        }
        return chosen;
    }

    synchronized void releaseServer(String server){
        activeConnections.computeIfPresent(server, (k, v) -> Math.max(0, v - 1));
    }

    synchronized int getActiveConnections(String server){
        return activeConnections.getOrDefault(server, 0);
    }

    public static void main(String[] args) {
        LeastConnectionsLoadBalancer lb = new LeastConnectionsLoadBalancer(
                List.of("server1", "server2", "server3"));

        // all servers start at 0 connections, so the first three acquires spread one
        // connection to each server (exact order depends on map iteration, not guaranteed)
        String s = lb.acquireServer();
        logp("first acquire -> " + s);

        lb.acquireServer();
        lb.acquireServer();

        logp("connections after 3 acquires: server1=" + lb.getActiveConnections("server1")
                + " server2=" + lb.getActiveConnections("server2")
                + " server3=" + lb.getActiveConnections("server3"));

        lb.releaseServer("server1");
        logp("released server1, connections now: server1=" + lb.getActiveConnections("server1")
                + " server2=" + lb.getActiveConnections("server2")
                + " server3=" + lb.getActiveConnections("server3"));

        String nextPick = lb.acquireServer();
        logp("next acquire should favor least-loaded server -> " + nextPick);
    }
}
