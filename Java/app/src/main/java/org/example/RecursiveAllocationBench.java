package org.example;

import java.util.Arrays;

public class RecursiveAllocationBench {
    public void run(long depth, long allocationSize) {
        double recursiveTime = measureRecursiveAllocation(depth, allocationSize);

        System.out.println("Recursive allocation test with depth " + depth
                + " and allocation size " + allocationSize + " bytes: "
                + recursiveTime + " milliseconds");
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