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
//            Pizza pizza = new Pizza(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));

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
