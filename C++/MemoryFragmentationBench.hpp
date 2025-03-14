#ifndef MEMORYFRAGMENTATIONBENCH_HPP
#define MEMORYFRAGMENTATIONBENCH_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <cstdlib>
#include <fstream>

class MemoryFragmentationBench {
public:
    void run(const std::vector<size_t>& allocationSizes, const std::vector<bool>& freePatterns) {
        if (allocationSizes.size() != freePatterns.size()) {
            throw std::invalid_argument("allocationSizes and freePatterns must have the same size.");
        }

        double fragmentation_time = measure_fragmentation(allocationSizes, freePatterns);

        std::cout << "Fragmentation test for " << allocationSizes.size()
                  << " allocations with predefined sizes and free patterns: "
                  << fragmentation_time << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Memory fragmentation bench" << std::endl;
            file << fragmentation_time << std::endl;
            file << std::endl << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    double measure_fragmentation(const std::vector<size_t>& allocationSizes, const std::vector<bool>& freePatterns) {
        std::vector<char*> allocations(allocationSizes.size(), nullptr);

        auto start = std::chrono::high_resolution_clock::now();

        for (size_t i = 0; i < allocationSizes.size(); ++i) {
            allocations[i] = new char[allocationSizes[i]];

            if (freePatterns[i]) {
                delete[] allocations[i];
                allocations[i] = nullptr;
            }
        }

        auto end = std::chrono::high_resolution_clock::now();

        for (char* ptr : allocations) {
            delete[] ptr;
        }

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // MEMORYFRAGMENTATIONBENCH_HPP