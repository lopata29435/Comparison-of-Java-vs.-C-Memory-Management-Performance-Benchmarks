#ifndef BYTENEWBENCH_HPP
#define BYTENEWBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <cstdlib>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

class ByteNewBench {
public:
    ByteNewBench(size_t allocation_size, size_t iterations)
        : allocation_size_(allocation_size), iterations_(iterations) {}

    void run() {
        // Measure allocation and deallocation time in milliseconds.
        double allocation_time = measure_allocation();
        double deallocation_time = measure_deallocation();

        std::cout << "Time for " << iterations_ << " allocations of size " << allocation_size_
                  << " bytes: " << allocation_time << " milliseconds" << std::endl;
        std::cout << "Time for " << iterations_ << " deallocations: " << deallocation_time << " milliseconds" << std::endl;
    }

private:
    size_t allocation_size_;
    size_t iterations_;

    // Function to measure allocation time in milliseconds.
    double measure_allocation() {
        std::vector<char*> allocations(iterations_, nullptr);

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations_; ++i) {
            allocations[i] = new char[allocation_size_];
        }
        TimePoint end = Clock::now();

        // Clean up allocations to avoid memory leak.
        for (size_t i = 0; i < iterations_; ++i) {
            delete[] allocations[i];
        }

        // Return time in milliseconds
        return std::chrono::duration<double, std::milli>(end - start).count();
    }

    // Function to measure deallocation time in milliseconds.
    double measure_deallocation() {
        std::vector<char*> allocations(iterations_, nullptr);
        for (size_t i = 0; i < iterations_; ++i) {
            allocations[i] = new char[allocation_size_];
        }

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations_; ++i) {
            delete[] allocations[i];
        }
        TimePoint end = Clock::now();

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //BYTENEWBENCH_HPP
