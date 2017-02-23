package org.blotter;

import java.util.*;

/**
 * Created by molok on 23.02.2017.
 */
public class Solver {

    private Map<Integer, Set<Video>> cacheEntries = new HashMap<>();
    private int cacheSize;

    public Solver(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Map<Integer, Set<Video>> solve(List<Endpoint> endpoints) {
        endpoints.sort((a, b) -> b.endpointLatency - a.endpointLatency);

        for (Endpoint endpoint : endpoints) {
            List<Map.Entry<Integer, Integer>> lats = new ArrayList<>(endpoint.latencies.entrySet());
            lats.sort(Comparator.comparing(Map.Entry::getValue));
            endpoint.videoRequests.forEach((video, reqCount) -> {
                findFirstAvailableCache(video, lats, endpoint.endpointLatency).ifPresent(cacheId -> {
                    cacheEntries.computeIfAbsent(cacheId, HashSet::new).add(video);
                });
            });
        }
        return cacheEntries;
    }

    private Optional<Integer> findFirstAvailableCache(Video video, List<Map.Entry<Integer, Integer>> lats, int endpointLatency) {
        for (int i = 0; i < lats.size(); i++) {
            Map.Entry<Integer, Integer> bestCacheConnection = lats.get(i);
            Integer cacheId = bestCacheConnection.getKey();
            if (emptySpace(cacheId, cacheEntries) >= video.getSize()) {
                if (bestCacheConnection.getValue() < endpointLatency) {
                    return Optional.of(cacheId);
                }
            }
        }
        return Optional.empty();
    }

    private int emptySpace(int cache, Map<Integer, Set<Video>> caches) {
        return cacheSize - caches.getOrDefault(cache, new HashSet<>()).stream().mapToInt(Video::getSize).sum();
    }

}
