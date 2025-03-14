package org.example;

import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RecursiveAllocationBench {
    public void run(long depth, long allocationSize) {
        double recursiveTime = measureRecursiveAllocation(depth, allocationSize);

        System.out.println("Recursive allocation test with depth " + depth
                + " and allocation size " + allocationSize + " bytes: "
                + recursiveTime + " milliseconds");

        try (FileWriter fileWriter = new FileWriter("Java_Benchmark_results.txt", true)) {
            fileWriter.write("Recursive allocation bench\n");
            fileWriter.write(String.valueOf(recursiveTime));
            fileWriter.write("\n\n");
        } catch (IOException e) {
            System.err.println("Failed to open the file for writing.");
            e.printStackTrace();
        }
    }

    private byte[] recursiveAllocation(long depth, long size) {
        if (depth == 0) {
            byte[] ptr = new byte[(int) size];
            Arrays.fill(ptr, (byte) 0); // Fill with zeros
            return ptr;
        }

        byte[] ptr = new byte[(int) size];
        Arrays.fill(ptr, (byte) 0); // Fill with zeros
        byte[] child = recursiveAllocation(depth - 1, size);

        // Simulate memory modification and copying
        Arrays.fill(ptr, (byte) 1);
        System.arraycopy(ptr, 0, child, 0, (int) size);

        // Help GC collect unused array
        ptr = null;

        return child;
    }

    private double measureRecursiveAllocation(long depth, long size) {
        long startTime = System.nanoTime();
        byte[] last = recursiveAllocation(depth, size);
        long endTime = System.nanoTime();

        // Help GC collect last array
        last = null;

        return (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
    }
}