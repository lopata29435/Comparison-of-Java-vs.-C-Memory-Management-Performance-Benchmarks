#include "ByteBench.hpp"
#include "AllocatorBench.hpp"
#include "AllocatorThreadBench.hpp"
#include "ByteNewBench.hpp"
#include "ComplexObjectBench.hpp"


int main() {
    BenchmarkResults results = {0, 0};

    ByteBench byteBench = ByteBench(1000, 1000);
    ByteNewBench byteNewBench = ByteNewBench(1000, 1000);
    ComplexObjectBench complexObjectBench = ComplexObjectBench();
    AllocatorBench allocatorBench = AllocatorBench();
    AllocatorThreadBench allocatorThreadBench = AllocatorThreadBench();

    std::cout << "-----------------------Byte Bench------------------------------------------\n";
    byteBench.run();
    std::cout << "-----------------------Byte New Bench--------------------------------------\n";
    byteNewBench.run();
    std::cout << "-----------------------Complex Object Bench--------------------------------\n";
    complexObjectBench.run(1000, 1000);
    std::cout << "-----------------------Allocator bench-------------------------------------\n";
    allocatorBench.run(100000, 1000, results);
    results = {0, 0};
    std::cout << "-----------------------Allocator Thread Bench------------------------------\n";
    allocatorThreadBench.run(1000, 1000, 20, results);

    return 0;
}

