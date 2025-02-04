package org.example;

import java.io.*;
import java.util.*;

class BenchmarkConfig {
    int iterations = 0;
    int allocationSize = 0;
    int elementCount = 0;
    int threadsNum = 0;
    int depth = 0;
    String allocationSizesFile = "";
    String accessIndicesFile = "";
    String freePatternsFile = "";
}

public class BenchmarkRunner {

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

    public static List<Integer> loadIntegerListFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(Integer.parseInt(line.trim()));
        }

        reader.close();
        return list;
    }

    public static List<Boolean> loadBooleanListFromFile(String filename) throws IOException {
        List<Boolean> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String line;
        while ((line = reader.readLine()) != null) {
            int value = Integer.parseInt(line.trim());
            if (value != 0 && value != 1) {
                throw new RuntimeException("Invalid value in file: " + filename + ". Expected 0 or 1.");
            }
            list.add(value == 1);
        }

        reader.close();
        return list;
    }

    public static void main(String[] args) {
        try {
            Map<String, BenchmarkConfig> configs = loadConfig("../../benchmarks_config.txt");
            BenchmarkResults results = new BenchmarkResults();

            ByteBench byteBench = new ByteBench();
            BenchmarkConfig byteBenchConfig = configs.get("ByteBench");
            System.out.println("-----------------------Byte Bench------------------------------------------");
            byteBench.run(byteBenchConfig.iterations, byteBenchConfig.allocationSize);

            ComplexObjectBench complexObjectBench = new ComplexObjectBench();
            BenchmarkConfig complexObjectBenchConfig = configs.get("ComplexObjectBench");
            System.out.println("-----------------------Complex Object Bench--------------------------------");
            complexObjectBench.run(complexObjectBenchConfig.iterations, complexObjectBenchConfig.elementCount);

            AllocatorBench allocatorBench = new AllocatorBench();
            BenchmarkConfig allocatorBenchConfig = configs.get("AllocatorBench");
            System.out.println("-----------------------Allocator Bench-------------------------------------");
            List<Integer> allocationSizes = loadIntegerListFromFile(allocatorBenchConfig.allocationSizesFile);
            allocatorBench.run(allocationSizes, results);
            
            results.duration = 0;
            results.maxMemory = 0;

            AllocatorThreadBench allocatorThreadBench = new AllocatorThreadBench();
            BenchmarkConfig allocatorThreadBenchConfig = configs.get("AllocatorThreadBench");
            System.out.println("-----------------------Allocator Thread Bench------------------------------");
            allocationSizes = loadIntegerListFromFile(allocatorThreadBenchConfig.allocationSizesFile);
            allocatorThreadBench.run(allocationSizes, allocatorThreadBenchConfig.threadsNum, results);

            MemoryFragmentationBench memoryFragmentationBench = new MemoryFragmentationBench();
            BenchmarkConfig memoryFragmentationBenchConfig = configs.get("MemoryFragmentationBench");
            System.out.println("-----------------------Memory Fragmentation Bench--------------------------");
            allocationSizes = loadIntegerListFromFile(memoryFragmentationBenchConfig.allocationSizesFile);
            List<Boolean> freePatterns = loadBooleanListFromFile(memoryFragmentationBenchConfig.freePatternsFile);
            memoryFragmentationBench.run(allocationSizes, freePatterns);

            RecursiveAllocationBench recursiveAllocationBench = new RecursiveAllocationBench();
            BenchmarkConfig recursiveAllocationBenchConfig = configs.get("RecursiveAllocationBench");
            System.out.println("-----------------------Recursive Allocation Bench--------------------------");
            recursiveAllocationBench.run(recursiveAllocationBenchConfig.depth, recursiveAllocationBenchConfig.allocationSize);

            MemoryAccessBench memoryAccessBench = new MemoryAccessBench();
            BenchmarkConfig memoryAccessBenchConfig = configs.get("MemoryAccessBench");
            System.out.println("-----------------------Memory Access Bench---------------------------------");
            List<Integer> accessIndices = loadIntegerListFromFile(memoryAccessBenchConfig.accessIndicesFile);
            memoryAccessBench.run(memoryAccessBenchConfig.elementCount, accessIndices);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
