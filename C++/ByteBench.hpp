#ifndef BYTEBENCH_HPP
#define BYTEBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <cstdlib>

// Function for time measure.
using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

class ByteBench {
public:
    ByteBench(size_t allocation_size, size_t iterations)
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
        std::vector<void*> allocations(iterations_, nullptr);

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations_; ++i) {
            allocations[i] = malloc(allocation_size_);
            if (!allocations[i]) {
                throw std::runtime_error("Allocation failed at iteration: " + std::to_string(i));
            }
        }
        TimePoint end = Clock::now();

        // Free the allocated memory.
        for (size_t i = 0; i < iterations_; ++i) {
            free(allocations[i]);
        }

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }

    // Function to measure deallocation time in milliseconds.
    double measure_deallocation() {
        std::vector<void*> allocations(iterations_, nullptr);
        for (size_t i = 0; i < iterations_; ++i) {
            allocations[i] = malloc(allocation_size_);
            if (!allocations[i]) {
                throw std::runtime_error("Allocation failed at iteration: " + std::to_string(i));
            }
        }

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations_; ++i) {
            free(allocations[i]);
        }
        TimePoint end = Clock::now();

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //BYTEBENCH_HPP
