package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class AllocatorThreadBench {

    private final ReentrantLock timeLock = new ReentrantLock();

    public void run(List<Integer> allocationSizes, int threadsNum, BenchmarkResults results) {
        allocatorThreadBench(allocationSizes, threadsNum, results);

        // Output results.
        System.out.println("Threads Num is: " + threadsNum);
        System.out.println("Time for " + allocationSizes.size() + " allocations: " 
                           + results.duration + " milliseconds");
        System.out.println("Max memory used: " + results.maxMemory + " bytes");
    }

    private void allocatorThreadBench(List<Integer> allocationSizes, int threadsNum, BenchmarkResults results) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsNum; ++i) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                ArrayList<int[]> allocations = new ArrayList<>();
                allocations.ensureCapacity(allocationSizes.size());

                long totalMemory = 0;
                long start = System.nanoTime();

                for (int allocSize : allocationSizes) {
                    // Allocate memory.
                    int[] ptr = new int[allocSize];
                    allocations.add(ptr);
                    totalMemory += allocSize * Integer.BYTES;

                    // Fill the memory.
                    for (int j = 0; j < allocSize; ++j) {
                        ptr[j] = threadIndex + j;
                    }

                    // Free memory conditionally to simulate partial deallocation.
                    if (allocations.size() > allocationSizes.size() / 2) {
                        allocations.remove(allocations.size() - 1);
                        totalMemory -= allocSize * Integer.BYTES;
                    }
                }

                // Clean up remaining objects.
                allocations.clear();

                long end = System.nanoTime();

                timeLock.lock();
                try {
                    results.duration += (end - start) / 1_000_000; // Convert to milliseconds
                    results.maxMemory = Math.max(results.maxMemory, totalMemory);
                } finally {
                    timeLock.unlock();
                }
            });

            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish.
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}