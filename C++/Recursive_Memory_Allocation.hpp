#ifndef RECURSIVE_MEMORY_ALLOCATION_HPP
#define RECURSIVE_MEMORY_ALLOCATION_HPP

#include <iostream>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <fstream>

class Recursive_Memory_Allocation {
public:
    void run(size_t depth, size_t allocation_size) {
        double recursive_time = measure_recursive_allocation(depth, allocation_size);

        std::cout << "Recursive allocation test with depth " << depth
                  << " and allocation size " << allocation_size << " bytes: "
                  << recursive_time << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Recursive Memory Allocation" << std::endl;
            file << recursive_time << std::endl;
            file << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    int8_t* recursive_allocation(size_t depth, size_t size) {
        if (depth == 0) {
            int8_t* ptr = new int8_t[size];
            std::memset(ptr, 0, size); // Fill the memory with zeros
            return ptr;
        }

        int8_t* ptr = new int8_t[size];
        std::memset(ptr, 0, size); // Fill the memory with zeros
        int8_t* child = recursive_allocation(depth - 1, size);

        // Add some "useful" work with memory to prevent the compiler from optimizing it
        std::memset(ptr, 1, size); // Modify the memory content
        std::memcpy(child, ptr, size); // Copy data from ptr to child

        delete[] ptr; // Free the intermediate memory
        return child;
    }

    double measure_recursive_allocation(size_t depth, size_t size) {
        auto start = std::chrono::steady_clock::now();
        int8_t* last = recursive_allocation(depth, size);
        delete[] last; // Free the last block of memory
        auto end = std::chrono::steady_clock::now();
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // RECURSIVE_MEMORY_ALLOCATION_HPP