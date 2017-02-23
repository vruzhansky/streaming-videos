package org.blotter;

import java.util.Map;

public class Endpoint {
    int endpointLatency;

    Map<Integer, Integer> latencies; // cacheId -> latency

    Map<Video, Integer> videoRequests; // video -> numOfRequests
}
