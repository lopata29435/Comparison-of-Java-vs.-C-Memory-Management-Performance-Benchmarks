package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class BenchmarkParser {

    public static void start() throws IOException {
        Path directory = Paths.get(System.getProperty("user.dir"));

        File resultFile = new File(directory.toFile(), "Java_Benchmark_results.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
            for (Path entry : stream) {
                String content = new String(Files.readAllBytes(entry), "UTF-8");
                JsonObject jsonObject = JsonParser.parseString(content).getAsJsonArray().get(0).getAsJsonObject();

                String benchmarkName = entry.getFileName().toString().replace(".json", "");
                double score = jsonObject.getAsJsonObject("primaryMetric").get("score").getAsDouble();

                writer.write(formatBenchmarkName(benchmarkName));
                writer.newLine();
                writer.write(String.format("%.4f", score));
                writer.newLine();
            }
        }

        writer.close();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
            for (Path entry : stream) {
                Files.delete(entry);
            }
        }

        System.out.println("Parsing complete. Results saved to " + resultFile.getAbsolutePath());
    }

    private static String formatBenchmarkName(String benchmarkName) {
        return benchmarkName.replace('_', ' ');
    }
}
