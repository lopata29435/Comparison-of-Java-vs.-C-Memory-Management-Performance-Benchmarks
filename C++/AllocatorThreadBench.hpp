#ifndef ALLOCATORTHREADBENCH_HPP
#define ALLOCATORTHREADBENCH_HPP

#include "AllocatorBench.hpp"

#include <iostream>
#include <thread>
#include <vector>
#include <chrono>
#include <mutex>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

// Class for multithreaded benchmarking.
class AllocatorThreadBench {
public:
    void run(const std::vector<size_t>& allocationSizes, size_t threadsNum, BenchmarkResults& results) {
        allocatorThreadBench(allocationSizes, threadsNum, results);

        // Output results.
        std::cout << "Threads Num is: " << threadsNum << std::endl;
        std::cout << "Time for " << allocationSizes.size() << " allocations: " 
                  << results.duration << " milliseconds" << std::endl;
        std::cout << "Max memory used: " << results.maxMemory << " bytes" << std::endl;
    }

private:
    void allocatorThreadBench(const std::vector<size_t>& allocationSizes, size_t threadsNum, BenchmarkResults& results) {
        // Create threads and start them.
        std::vector<std::thread> threads;
        for (size_t i = 0; i < threadsNum; ++i) {
            threads.emplace_back([this, i, &results, &allocationSizes]() {
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
                        ptr[j] = static_cast<int>(i + j);
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

                std::lock_guard<std::mutex> lock(timeMutex);
                results.duration += std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
                results.maxMemory = std::max(results.maxMemory, totalMemory);
            });
        }

        // Wait for all threads to finish.
        for (auto& t : threads) {
            t.join();
        }
    }

private:
    std::mutex timeMutex;
};

#endif // ALLOCATORTHREADBENCH_HPP