package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ByteBench {

    public void run(long iterations, int allocationSize) {
        // Measure allocation and deallocation time in milliseconds.
        double allocationTime = measureAllocation(iterations, allocationSize);
        double deallocationTime = measureDeallocation(iterations, allocationSize);

        System.out.println("Time for " + iterations + " allocations of size " + allocationSize
                + " bytes: " + allocationTime + " milliseconds");
        System.out.println("Time for " + iterations + " deallocations: " + deallocationTime + " milliseconds");
    }

    // Function to measure allocation time in milliseconds.
    private double measureAllocation(long iterations, int allocationSize) {
        List<byte[]> allocations = new ArrayList<>((int) iterations);

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            byte[] allocation = new byte[allocationSize];
            if (allocation == null) {
                throw new RuntimeException("Allocation failed at iteration: " + i);
            }
            allocations.add(allocation);
        }
        long end = System.nanoTime();

        // Free the allocated memory.
        allocations.clear();

        // Return time in milliseconds.
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    // Function to measure deallocation time in milliseconds.
    private double measureDeallocation(long iterations, int allocationSize) {
        List<byte[]> allocations = new ArrayList<>((int) iterations);
        for (int i = 0; i < iterations; i++) {
            byte[] allocation = new byte[allocationSize];
            if (allocation == null) {
                throw new RuntimeException("Allocation failed at iteration: " + i);
            }
            allocations.add(allocation);
        }

        long start = System.nanoTime();
        allocations.clear();
        long end = System.nanoTime();

        // Return time in milliseconds.
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }
}
