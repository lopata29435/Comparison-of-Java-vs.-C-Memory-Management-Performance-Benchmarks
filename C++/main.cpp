#include "ByteBench.hpp"
#include "AllocatorBench.hpp"
#include "AllocatorThreadBench.hpp"
#include "ByteNewBench.hpp"
#include "ComplexObjectBench.hpp"


int main() {
    BenchmarkResults results = {0, 0};

    ByteBench byteBench = ByteBench();
    ByteNewBench byteNewBench = ByteNewBench();
    ComplexObjectBench complexObjectBench = ComplexObjectBench();
    AllocatorBench allocatorBench = AllocatorBench();
    AllocatorThreadBench allocatorThreadBench = AllocatorThreadBench();


    //Template for all run function arguments (in brackets special cases, depends on realization):
    // Iterations(num of elements), Size of element(max size of elemnt), additional args(threads, e.t.c)
    std::cout << "-----------------------Byte Bench------------------------------------------\n";
    byteBench.run(1000, 1000);
    std::cout << "-----------------------Byte New Bench--------------------------------------\n";
    byteNewBench.run(1000, 1000);
    std::cout << "-----------------------Complex Object Bench--------------------------------\n";
    complexObjectBench.run(1000, 1000);
    std::cout << "-----------------------Allocator bench-------------------------------------\n";
    allocatorBench.run(100000, 1000, results);
    results = {0, 0};
    std::cout << "-----------------------Allocator Thread Bench------------------------------\n";
    allocatorThreadBench.run(1000, 1000, 20, results);

    return 0;
}

