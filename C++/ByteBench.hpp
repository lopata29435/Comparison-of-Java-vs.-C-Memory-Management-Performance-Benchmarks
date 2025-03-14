#ifndef BYTEBENCH_HPP
#define BYTEBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <cstdlib>
#include <fstream>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

class ByteBench {
public:
    void run(size_t allocation_size, size_t iterations) {
        // Measure allocation and deallocation time in milliseconds.
        double allocation_time = measure_allocation(allocation_size, iterations);
        double deallocation_time = measure_deallocation(allocation_size, iterations);

        std::cout << "Time for " << iterations << " allocations of size " << allocation_size
                  << " bytes: " << allocation_time << " milliseconds" << std::endl;
        std::cout << "Time for " << iterations << " deallocations: " << deallocation_time << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Byte bench allocation" << std::endl;
            file << allocation_time << std::endl;
            file << std::endl << std::endl;
            file << "Byte bench deallocation" << std::endl;
            file << deallocation_time << std::endl;
            file << std::endl << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    // Function to measure allocation time in milliseconds.
    double measure_allocation(size_t allocation_size, size_t iterations) {
        std::vector<char*> allocations(iterations, nullptr);

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations; ++i) {
            allocations[i] = new char[allocation_size];
        }
        TimePoint end = Clock::now();

        // Clean up allocations to avoid memory leak.
        for (size_t i = 0; i < iterations; ++i) {
            delete[] allocations[i];
        }

        // Return time in milliseconds
        return std::chrono::duration<double, std::milli>(end - start).count();
    }

    // Function to measure deallocation time in milliseconds.
    double measure_deallocation(size_t allocation_size, size_t iterations) {
        std::vector<char*> allocations(iterations, nullptr);
        for (size_t i = 0; i < iterations; ++i) {
            allocations[i] = new char[allocation_size];
        }

        TimePoint start = Clock::now();
        for (size_t i = 0; i < iterations; ++i) {
            delete[] allocations[i];
        }
        TimePoint end = Clock::now();

        // Return time in milliseconds.
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //BYTEBENCH_HPP
