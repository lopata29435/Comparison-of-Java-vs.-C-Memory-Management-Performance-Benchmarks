package org.example;

import java.util.List;
import java.util.ArrayList;

public class MemoryFragmentationBench {

    public void run(List<Integer> allocationSizes, List<Boolean> freePatterns) {
        if (allocationSizes.size() != freePatterns.size()) {
            throw new IllegalArgumentException("allocationSizes and freePatterns must have the same size.");
        }

        double fragmentationTime = measureFragmentation(allocationSizes, freePatterns);

        System.out.println("Fragmentation test for " + allocationSizes.size()
                + " allocations with predefined sizes and free patterns: "
                + fragmentationTime + " milliseconds");
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