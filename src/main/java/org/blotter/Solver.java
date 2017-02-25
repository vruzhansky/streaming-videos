package org.blotter;

import java.util.*;
import java.util.stream.Collectors;

class Solver {
    private final YouTube youTube;

    private final Map<Integer, Set<Video>> cachedVideos = new HashMap<>(); // cacheId -> videos in cache

    Solver(YouTube youTube) {
        this.youTube = youTube;
    }

    private double avgScore(Video video) {
        long hitsum = 0;
        long latsum = 0;
        for (Endpoint endpoint : youTube.endpoints) {
            if (endpoint.videoRequests.containsKey(video)) {
                Optional<Integer> nearestCache = findClosestCacheWithVideo(video, endpoint);
                Integer hits = endpoint.videoRequests.get(video);
                int lat = nearestCache.map(endpoint.latencies::get).orElseGet(() -> endpoint.endpointLatency);
                hitsum += hits;
                latsum += (lat * hits);
            }
        }
        if (hitsum != 0) {
            return latsum / hitsum;
        } else {
            return Double.MAX_VALUE;
        }
    }

    private Optional<Integer> findClosestCacheWithVideo(Video video, Endpoint endpoint) {
        Map<Integer, Integer> cacheIdToLatency = new HashMap<>();
        cachedVideos.entrySet().stream().filter(e -> e.getValue().contains(video)).forEach(e -> {
            Integer cacheId = e.getKey();
            if (endpoint.latencies.containsKey(cacheId)) {
                Integer cacheLatency = endpoint.latencies.get(cacheId);
                cacheIdToLatency.put(cacheId, cacheLatency);
            }
        });
        Optional<Map.Entry<Integer, Integer>> max = cacheIdToLatency.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
        if (max.isPresent()) {
            if (max.get().getValue() < endpoint.endpointLatency) {
                return Optional.of(max.get().getKey());
            }
        }
        return Optional.empty();

    }

    private List<Endpoint> endpointsWithVideo(Video video) {
        return youTube.endpoints.stream()
                .filter(endpoint -> endpoint.videoRequests.containsKey(video))
                .sorted((e1, e2) -> e2.videoRequests.get(video) - e1.videoRequests.get(video)).collect(Collectors.toList());
    }

    Map<Integer, Set<Video>> solve() {
        youTube.mostRequestedVideos.forEach(video -> endpointsWithVideo(video).forEach(endpoint -> {
            Integer bestCache = null;
            Double bestScore = Double.MAX_VALUE;
            for (Integer cache : findAllCachesWithSpaceForVideoConnectedToEndpoint(video, endpoint)) {
                double before = avgScore(video);
                cachedVideos.computeIfAbsent(cache, HashSet::new).add(video);
                double after = avgScore(video);
                if (after < before) {
//                        System.out.println("BEFORE avgScore(video,reqCount) = " + before + "\nAFTER avgScore(video,reqCount) = " + after);
                    if (after < bestScore) {
                        bestScore = after;
                        bestCache = cache;
                    }
                }
                cachedVideos.computeIfAbsent(cache, HashSet::new).remove(video);
            }
            if (bestCache != null) {
                cachedVideos.computeIfAbsent(bestCache, HashSet::new).add(video);
            }
//                findFirstAvailableCache(video, lats, endpoint.endpointLatency).ifPresent(cacheId -> {
//                    cachedVideos.computeIfAbsent(cacheId, HashSet::new).add(video);
//                });
        }));

        youTube.mostRequestedVideos.forEach(video -> cachedVideos.forEach((cacheId, videos) -> {
            if (emptySpace(cacheId) >= video.size) {
                cachedVideos.computeIfAbsent(cacheId, HashSet::new).add(video);
            }
        }));

        cachedVideos.forEach((cacheId, videos) -> System.out.println(emptySpace(cacheId)));
        return cachedVideos;

    }

    private List<Integer> findAllCachesWithSpaceForVideoConnectedToEndpoint(Video video, Endpoint endpoint) {
        return endpoint.sortedLats.stream().map(Map.Entry::getKey)
                .filter(cacheId -> emptySpace(cacheId) >= video.size)
                .collect(Collectors.toList());
    }

    private Optional<Integer> findFirstAvailableCache(Video video, Endpoint endpoint) {
        for (int i = 0; i < endpoint.sortedLats.size(); i++) {
            Map.Entry<Integer, Integer> bestCacheConnection = endpoint.sortedLats.get(i);
            Integer cacheId = bestCacheConnection.getKey();
            if (emptySpace(cacheId) >= video.size) {
                if (bestCacheConnection.getValue() < endpoint.endpointLatency) {
                    return Optional.of(cacheId);
                }
            }
        }
        return Optional.empty();
    }

    private int emptySpace(int cache) {
        return youTube.cacheSize - cachedVideos.getOrDefault(cache, new HashSet<>()).stream().mapToInt(video -> video.size).sum();
    }

}
