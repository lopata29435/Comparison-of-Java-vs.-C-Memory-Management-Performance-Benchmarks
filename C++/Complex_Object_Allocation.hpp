#ifndef COMPLEX_OBJECT_ALLOCATION_HPP
#define COMPLEX_OBJECT_ALLOCATION_HPP

#include <iostream>
#include <chrono>
#include <memory>
#include <fstream>
#include <cstdint>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

// Some complex object.
class ComplexObject {
public:
    ComplexObject(size_t size)
        : data_(new int8_t[size], std::default_delete<int8_t[]>()) {
        size_ = size;
        for (size_t i = 0; i < size_; ++i) {
            data_.get()[i] = static_cast<int8_t>(i);
        }
    }

private:
    size_t size_;
    std::unique_ptr<int8_t[]> data_;
};



class Complex_Object_Allocation {
public:
    volatile ComplexObject* volatile_obj;

    void run(size_t iterations, size_t complex_size) {
        double complex_time = measure_allocate_complex(iterations, complex_size);
        std::cout << "Time to allocate and free " << iterations << " complex objects: "
                  << complex_time << " milliseconds" << std::endl;

        std::ofstream file("CXX_Benchmark_results.txt", std::ios::app);

        if (file.is_open()) {
            file << "Complex Object Allocation" << std::endl;
            file << complex_time << std::endl;
            file << std::endl;
        } else {
            std::cerr << "Failed to open the file for writing." << std::endl;
        }

        file.close();
    }

private:
    double measure_allocate_complex(size_t count, size_t complex_size) {
        const size_t bufferSize = 10000;
        ComplexObject* buffer[bufferSize];
        size_t bufferIndex = 0;

        TimePoint start = Clock::now();
        for (size_t i = 0; i < count; ++i) {
            buffer[bufferIndex] = new ComplexObject(complex_size);
            bufferIndex++;
            
            if (bufferIndex >= bufferSize) {
                for (size_t j = 0; j < bufferSize; j++) {
                    delete buffer[j];
                    buffer[j] = nullptr;
                }
                bufferIndex = 0;
            }
        }
        for (size_t j = 0; j < bufferIndex; j++) {
            delete buffer[j];
        }
        TimePoint end = Clock::now();
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //COMPLEX_OBJECT_ALLOCATION_HPP