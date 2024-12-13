#ifndef ALLOCATORBENCH_HPP
#define ALLOCATORBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <random>

// Structure to store benchmark results.
struct BenchmarkResults {
    size_t duration; // milliseconds
    size_t maxMemory; // bytes
};

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

// Class for single-threaded benchmarking.
class AllocatorBench {
public:
    void runBenchmark(size_t iterations, size_t maxItemSize, BenchmarkResults& results) {
        // Initialize random number generator.
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<size_t> dist(1, maxItemSize);

        std::vector<int*> allocations;
        allocations.reserve(maxItemSize);

        TimePoint start = Clock::now();

        for (size_t i = 0; i < iterations; ++i) {
            size_t allocSize = dist(gen); // Generate a random size.

            // Allocate memory.
            int* ptr = new int[allocSize];
            allocations.push_back(ptr);

            // Fill the memory.
            for (size_t j = 0; j < allocSize; ++j) {
                ptr[j] = static_cast<int>(i + j);
            }

            // Free memory.
            if (allocations.size() > maxItemSize / 2) {
                delete[] allocations.back();
                allocations.pop_back();
            }
        }

        // Clean up remaining objects
        for (int* ptr : allocations) {
            delete[] ptr;
        }

        TimePoint end = Clock::now();

        results.duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
        results.maxMemory = maxItemSize * sizeof(int) * iterations; // Conditional estimation.
    }

    void run(size_t iterations, size_t maxItemSize, BenchmarkResults& results) {
        runBenchmark(iterations, maxItemSize, results);

        std::cout << "Time for " << iterations << " allocations of maxItemSize " << maxItemSize << " bytes: " << results.duration << " milliseconds" << std::endl;
        std::cout << "Max memory used: " << results.maxMemory << " bytes" << std::endl;
    }
};

#endif //ALLOCATORBENCH_HPP