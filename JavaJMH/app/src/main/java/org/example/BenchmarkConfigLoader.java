package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BenchmarkConfigLoader {

    public static Map<String, BenchmarkConfig> loadConfig(String filename) throws IOException {
        Map<String, BenchmarkConfig> configs = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        String section = "";

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1);
                configs.put(section, new BenchmarkConfig());
            } else if (!section.isEmpty()) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    throw new RuntimeException("Invalid line in config: " + line);
                }

                String key = parts[0].trim();
                String value = parts[1].trim();
                BenchmarkConfig config = configs.get(section);

                switch (key) {
                    case "iterations": config.iterations = Integer.parseInt(value); break;
                    case "element_count": config.elementCount = Integer.parseInt(value); break;
                    case "allocation_sizes_file": config.allocationSizesFile = value; break;
                    case "access_indices_file": config.accessIndicesFile = value; break;
                    case "free_patterns_file": config.freePatternsFile = value; break;
                    case "threads_num": config.threadsNum = Integer.parseInt(value); break;
                    case "allocation_size": config.allocationSize = Integer.parseInt(value); break;
                    case "depth": config.depth = Integer.parseInt(value); break;
                }
            }
        }

        reader.close();
        return configs;
    }
}