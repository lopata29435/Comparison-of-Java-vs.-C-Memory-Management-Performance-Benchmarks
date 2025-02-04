package org.example;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MemoryAccessBench {
    public void run(int elementCount, List<Integer> accessIndices) {
        double accessTime = measureMemoryAccess(elementCount, accessIndices);

        System.out.println("Memory access test for " + accessIndices.size()
                + " accesses in memory block of size " + elementCount * Integer.BYTES
                + " bytes: " + accessTime + " milliseconds");
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
