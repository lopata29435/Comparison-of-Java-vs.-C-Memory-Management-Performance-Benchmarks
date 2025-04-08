#include <iostream>
#include <fstream>
#include <unordered_map>
#include <string>
#include <sstream>

#include "Fixed_Size_Allocation.hpp"
#include "Variable_Size_Allocation.hpp"
#include "Concurrent_Multithreaded_Allocation_Performance.hpp"
#include "Complex_Object_Allocation.hpp"
#include "Fragmentation_Test_Random_Allocation_Release.hpp"
#include "Recursive_Memory_Allocation.hpp"

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
    auto configs = loadConfig("../../benchmarks_config.ini");

    Fixed_Size_Allocation fixed_Size_Allocation;
    auto& fixed_Size_Allocation_Config = configs["Fixed_Size_Allocation"];
    
    Complex_Object_Allocation complex_Object_Allocation;
    auto& complex_Object_Allocation_Config = configs["Complex_Object_Allocation"];
    
    Variable_Size_Allocation variable_Size_Allocation;
    auto& variable_Size_Allocation_Config = configs["Variable_Size_Allocation"];
    
    Concurrent_Multithreaded_Allocation_Performance concurrent_Multithreaded_Allocation_Performance;
    auto& concurrent_Multithreaded_Allocation_Performance_Config = configs["Concurrent_Multithreaded_Allocation_Performance"];
    
    Fragmentation_Test_Random_Allocation_Release fragmentation_Test_Random_Allocation_Release;
    auto& fragmentation_Test_Random_Allocation_Release_Config = configs["Fragmentation_Test_Random_Allocation_Release"];
    
    Recursive_Memory_Allocation recursive_Memory_Allocation;
    auto& recursive_Memory_Allocation_Config = configs["Recursive_Memory_Allocation"];

    std::vector<size_t> allocationSizes;
    std::vector<size_t> accessIndices;
    std::vector<bool> freePatterns;

    std::ofstream clearFile("CXX_Benchmark_results.txt", std::ios::trunc);
    clearFile.close();

    std::cout << "-----------------------Fixed_Size_Allocation--------------------------------------\n";
    fixed_Size_Allocation.run(fixed_Size_Allocation_Config.allocation_size, fixed_Size_Allocation_Config.iterations);
    std::cout << "-----------------------Complex_Object_Allocation----------------------------------\n";
    complex_Object_Allocation.run(complex_Object_Allocation_Config.iterations, complex_Object_Allocation_Config.element_count);
    std::cout << "-----------------------Variable_Size_Allocation-----------------------------------\n";
    allocationSizes = loadAllocationSizesFromFile(variable_Size_Allocation_Config.allocation_sizes_file);
    variable_Size_Allocation.run(allocationSizes);
    std::cout << "-----------------------Concurrent_Multithreaded_Allocation_Performance------------\n";
    allocationSizes = loadAllocationSizesFromFile(concurrent_Multithreaded_Allocation_Performance_Config.allocation_sizes_file);
    concurrent_Multithreaded_Allocation_Performance.run(allocationSizes, concurrent_Multithreaded_Allocation_Performance_Config.threads_num);
    std::cout << "-----------------------Fragmentation_Test_Random_Allocation_Release---------------\n";
    allocationSizes = loadAllocationSizesFromFile(fragmentation_Test_Random_Allocation_Release_Config.allocation_sizes_file);
    freePatterns = loadFreePatternsFromFile(fragmentation_Test_Random_Allocation_Release_Config.free_patterns_file);
    fragmentation_Test_Random_Allocation_Release.run(allocationSizes, freePatterns);
    std::cout << "-----------------------Recursive_Memory_Allocation--------------------------------\n";
    recursive_Memory_Allocation.run(recursive_Memory_Allocation_Config.depth, recursive_Memory_Allocation_Config.allocation_size);
    return 0;
}

