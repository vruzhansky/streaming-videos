package org.blotter;

import java.util.HashMap;
import java.util.Map;

public class Endpoint {
    final int endpointLatency;

    final Map<Integer, Integer> latencies = new HashMap<>(); // cacheId -> latency

    final Map<Video, Integer> videoRequests = new HashMap<>(); // video -> numOfRequests

    public Endpoint(int endpointLatency) {
        this.endpointLatency = endpointLatency;
    }
}
