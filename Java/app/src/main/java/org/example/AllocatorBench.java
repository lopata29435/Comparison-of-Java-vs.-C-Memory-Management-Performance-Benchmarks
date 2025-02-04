package org.example;

import java.util.ArrayList;
import java.util.List;

// Structure to store benchmark results.
class BenchmarkResults {
    long duration; // milliseconds
    long maxMemory; // bytes

    public BenchmarkResults() {
        this.duration = 0;
        this.maxMemory = 0;
    }
}

// Class for single-threaded benchmarking.
public class AllocatorBench {

    public void run(List<Integer> allocationSizes, BenchmarkResults results) {
        allocatorBench(allocationSizes, results);

        System.out.println("Time for " + allocationSizes.size() + " allocations: " 
                + results.duration + " milliseconds");
        System.out.println("Max memory used: " + results.maxMemory + " bytes");
    }

    private void allocatorBench(List<Integer> allocationSizes, BenchmarkResults results) {
        List<int[]> allocations = new ArrayList<>(allocationSizes.size());

        long totalMemory = 0;

        long startTime = System.nanoTime();

        for (int allocSize : allocationSizes) {
            // Allocate memory.
            int[] arr = new int[allocSize];
            allocations.add(arr);
            totalMemory += (long) allocSize * Integer.BYTES;

            // Fill the memory.
            for (int j = 0; j < allocSize; j++) {
                arr[j] = j;
            }

            // Free memory conditionally to simulate partial deallocation.
            if (allocations.size() > allocationSizes.size() / 2) {
                totalMemory -= (long) allocations.get(allocations.size() - 1).length * Integer.BYTES;
                allocations.remove(allocations.size() - 1);
            }
        }

        // Clean up remaining objects.
        allocations.clear();

        long endTime = System.nanoTime();

        results.duration = (endTime - startTime) / 1_000_000;
        results.maxMemory = totalMemory; // Maximum memory tracked during allocation.
    }

}
