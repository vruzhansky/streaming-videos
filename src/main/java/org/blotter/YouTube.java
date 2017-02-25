package org.blotter;

import java.util.*;
import java.util.stream.Collectors;

class YouTube {
    final int numOfCaches;
    final int cacheSize;
    final List<Endpoint> endpoints;
    final List<Video> mostRequestedVideos;

    YouTube(int numOfCaches, int cacheSize, Collection<Endpoint> endpoints) {
        this.numOfCaches = numOfCaches;
        this.cacheSize = cacheSize;
        this.endpoints = new ArrayList<>(endpoints);
        this.mostRequestedVideos = mostRequestedVideos(endpoints);
    }

    private List<Video> mostRequestedVideos(Collection<Endpoint> endpoints) {
        Map<Video, Integer> totalRequestsNum = new HashMap<>();
        endpoints.stream()
                .map(endpoint -> endpoint.videoRequests)
                .flatMap(videoRequests -> videoRequests.entrySet().stream())
                .forEach(entry -> totalRequestsNum.compute(entry.getKey(),
                        (video, requests) -> requests == null
                                ? entry.getValue()
                                : requests + entry.getValue()));

        return totalRequestsNum.entrySet().stream().sorted((a, b) -> {
            int c = b.getValue() - a.getValue();
            if (c == 0) {
                return a.getKey().size - b.getKey().size;
            } else {
                return c;
            }
        }).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "YouTube{" +
                "numOfCaches=" + numOfCaches +
                ", cacheSize=" + cacheSize +
                ", endpoints=" + endpoints +
                ", mostRequestedVideos=" + mostRequestedVideos +
                '}';
    }
}
