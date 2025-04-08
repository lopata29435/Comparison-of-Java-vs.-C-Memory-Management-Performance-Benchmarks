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
class Variable_Size_Allocation {
public:
    void run(const std::vector<size_t>& allocationSizes) {
        double duration = allocatorBench(allocationSizes);

        std::cout << "Time for " << allocationSizes.size() << " allocations: " 
                  << duration << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Variable Size Allocation" << std::endl;
            file << duration << std::endl;
            file << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    double allocatorBench(const std::vector<size_t>& allocationSizes) {
        std::vector<int8_t*> allocations;
        allocations.reserve(allocationSizes.size());

        TimePoint start = Clock::now();
        for (size_t allocSize : allocationSizes) {
            int8_t* ptr = new int8_t[allocSize];
            allocations.push_back(ptr);

            for (size_t j = 0; j < allocSize; ++j) {
                ptr[j] = static_cast<int8_t>(j);
            }

        }

        for (int8_t* ptr : allocations) {
            delete[] ptr;
        }

        TimePoint end = Clock::now();

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // ALLOCATORBENCH_HPP