#ifndef FRAGMENTATION_TEST_RANDOM_ALLOCATION_RELEASE_HPP
#define FRAGMENTATION_TEST_RANDOM_ALLOCATION_RELEASE_HPP

#include <iostream>
#include <vector>
#include <chrono>
#include <cstdlib>
#include <fstream>

class Fragmentation_Test_Random_Allocation_Release {
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
            file << "Fragmentation Test Random Allocation Release" << std::endl;
            file << fragmentation_time << std::endl;
            file << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    double measure_fragmentation(const std::vector<size_t>& allocationSizes, const std::vector<bool>& freePatterns) {
        std::vector<int8_t*> allocations(allocationSizes.size(), nullptr);

        auto start = std::chrono::high_resolution_clock::now();

        for (size_t i = 0; i < allocationSizes.size(); ++i) {
            allocations[i] = new int8_t[allocationSizes[i]];

            if (freePatterns[i]) {
                delete[] allocations[i];
                allocations[i] = nullptr;
            }
        }

        auto end = std::chrono::high_resolution_clock::now();

        for (int8_t* ptr : allocations) {
            delete[] ptr;
        }

        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif // FRAGMENTATION_TEST_RANDOM_ALLOCATION_RELEASE_HPP