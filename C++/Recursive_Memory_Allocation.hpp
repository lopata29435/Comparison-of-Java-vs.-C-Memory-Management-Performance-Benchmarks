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
    void recursive_allocation(size_t depth, size_t size, std::vector<int8_t*>& blocks, size_t index = 0) {
        if (index >= depth) return;

        int8_t* ptr = new int8_t[size];
        std::memset(ptr, 0, size);

        blocks[index] = ptr;

        recursive_allocation(depth, size, blocks, index + 1);

        std::memset(ptr, 1, size);
        std::memcpy(blocks[depth - 1], ptr, size);
    }

    double measure_recursive_allocation(size_t depth, size_t size) {
        std::vector<int8_t*> blocks(depth);

        auto start = std::chrono::steady_clock::now();
        recursive_allocation(depth, size, blocks);
        for (int8_t* ptr : blocks) {
            delete[] ptr;
        }
        auto end = std::chrono::steady_clock::now();
        
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // RECURSIVE_MEMORY_ALLOCATION_HPP