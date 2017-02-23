package org.blotter;

import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VideoCache {

    public static void main(String[] args) throws Exception {

        System.out.println(ManagementFactory.getRuntimeMXBean().getName());

        String filename;
        if (0 < args.length) {
            filename = args[0];
        } else {
            filename = getFilenameFromSystemIn();
        }

        Stopwatch stopwatch = Stopwatch.createStarted();

    }

    private static String getFilenameFromSystemIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a file name: ");
        System.out.flush();
        return scanner.nextLine();
    }

    private static List<Endpoint> readFileToPizza(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String[] s = br.readLine().split("\\s+");

//            int videos = Integer.parseInt(s[0]);
            int endpoints = Integer.parseInt(s[1]);
            int requests = Integer.parseInt(s[2]);
            int caches = Integer.parseInt(s[3]);
            int cacheSize = Integer.parseInt(s[4]);

            s = br.readLine().split("\\s+");

            // videos
            List<Video> videos = new ArrayList<>();
            int videoId = 0;
            for (String ss: s) {
                videos.add(new Video(videoId, Integer.parseInt(ss)));
                videoId ++;
            }

            for (int i = 0; i<endpoints; i++) {
                s = br.readLine().split("\\s+");

                int endpointLatency = Integer.parseInt(s[0]);
                int connectedCaches = Integer.parseInt(s[1]);

                for (int j = 0; j<connectedCaches; j++) {
                    s = br.readLine().split("\\s+");
                    int cacheId = Integer.parseInt(s[0]);
                    int latency = Integer.parseInt(s[1]);
                }
            }

            int i = 0;
            while ((line = br.readLine()) != null) {
//                List<Pizza.Ingredient> row = Arrays.stream(line.split(""))
//                        .map(Pizza.Ingredient::valueOf).collect(Collectors.toList());
//
//                pizza.ingredients[i++] = row.toArray(new Pizza.Ingredient[0]);
            }
            return null;
        }
    }
}
