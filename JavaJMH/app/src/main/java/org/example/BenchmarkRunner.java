package org.example;

import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class BenchmarkRunner {

    public static List<String> readLastNLines(String filePath, int n) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        int size = lines.size();
        return lines.subList(Math.max(size - n, 0), size);
    }

    public static void main(String[] args) throws RunnerException {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.txt");
            List<String> lines;

            ByteBench byteBench = new ByteBench();
            BenchmarkConfig byteBenchConfig = configs.get("ByteBench");
            System.out.println("-----------------------Byte Bench------------------------------------------");
            byteBench.run(byteBenchConfig.iterations, byteBenchConfig.allocationSize);
            lines = readLastNLines("ByteBenchmark_results.txt", 3);
            for (String line : lines) {
                System.out.println(line);
            }

            ComplexObjectBench complexObjectBench = new ComplexObjectBench();
            BenchmarkConfig complexObjectBenchConfig = configs.get("ComplexObjectBench");
            System.out.println("-----------------------Complex Object Bench--------------------------------");
            complexObjectBench.run(complexObjectBenchConfig.iterations, complexObjectBenchConfig.elementCount);
            lines = readLastNLines("ComplexObjectBenchmark_results.txt", 3);
            for (String line : lines) {
                System.out.println(line);
            }

            AllocatorBench allocatorBench = new AllocatorBench();
            BenchmarkConfig allocatorBenchConfig = configs.get("AllocatorBench");
            System.out.println("-----------------------Allocator Bench-------------------------------------");
            allocatorBench.run();
            lines = readLastNLines("AllocatorBench_results.txt", 2);
            for (String line : lines) {
                System.out.println(line);
            }

            AllocatorThreadBench allocatorThreadBench = new AllocatorThreadBench();
            BenchmarkConfig allocatorThreadBenchConfig = configs.get("AllocatorThreadBench");
            System.out.println("-----------------------Allocator Thread Bench------------------------------");
            allocatorThreadBench.run();
            lines = readLastNLines("AllocatorThreadBench_results.txt", 2);
            for (String line : lines) {
                System.out.println(line);
            }

            MemoryFragmentationBench memoryFragmentationBench = new MemoryFragmentationBench();
            BenchmarkConfig memoryFragmentationBenchConfig = configs.get("MemoryFragmentationBench");
            System.out.println("-----------------------Memory Fragmentation Bench--------------------------");
            memoryFragmentationBench.run();
            lines = readLastNLines("MemoryFragmentationBench_results.txt", 2);
            for (String line : lines) {
                System.out.println(line);
            }

            RecursiveAllocationBench recursiveAllocationBench = new RecursiveAllocationBench();
            BenchmarkConfig recursiveAllocationBenchConfig = configs.get("RecursiveAllocationBench");
            System.out.println("-----------------------Recursive Allocation Bench--------------------------");
            recursiveAllocationBench.run(recursiveAllocationBenchConfig.depth, recursiveAllocationBenchConfig.allocationSize);
            lines = readLastNLines("RecursiveAllocationBenchmark_results.txt", 2);
            for (String line : lines) {
                System.out.println(line);
            }

            MemoryAccessBench memoryAccessBench = new MemoryAccessBench();
            BenchmarkConfig memoryAccessBenchConfig = configs.get("MemoryAccessBench");
            System.out.println("-----------------------Memory Access Bench---------------------------------");
            memoryAccessBench.run();
            lines = readLastNLines("MemoryAccessBench_results.txt", 2);
            for (String line : lines) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}