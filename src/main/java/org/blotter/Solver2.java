package org.blotter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solver2 {

    private Map<Integer, Set<Video>> cacheEntries = new HashMap<>();
    private int cacheSize;

    public Solver2(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Map<Integer, Set<Video>> solve(List<Endpoint> endpoints) {
        endpoints.sort((a, b) -> b.endpointLatency - a.endpointLatency);
        mostRequestedVideos(endpoints)

        for (Endpoint endpoint : endpoints) {
            List<Map.Entry<Integer, Integer>> lats = new ArrayList<>(endpoint.latencies.entrySet());
            lats.sort(Comparator.comparing(Map.Entry::getValue));
            endpoint.videoRequests.forEach((Video video, Integer count) -> {
                findFirstAvailableCache(video, lats, endpoint.endpointLatency).ifPresent(cacheId -> {
                    cacheEntries.computeIfAbsent(cacheId, HashSet::new).add(video);
                });
            });
        }
        return cacheEntries;
    }

    private List<Video> mostRequestedVideos(List<Endpoint> endpoints) {
        Map<Video, Integer> totalRequestsNum = new HashMap<>();
        endpoints.stream()
                .map(endpoint -> endpoint.videoRequests)
                .flatMap(videoRequests -> videoRequests.entrySet().stream())
                .forEach(entry -> {
                    if (totalRequestsNum.containsKey(entry.getKey())) {
                        totalRequestsNum.put(entry.getKey(), totalRequestsNum.get(entry.getKey()) + entry.getValue());
                    } else {
                        totalRequestsNum.put(entry.getKey(), entry.getValue());
                    }
                });

        return totalRequestsNum.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
