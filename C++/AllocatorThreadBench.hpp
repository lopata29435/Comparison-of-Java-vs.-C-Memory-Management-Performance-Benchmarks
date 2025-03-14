#ifndef ALLOCATORTHREADBENCH_HPP
#define ALLOCATORTHREADBENCH_HPP

#include "AllocatorBench.hpp"

#include <iostream>
#include <thread>
#include <vector>
#include <chrono>
#include <mutex>
#include <fstream>

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

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Allocator thread bench" << std::endl;
            file << results.duration << std::endl;
            file << std::endl << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    void allocatorThreadBench(const std::vector<size_t>& allocationSizes, size_t threadsNum, BenchmarkResults& results) {
        // Record the start time for the execution of all threads.
        TimePoint start = Clock::now();

        // Create and launch threads.
        std::vector<std::thread> threads;
        for (size_t i = 0; i < threadsNum; ++i) {
            threads.emplace_back([this, i, &results, &allocationSizes]() {
                std::vector<int*> allocations;
                allocations.reserve(allocationSizes.size());

                size_t totalMemory = 0;

                for (size_t allocSize : allocationSizes) {
                    // Allocate memory.
                    int* ptr = new int[allocSize];
                    allocations.push_back(ptr);
                    totalMemory += allocSize * sizeof(int);

                    // Fill the allocated memory.
                    for (size_t j = 0; j < allocSize; ++j) {
                        ptr[j] = static_cast<int>(i + j);
                    }

                    // Partially deallocate memory.
                    if (allocations.size() > allocationSizes.size() / 2) {
                        delete[] allocations.back();
                        allocations.pop_back();
                        totalMemory -= allocSize * sizeof(int);
                    }
                }

                // Deallocate the remaining memory.
                for (int* ptr : allocations) {
                    delete[] ptr;
                }

                // Update the maximum memory usage.
                std::lock_guard<std::mutex> lock(timeMutex);
                results.maxMemory = std::max(results.maxMemory, totalMemory);
            });
        }

        // Wait for all threads to finish.
        for (auto& t : threads) {
            t.join();
        }

        // Record the end time for the execution of all threads.
        TimePoint end = Clock::now();

        // Calculate the total execution time.
        results.duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
    }

private:
    std::mutex timeMutex;
};

#endif // ALLOCATORTHREADBENCH_HPP