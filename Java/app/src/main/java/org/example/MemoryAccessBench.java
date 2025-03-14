package org.example;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.io.IOException;

public class MemoryAccessBench {
    public void run(int elementCount, List<Integer> accessIndices) {
        double accessTime = measureMemoryAccess(elementCount, accessIndices);

        System.out.println("Memory access test for " + accessIndices.size()
                + " accesses in memory block of size " + elementCount * Integer.BYTES
                + " bytes: " + accessTime + " milliseconds");

        try (FileWriter fileWriter = new FileWriter("Java_Benchmark_results.txt", true)) {
            fileWriter.write("Memory access bench\n");
            fileWriter.write(String.valueOf(accessTime));
            fileWriter.write("\n\n");
        } catch (IOException e) {
            System.err.println("Failed to open the file for writing.");
            e.printStackTrace();
        }
    }

    private double measureMemoryAccess(int elementCount, List<Integer> accessIndices) {
        int[] data = new int[elementCount];

        for (int i = 0; i < elementCount; ++i) {
            data[i] = i;
        }

        long start = System.nanoTime();
        int sum = 0;

        for (int index : accessIndices) {
            sum += data[index % elementCount];
        }

        long end = System.nanoTime();

        return (end - start) / 1_000_000.0; // Convert nanoseconds to milliseconds
    }
}
