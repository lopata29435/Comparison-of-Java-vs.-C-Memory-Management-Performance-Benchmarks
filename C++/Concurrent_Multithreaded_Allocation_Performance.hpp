#ifndef CONCURRENT_MULTITHREADED_ALLOCATION_PERFORMANCE_HPP
#define CONCURRENT_MULTITHREADED_ALLOCATION_PERFORMANCE_HPP

#include <iostream>
#include <thread>
#include <vector>
#include <chrono>
#include <mutex>
#include <fstream>
#include <cstdint>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

// Class for multithreaded benchmarking.
class Concurrent_Multithreaded_Allocation_Performance {
public:
    void run(const std::vector<size_t>& allocationSizes, size_t threadsNum) {
        double duration = allocatorThreadBench(allocationSizes, threadsNum);

        // Output results.
        std::cout << "Threads Num is: " << threadsNum << std::endl;
        std::cout << "Time for " << allocationSizes.size() << " allocations: " 
                  << duration << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Concurrent Multithreaded Allocation Performance" << std::endl;
            file << duration << std::endl;
            file << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    double allocatorThreadBench(const std::vector<size_t>& allocationSizes, size_t threadsNum) {
        std::vector<std::thread> threads;
        
        TimePoint start = Clock::now();
        for (size_t i = 0; i < threadsNum; ++i) {
            threads.emplace_back([this, i, &allocationSizes]() {
                std::vector<int8_t*> allocations;
                allocations.reserve(allocationSizes.size());
                for (size_t allocSize : allocationSizes) {
                    int8_t* ptr = new int8_t[allocSize];
                    allocations.push_back(ptr);

                    for (size_t j = 0; j < allocSize; ++j) {
                        ptr[j] = static_cast<int8_t>(i + j);
                    }
                }
                for (int8_t* ptr : allocations) {
                    delete[] ptr;
                }
            });
        }
        for (auto& t : threads) {
            t.join();
        }
        TimePoint end = Clock::now();

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // CONCURRENT_MULTITHREADED_ALLOCATION_PERFORMANCE_HPP