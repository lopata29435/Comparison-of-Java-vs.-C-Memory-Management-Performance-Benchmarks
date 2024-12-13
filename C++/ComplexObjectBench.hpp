#ifndef COMPLEXOBJECTBENCH_HPP
#define COMPLEXOBJECTBENCH_HPP

#include <iostream>
#include <chrono>
#include <memory>

using Clock = std::chrono::high_resolution_clock;
using TimePoint = std::chrono::time_point<Clock>;

// Some complex object.
class ComplexObject {
public:
    ComplexObject(size_t size)
        : data_(new int[size], std::default_delete<int[]>()) {
        size_ = size;
        for (size_t i = 0; i < size_; ++i) {
            data_.get()[i] = static_cast<int>(i);
        }
    }

private:
    size_t size_;
    std::unique_ptr<int[]> data_;
};



class ComplexObjectBench {
public:
    void run(size_t iterations, size_t complex_size) {
        double primitive_time = measure_allocate_primitive(iterations);
        std::cout << "Time to allocate and free " << iterations << " primitive integers: "
                  << primitive_time << " milliseconds" << std::endl;

        double complex_time = measure_allocate_complex(iterations, complex_size);
        std::cout << "Time to allocate and free " << iterations << " complex objects: "
                  << complex_time << " milliseconds" << std::endl;
    }

private:
    // Measure primitive allocation time in milliseconds.
    double measure_allocate_primitive(size_t count) {
        TimePoint start = Clock::now();
        for (size_t i = 0; i < count; ++i) {
            int* ptr = new int(i);
            delete ptr;
        }
        TimePoint end = Clock::now();
        return std::chrono::duration<double, std::milli>(end - start).count();
    }

    // Measure complex object allocation time in milliseconds.
    double measure_allocate_complex(size_t count, size_t complex_size) {
        TimePoint start = Clock::now();
        for (size_t i = 0; i < count; ++i) {
            ComplexObject* obj = new ComplexObject(complex_size);
            delete obj;
        }
        TimePoint end = Clock::now();
        return std::chrono::duration<double, std::milli>(end - start).count();
    }
};

#endif //COMPLEXOBJECTBENCH_HPP