#ifndef MEMORYACCESSBENCH_HPP
#define MEMORYACCESSBENCH_HPP

#include <iostream>
#include <chrono>
#include <vector>

class MemoryAccessBench {
public:
    void run(size_t element_count, const std::vector<size_t>& accessIndices) {
        double access_time = measure_memory_access(element_count, accessIndices);

        std::cout << "Memory access test for " << accessIndices.size()
                  << " accesses in memory block of size " << element_count * sizeof(int)
                  << " bytes: " << access_time << " milliseconds" << std::endl;
    }

private:
    double measure_memory_access(size_t element_count, const std::vector<size_t>& accessIndices) {
        int* data = new int[element_count];

        for (size_t i = 0; i < element_count; ++i) {
            data[i] = static_cast<int>(i);
        }

        auto start = std::chrono::high_resolution_clock::now();
        volatile int sum = 0;

        for (size_t index : accessIndices) {
            sum += data[index % element_count];
        }

        auto end = std::chrono::high_resolution_clock::now();
        delete[] data;

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // MEMORYACCESSBENCH_HPP