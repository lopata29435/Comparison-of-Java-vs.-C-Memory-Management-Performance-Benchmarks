package org.example;

public class RecursiveAllocationBench {
    public void run(long depth, long allocationSize) {
        double recursiveTime = measureRecursiveAllocation(depth, allocationSize);

        System.out.println("Recursive allocation test with depth " + depth
                + " and allocation size " + allocationSize + " bytes: "
                + recursiveTime + " milliseconds");
    }

    // Recursive allocation function, allocates memory at each level of recursion
    private byte[] recursiveAllocation(long depth, long size) {
        if (depth == 0) {
            return new byte[(int) size]; // Allocate a byte array
        }

        byte[] ptr = new byte[(int) size]; // Allocate memory
        byte[] child = recursiveAllocation(depth - 1, size); // Recurse
        return child;
    }

    // Measure the time taken for recursive allocation
    private double measureRecursiveAllocation(long depth, long size) {
        long startTime = System.nanoTime(); // Start time
        byte[] last = recursiveAllocation(depth, size); // Perform the allocation
        long endTime = System.nanoTime(); // End time

        return (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
    }
}