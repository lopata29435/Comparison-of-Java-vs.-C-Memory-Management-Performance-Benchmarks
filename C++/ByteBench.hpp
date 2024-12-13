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
    void run(size_t iterations, size_t allocation_size) {
        // Measure allocation and deallocation time in milliseconds.
        double allocation_time = measure_allocation(iterations, allocation_size);
        double deallocation_time = measure_deallocation(iterations, allocation_size);

        std::cout << "Time for " << iterations << " allocations of size " << allocation_size
                  << " bytes: " << allocation_time << " milliseconds" << std::endl;
        std::cout << "Time for " << iterations << " deallocations: " << deallocation_time << " milliseconds" << std::endl;
    }

private:
    // Function to measure allocation time in milliseconds.
    double measure_allocation(size_t iterations, size_t allocation_size) {
        std::vector<void*> allocations(iterations, nullptr);

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations; ++i) {
            allocations[i] = malloc(allocation_size);
            if (!allocations[i]) {
                throw std::runtime_error("Allocation failed at iteration: " + std::to_string(i));
            }
        }
        TimePoint end = Clock::now();

        // Free the allocated memory.
        for (size_t i = 0; i < iterations; ++i) {
            free(allocations[i]);
        }

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }

    // Function to measure deallocation time in milliseconds.
    double measure_deallocation(size_t iterations, size_t allocation_size) {
        std::vector<void*> allocations(iterations, nullptr);
        for (size_t i = 0; i < iterations; ++i) {
            allocations[i] = malloc(allocation_size);
            if (!allocations[i]) {
                throw std::runtime_error("Allocation failed at iteration: " + std::to_string(i));
            }
        }

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations; ++i) {
            free(allocations[i]);
        }
        TimePoint end = Clock::now();

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //BYTEBENCH_HPP
