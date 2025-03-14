#include <iostream>
#include <fstream>
#include <unordered_map>
#include <string>
#include <sstream>

#include "ByteBench.hpp"
#include "AllocatorBench.hpp"
#include "AllocatorThreadBench.hpp"
#include "ComplexObjectBench.hpp"
#include "MemoryAccessBench.hpp"
#include "MemoryFragmentationBench.hpp"
#include "RecursiveAllocationBench.hpp"

struct BenchmarkConfig {
    size_t iterations = 0;
    size_t allocation_size = 0;
    size_t element_count = 0;
    size_t threads_num = 0;
    size_t depth = 0;
    std::string allocation_sizes_file;
    std::string access_indices_file;
    std::string free_patterns_file;
};

std::unordered_map<std::string, BenchmarkConfig> loadConfig(const std::string& filename) {
    std::ifstream file(filename);
    if (!file.is_open()) {
        throw std::runtime_error("Could not open the config file: " + filename);
    }

    std::unordered_map<std::string, BenchmarkConfig> configs;
    std::string line, section;
    while (std::getline(file, line)) {
        line.erase(0, line.find_first_not_of(" \t"));
        line.erase(line.find_last_not_of(" \t") + 1);

        if (line.empty() || line[0] == '#') {
            continue;
        }

        if (line[0] == '[' && line.back() == ']') {
            section = line.substr(1, line.size() - 2);
            configs[section] = BenchmarkConfig();
        } else if (!section.empty()) {
            auto pos = line.find('=');
            if (pos == std::string::npos) {
                throw std::runtime_error("Invalid line in config: " + line);
            }

            std::string key = line.substr(0, pos);
            std::string value = line.substr(pos + 1);

            auto& config = configs[section];
            if (key == "iterations") {
                config.iterations = std::stoi(value);
            } else if (key == "element_count") {
                config.element_count = std::stoul(value);
            } else if (key == "allocation_sizes_file") {
                config.allocation_sizes_file = value;
            } else if (key == "access_indices_file") {
                config.access_indices_file = value;
            } else if (key == "free_patterns_file") {
                config.free_patterns_file = value;
            } else if (key == "threads_num") {
                config.threads_num = std::stoi(value);
            } else if (key == "allocation_size") {
                config.allocation_size = std::stoi(value);
            } else if (key == "depth") {
                config.depth = std::stoi(value);
            }
        }
    }

    return configs;
}

std::vector<size_t> loadAllocationSizesFromFile(const std::string& filename) {
    std::vector<size_t> allocationSizes;
    std::ifstream file(filename);

    if (!file.is_open()) {
        throw std::runtime_error("Could not open the file: " + filename);
    }

    size_t size;
    while (file >> size) {
        allocationSizes.push_back(size);
    }

    file.close();

    return allocationSizes;
}

std::vector<size_t> loadAccessIndicesFromFile(const std::string& filename) {
    std::vector<size_t> accessIndices;
    std::ifstream file(filename);

    if (!file.is_open()) {
        throw std::runtime_error("Could not open the file: " + filename);
    }

    size_t index;
    while (file >> index) {
        accessIndices.push_back(index);
    }

    file.close();

    return accessIndices;
}

std::vector<bool> loadFreePatternsFromFile(const std::string& filename) {
    std::vector<bool> freePatterns;
    std::ifstream file(filename);

    if (!file.is_open()) {
        throw std::runtime_error("Could not open the file: " + filename);
    }

    int value;
    while (file >> value) {
        if (value != 0 && value != 1) {
            throw std::runtime_error("Invalid value in file: " + filename + ". Expected 0 or 1.");
        }
        freePatterns.push_back(value == 1);
    }

    file.close();

    return freePatterns;
}

int main() {
    auto configs = loadConfig("../../benchmarks_config.txt");

    BenchmarkResults results = {0, 0};

    ByteBench byteBench;
    auto& byteBenchConfig = configs["ByteBench"];
    
    ComplexObjectBench complexObjectBench;
    auto& complexObjectBenchConfig = configs["ComplexObjectBench"];
    
    AllocatorBench allocatorBench;
    auto& allocatorBenchConfig = configs["AllocatorBench"];
    
    AllocatorThreadBench allocatorThreadBench;
    auto& allocatorThreadBenchConfig = configs["AllocatorThreadBench"];
    
    MemoryAccessBench memoryAccessBench;
    auto& memoryAccessBenchConfig = configs["MemoryAccessBench"];

    MemoryFragmentationBench memoryFragmentationBench;
    auto& memoryFragmentationBenchConfig = configs["MemoryFragmentationBench"];
    
    RecursiveAllocationBench recursiveAllocationBench;
    auto& recursiveAllocationBenchConfig = configs["RecursiveAllocationBench"];

    std::vector<size_t> allocationSizes;
    std::vector<size_t> accessIndices;
    std::vector<bool> freePatterns;

    //Template for all run function arguments (in brackets special cases, depends on realization):
    // Iterations(num of elements), Size of element(max size of elemnt), additional args(threads, e.t.c)
    std::cout << "-----------------------Byte Bench--------------------------------------\n";
    byteBench.run(byteBenchConfig.allocation_size, byteBenchConfig.iterations);
    std::cout << "-----------------------Complex Object Bench--------------------------------\n";
    complexObjectBench.run(complexObjectBenchConfig.iterations, complexObjectBenchConfig.element_count);
    std::cout << "-----------------------Allocator bench-------------------------------------\n";
    allocationSizes = loadAllocationSizesFromFile(allocatorBenchConfig.allocation_sizes_file);
    allocatorBench.run(allocationSizes, results);
    results = {0, 0};
    std::cout << "-----------------------Allocator Thread Bench------------------------------\n";
    allocationSizes = loadAllocationSizesFromFile(allocatorThreadBenchConfig.allocation_sizes_file);
    allocatorThreadBench.run(allocationSizes, allocatorThreadBenchConfig.threads_num, results);
    std::cout << "-----------------------Memory Fragmentation Bench--------------------------\n";
    allocationSizes = loadAllocationSizesFromFile(memoryFragmentationBenchConfig.allocation_sizes_file);
    freePatterns = loadFreePatternsFromFile(memoryFragmentationBenchConfig.free_patterns_file);
    memoryFragmentationBench.run(allocationSizes, freePatterns);
    std::cout << "-----------------------Recursive Allocation Bench--------------------------\n";
    recursiveAllocationBench.run(recursiveAllocationBenchConfig.depth, recursiveAllocationBenchConfig.allocation_size);
    std::cout << "-----------------------Memory Access Bench---------------------------------\n";
    accessIndices = loadAccessIndicesFromFile(memoryAccessBenchConfig.access_indices_file);
    memoryAccessBench.run(memoryAccessBenchConfig.element_count, accessIndices);
    
    return 0;
}

