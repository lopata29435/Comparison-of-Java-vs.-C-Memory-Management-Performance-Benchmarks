package org.example;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MemoryFragmentationBench {

    public void run(List<Integer> allocationSizes, List<Boolean> freePatterns) {
        if (allocationSizes.size() != freePatterns.size()) {
            throw new IllegalArgumentException("allocationSizes and freePatterns must have the same size.");
        }

        double fragmentationTime = measureFragmentation(allocationSizes, freePatterns);

        System.out.println("Fragmentation test for " + allocationSizes.size()
                + " allocations with predefined sizes and free patterns: "
                + fragmentationTime + " milliseconds");

        try (FileWriter fileWriter = new FileWriter("Java_Benchmark_results.txt", true)) {
            fileWriter.write("Memory fragmentation bench\n");
            fileWriter.write(String.valueOf(fragmentationTime));
            fileWriter.write("\n\n");
        } catch (IOException e) {
            System.err.println("Failed to open the file for writing.");
            e.printStackTrace();
        }
    }

    private double measureFragmentation(List<Integer> allocationSizes, List<Boolean> freePatterns) {
        List<byte[]> allocations = new ArrayList<>(allocationSizes.size());
        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.add(null);
        }

        long start = System.nanoTime();

        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.set(i, new byte[allocationSizes.get(i)]);

            if (freePatterns.get(i)) {
                allocations.set(i, null);
            }
        }

        long end = System.nanoTime();

        // Explicitly trigger garbage collection to clean up
        System.gc();

        return (end - start) / 1_000_000.0; // Convert nanoseconds to milliseconds
    }
}