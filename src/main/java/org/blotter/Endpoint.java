package org.blotter;

import java.util.*;

class Endpoint {
    final int endpointLatency;

    final Map<Integer, Integer> latencies = new HashMap<>(); // cacheId -> latency

    final List<Map.Entry<Integer, Integer>> sortedLats = new ArrayList<>();

    final Map<Video, Integer> videoRequests = new HashMap<>(); // video -> numOfRequests

    Endpoint(int endpointLatency) {
        this.endpointLatency = endpointLatency;
    }

    void sortLats() {
        sortedLats.addAll(latencies.entrySet());
        sortedLats.sort(Comparator.comparing(Map.Entry::getValue));
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "endpointLatency=" + endpointLatency +
                ", latencies=" + latencies +
                ", videoRequests=" + videoRequests +
                '}';
    }
}
