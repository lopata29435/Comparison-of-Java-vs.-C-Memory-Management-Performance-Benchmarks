#ifndef ALLOCATORBENCH_HPP
#define ALLOCATORBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <fstream>

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
    void run(const std::vector<size_t>& allocationSizes, BenchmarkResults& results) {
        allocatorBench(allocationSizes, results);

        std::cout << "Time for " << allocationSizes.size() << " allocations: " 
                  << results.duration << " milliseconds" << std::endl;
        std::cout << "Max memory used: " << results.maxMemory << " bytes" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Allocator bench" << std::endl;
            file << results.duration << std::endl;
            file << std::endl << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    void allocatorBench(const std::vector<size_t>& allocationSizes, BenchmarkResults& results) {
        std::vector<int*> allocations;
        allocations.reserve(allocationSizes.size());

        size_t totalMemory = 0;
        TimePoint start = Clock::now();

        for (size_t allocSize : allocationSizes) {
            // Allocate memory.
            int* ptr = new int[allocSize];
            allocations.push_back(ptr);
            totalMemory += allocSize * sizeof(int);

            // Fill the memory.
            for (size_t j = 0; j < allocSize; ++j) {
                ptr[j] = static_cast<int>(j);
            }

            // Free memory conditionally to simulate partial deallocation.
            if (allocations.size() > allocationSizes.size() / 2) {
                delete[] allocations.back();
                allocations.pop_back();
                totalMemory -= allocSize * sizeof(int);
            }
        }

        // Clean up remaining objects.
        for (int* ptr : allocations) {
            delete[] ptr;
        }

        TimePoint end = Clock::now();

        results.duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
        results.maxMemory = totalMemory; // Maximum memory tracked during allocation.
    }
};

#endif // ALLOCATORBENCH_HPP