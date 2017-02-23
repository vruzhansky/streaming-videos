package org.blotter;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {

    private Map<Integer, Set<Video>> cacheEntries = new HashMap<>();
    private int cacheSize;

    public Solver(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public double avgScore(Video video, List<Endpoint> endpoints) {
        long hitsum = 0;
        long latsum = 0;
        for (Endpoint endpoint : endpoints) {
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

        return totalRequestsNum.entrySet().stream().sorted((a, b) -> {
            int c = b.getValue() - a.getValue();
            if(c==0) {
                return a.getKey().getSize() - b.getKey().getSize();
            }else{
                return c;
            }

        }).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Optional<Integer> findClosestCacheWithVideo(Video video, Endpoint endpoint) {
        Map<Integer, Integer> cacheIdToLatency = new HashMap<>();
        cacheEntries.entrySet().stream().filter(e -> e.getValue().contains(video)).forEach(e -> {
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

    public List<Endpoint> endpointsWithVideo(Video video, List<Endpoint> endpoints){
        return endpoints.stream().filter(endpoint -> endpoint.videoRequests.containsKey(video)).sorted((e1,e2)->e2.endpointLatency - e1.endpointLatency).collect(Collectors.toList());
    }

    public Map<Integer, Set<Video>> solve(List<Endpoint> endpoints) {
        mostRequestedVideos(endpoints).forEach(video->{
            endpointsWithVideo(video, endpoints).forEach(endpoint -> {
                List<Map.Entry<Integer, Integer>> lats = new ArrayList<>(endpoint.latencies.entrySet());
                lats.sort(Comparator.comparing(Map.Entry::getValue));
                    Integer bestCache = null;
                    Double bestScore = Double.MAX_VALUE;
                    for (Integer cache : findAllCachesWithSpaceForVideoConnectedToEndpoint(video, endpoint)) {
                        double before = avgScore(video, endpoints);
                        cacheEntries.computeIfAbsent(cache, HashSet::new).add(video);
                        double after = avgScore(video, endpoints);
                        if (after < before) {
//                        System.out.println("BEFORE avgScore(video,reqCount) = " + before + "\nAFTER avgScore(video,reqCount) = " + after);
                            if (after < bestScore) {
                                bestScore = after;
                                bestCache = cache;
                            }
                        }
                        cacheEntries.computeIfAbsent(cache, HashSet::new).remove(video);
                    }
                    if (bestCache != null) {
                        cacheEntries.computeIfAbsent(bestCache, HashSet::new).add(video);
                    }
            });
        });
        return cacheEntries;

    }

    private List<Integer> findAllCachesWithSpaceForVideoConnectedToEndpoint(Video video, Endpoint endpoint) {
        return endpoint.latencies.keySet().stream().filter(cacheId -> emptySpace(cacheId) >= video.getSize()).collect(Collectors.toList());
    }

    private Optional<Integer> findFirstAvailableCache(Video video, List<Map.Entry<Integer, Integer>> lats, int endpointLatency) {

        for (int i = 0; i < lats.size(); i++) {
            Map.Entry<Integer, Integer> bestCacheConnection = lats.get(i);
            Integer cacheId = bestCacheConnection.getKey();
            if (emptySpace(cacheId) >= video.getSize()) {
                if (bestCacheConnection.getValue() < endpointLatency) {
                    return Optional.of(cacheId);
                }
            }
        }
        return Optional.empty();
    }

    private int emptySpace(int cache) {
        return cacheSize - cacheEntries.getOrDefault(cache, new HashSet<>()).stream().mapToInt(Video::getSize).sum();
    }

}
