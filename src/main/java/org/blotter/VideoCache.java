package org.blotter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VideoCache {

    public static void main(String[] args) throws Exception {

        String filename;
        if (0 < args.length) {
            filename = args[0];
        } else {
            filename = getFilenameFromSystemIn();
        }

//        Stopwatch stopwatch = Stopwatch.createStarted();
        YouTube youTube = readFileToYouTube(Paths.get("data/" + filename).toAbsolutePath().toFile());

        System.out.println(youTube);

        Map<Integer, List<Video>> cacheEntries = new Solver(youTube.cacheSize).solve(youTube.endpoints);

        List<Map.Entry<Integer, List<Video>>> solution = cacheEntries.entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toList());
        System.out.println(solution.size());
        solution.forEach(cacheEntry->{
            System.out.print(cacheEntry.getKey());
            for (Video video : cacheEntry.getValue()) {
                System.out.print(" "+video.getId());
            }
            System.out.println();
        });

        System.out.println(youTube);
    }

    private static String getFilenameFromSystemIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a file name: ");
        System.out.flush();
        return scanner.nextLine();
    }

    private static YouTube readFileToYouTube(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String[] s = br.readLine().split("\\s+");

            int numOfVideos = Integer.parseInt(s[0]);
            int numOfEndpoints = Integer.parseInt(s[1]);
            int numOfRequestsDescriptions = Integer.parseInt(s[2]);
            int numOfCaches = Integer.parseInt(s[3]);
            int cacheSize = Integer.parseInt(s[4]);

            Map<Integer, Endpoint> endpoints = new HashMap<>();

            // videos
            s = br.readLine().split("\\s+");
            Map<Integer, Video> videos = new HashMap<>();
            for (int i = 0; i < numOfVideos; i++) {
                videos.put(i, new Video(i, Integer.parseInt(s[i])));
            }

            // endpoints
            for (int i = 0; i < numOfEndpoints; i++) {
                s = br.readLine().split("\\s+");

                int endpointLatency = Integer.parseInt(s[0]);
                int connectedCaches = Integer.parseInt(s[1]);

                Endpoint endpoint = new Endpoint(endpointLatency);

                for (int j = 0; j < connectedCaches; j++) {
                    s = br.readLine().split("\\s+");
                    int cacheId = Integer.parseInt(s[0]);
                    int latency = Integer.parseInt(s[1]);

                    endpoint.latencies.put(cacheId, latency);
                }
                endpoints.put(i, endpoint);
            }

            // video requests
            for (int i = 0; i < numOfRequestsDescriptions; i++) {
                s = br.readLine().split("\\s+");

                int videoId = Integer.parseInt(s[0]);
                int endpointId = Integer.parseInt(s[1]);
                int numOfRequests = Integer.parseInt(s[2]);

                endpoints.get(endpointId).videoRequests.put(videos.get(videoId), numOfRequests);
            }

            return new YouTube(numOfCaches, cacheSize, endpoints.values());
        }
    }
}
