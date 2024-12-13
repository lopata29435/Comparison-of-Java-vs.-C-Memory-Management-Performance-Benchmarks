#ifndef ALLOCATORTHREADBENCH_HPP
#define ALLOCATORTHREADBENCH_HPP

#include "AllocatorBench.hpp"

#include <iostream>
#include <thread>
#include <vector>
#include <chrono>
#include <random>
#include <mutex>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;


// Class for multithreaded benchmarking.
class AllocatorThreadBench {
public:
    void run(size_t iterations, size_t maxItemSize, size_t threadsNum, BenchmarkResults& results) {
        allocatorThreadBench(iterations, maxItemSize, threadsNum, results);

        // Output results.
        std::cout << "Threads Num is: " << threadsNum << std::endl;
        std::cout << "Time for " << iterations << " allocations of maxItemSize" << maxItemSize << " bytes: " << results.duration << " milliseconds" << std::endl;
        std::cout << "Max memory used: " << results.maxMemory << " bytes" << std::endl;
    }

private:
    void allocatorThreadBench(size_t iterations, size_t maxItemSize, size_t threadsNum, BenchmarkResults& results) {
        // Create threads and start them.
        std::vector<std::thread> threads;
        for (size_t i = 0; i < threadsNum; ++i) {
            threads.emplace_back([this, i, &results, iterations, maxItemSize]() {
                // Initialize random number generator.
                std::random_device rd;
                std::mt19937 gen(rd());
                std::uniform_int_distribution<size_t> dist(1, maxItemSize);

                std::vector<int*> allocations;
                allocations.reserve(maxItemSize);

                TimePoint start = Clock::now();

                for (size_t j = 0; j < iterations; ++j) {
                    size_t allocSize = dist(gen); // Generate a random size.

                    // Allocate memory.
                    int* ptr = new int[allocSize];
                    allocations.push_back(ptr);

                    // Fill the memory.
                    for (size_t k = 0; k < allocSize; ++k) {
                        ptr[k] = static_cast<int>(i + k);
                    }

                    // Free memory.
                    if (allocations.size() > maxItemSize / 2) {
                        delete[] allocations.back();
                        allocations.pop_back();
                    }
                }

                // Clean up remaining objects.
                for (int* ptr : allocations) {
                    delete[] ptr;
                }

                TimePoint end = Clock::now();
                std::lock_guard<std::mutex> lock(timeMutex);
                results.duration += std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
                results.maxMemory = std::max(results.maxMemory, maxItemSize * sizeof(int) * iterations);
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

#endif //ALLOCATORTHREADBENCH_HPP