#ifndef RECURSIVEALLOCATIONBENCH_HPP
#define RECURSIVEALLOCATIONBENCH_HPP

#include <iostream>
#include <chrono>
#include <cstdlib>

class RecursiveAllocationBench {
public:
    void run(size_t depth, size_t allocation_size) {
        double recursive_time = measure_recursive_allocation(depth, allocation_size);

        std::cout << "Recursive allocation test with depth " << depth
                  << " and allocation size " << allocation_size << " bytes: "
                  << recursive_time << " milliseconds" << std::endl;
    }

private:
    char* recursive_allocation(size_t depth, size_t size) {
        if (depth == 0) {
            return new char[size];
        }

        char* ptr = new char[size];
        char* child = recursive_allocation(depth - 1, size);
        delete[] ptr;
        return child;
    }

    double measure_recursive_allocation(size_t depth, size_t size) {
        auto start = std::chrono::high_resolution_clock::now();
        char* last = recursive_allocation(depth, size);
        auto end = std::chrono::high_resolution_clock::now();

        delete[] last;

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // RECURSIVEALLOCATIONBENCH_HPP
