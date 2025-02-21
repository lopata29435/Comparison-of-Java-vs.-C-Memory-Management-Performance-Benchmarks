# Benchmark for performance testing of C++ and Java memory managers

This repository contains a set of benchmarks that are identical in both languages for testing the performance of C++ and Java memory managers. Benchmarks are configured via the benchmarks_config.txt configuration file and random input data is generated by the dataGen.py script.

## Requirements

Before you start, make sure you have the following tools installed.

In brackets are the versions on which I built all this, so everything works exactly on them:
- **C++ Clang compiler** (18.1.3)
- **Java SDK** (JDK21)
- **Gradle** (8.12) for building the Java part of the project
- **CMake** (3.31.5) for building the C++ part of the project
- **Python** (3.12.1 or higher, will not affect the benchmark results) for generating input data files

## Installation Benchmarks
### Step 1: Clone a repository
```bash
git clone https://github.com/lopata29435/Comparison-of-Java-vs.-C-Memory-Management-Performance-Benchmarks.git
cd Comparison-of-Java-vs.-C-Memory-Management-Performance-Benchmarks/
```
### Step 2: Build Makefile
```bash
make build
```

## Running benchmarks

### Step 1: Preparing the configuration file
Benchmarks are managed through a configuration file. This file defines the parameters for each benchmark.

below is the structure of the file, #mutable is labelled with the fields to be changed
```ini
[ByteBench]
iterations= #mutable
allocation_size= #mutable

[ByteNewBench]
iterations= #mutable
allocation_size= #mutable

[ComplexObjectBench]
iterations= #mutable
element_count= #mutable

[AllocatorBench]
sizes_length= #mutable
allocation_sizes_file=../../data/AllocatorBench/allocation_sizes.txt

[AllocatorThreadBench]
sizes_length= #mutable
allocation_sizes_file=../../data/AllocatorThreadBench/allocation_sizes.txt
threads_num= #mutable

[MemoryFragmentationBench]
sizes_length= #mutable
allocation_sizes_file=../../data/MemoryFragmentationBench/allocation_sizes.txt
free_patterns_file=../../data/MemoryFragmentationBench/free_patterns.txt

[RecursiveAllocationBench]
depth= #mutable
allocation_size= #mutable

[MemoryAccessBench]
element_count= #mutable
indices_length= #mutable
access_indices_file=../../data/MemoryAccessBench/access_indices.txt
```

### Step 2: Generate input data files

The benchmarks require input data files such as allocation_sizes.txt, access_indices.txt, and free_patterns.txt. These files contain random values that simulate different memory allocation sizes, access patterns, and memory fragmentation.

Use the provided Python script to generate these files based on the configuration file. The script will automatically create the necessary directories and populate the files with random data.

Run the Python script:
```bash
python3 dataGen.py
```
You can also specify the --max-power parameter. This parameter sets the limit of the maximum size of allocated memory in randomly generated data. If for example --max-power 10, the maximum size will be 2^10 bytes. default value is 12.
```bash
python3 dataGen.py --max-power 10
```

### Step 3: Run benchmarks
```bash
make run
```

## Configuration File Details
The configuration file (benchmark_config.txt) defines the parameters for each benchmark. Below is an explanation of the various sections and their options:

### ByteBench
- **iterations**: The number of iterations for the benchmark. Each iteration performs a memory allocation.
- **allocation_size**: The size of memory allocated in each iteration (in bytes).

### ByteNewBench.
- **iterations**: The number of iterations for the benchmark.
- **allocation_size**: The size of memory allocated in each iteration using the `new` operator (in bytes).

### ComplexObjectBench
- **iterations**: The number of iterations for the benchmark.
- **element_count**: The number of complex objects (such as structures or classes) to be allocated in each iteration.

### AllocatorBench
- **sizes_length**: The number of memory allocation sizes to generate.
- **allocation_sizes_file**: Path to the file containing the memory allocation sizes for the benchmark (generated by a Python script).

### AllocatorThreadBench
- **sizes_length**: Number of memory allocation sizes to generate.
- **allocation_sizes_file**: Path to the file containing the memory allocation sizes for the benchmark.
- **threads_num**: The number of threads for the multitasking memory allocation benchmark.

### MemoryFragmentationBench
- **sizes_length**: Number of memory allocation sizes to generate.
- **allocation_sizes_file**: Path to the file containing the memory allocation sizes for the benchmark.
- **free_patterns_file**: Path to the file containing the memory freeing patterns (random values 0 or 1) for the fragmentation simulation.

### RecursiveAllocationBench
- **depth**: Recursive memory allocation depth.
- **allocation_size**: The size of memory allocated at each recursion level (in bytes).

### MemoryAccessBench
- **element_count**: The number of elements in the array to be accessed.
- **indices_length**: The number of random indices to access in the array.
- **access_indices_file**: Path to the file containing the indices to be accessed in the benchmark.

## Description of benchmark scenarios

### ByteBench
Benchmark to measure the performance of fixed-size memory allocation. Each iteration performs memory allocation of the specified size. Malloc is used for allocation.

### ByteNewBench
Benchmark to measure memory allocation performance using `new` operator. Each iteration performs a memory allocation of the specified size using the `new` operator.

### ComplexObjectBench
Benchmark for allocating memory for complex objects with a specified number of elements. Each iteration allocates memory for a complex object the specified number of iterations. A complex object is a class with a field containing a vector of the specified size.

### AllocatorBench
Benchmark for testing memory allocation using different sizes. The sizes to be allocated are generated in a Python script and passed to the benchmark via a file.

### AllocatorThreadBench
Benchmark for multitasking memory allocation testing. Uses several threads to simulate real multitasking conditions, each of which allocates memory of different sizes.

### MemoryFragmentationBench
Benchmark for simulating memory fragmentation. Includes testing of memory allocation and release with different sizes and random memory release patterns.


### RecursiveAllocationBench
Benchmark for testing recursive memory allocation. Each iteration performs recursive memory allocation with specified depth and size at each level.

### MemoryAccessBench
Benchmark for measuring memory access performance. Each access is performed on random indices in an array with a specified number of elements.
### C++ Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.39773 milliseconds
Time for 1000 deallocations: 0.09656 milliseconds
-----------------------Byte New Bench--------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.323952 milliseconds
Time for 1000 deallocations: 0.07025 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 3e-05 milliseconds
Time to allocate and free 1000 complex objects: 0.21499 milliseconds
-----------------------Allocator bench-------------------------------------
Time for 10 allocations: 0 milliseconds
Max memory used: 365568 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 80 milliseconds
Max memory used: 4231680 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.012924 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 3e-05 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 6e-05 milliseconds

### Java Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.0 milliseconds
Time for 1000 deallocations: 0.0 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 0.0 milliseconds
Time to allocate and free 1000 complex objects: 5.0 milliseconds
-----------------------Allocator Bench-------------------------------------
Time for 10 allocations: 4 milliseconds
Max memory used: 365568 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 1128 milliseconds
Max memory used: 4231680 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.334402 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 0.239475 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 0.014397 milliseconds
```
### C++ Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.467022 milliseconds
Time for 1000 deallocations: 0.07543 milliseconds
-----------------------Byte New Bench--------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.351256 milliseconds
Time for 1000 deallocations: 0.073277 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 3e-05 milliseconds
Time to allocate and free 1000 complex objects: 0.195896 milliseconds
-----------------------Allocator bench-------------------------------------
Time for 10 allocations: 1 milliseconds
Max memory used: 1186816 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 1 milliseconds
Max memory used: 108032 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.011091 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 3e-05 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 6e-05 milliseconds

### Java Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.0 milliseconds
Time for 1000 deallocations: 0.0 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 0.0 milliseconds
Time to allocate and free 1000 complex objects: 5.0 milliseconds
-----------------------Allocator Bench-------------------------------------
Time for 10 allocations: 4 milliseconds
Max memory used: 1186816 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 189 milliseconds
Max memory used: 108032 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.511939 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 0.237956 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 0.012554 milliseconds
```
## Latest Benchmarks results
```
### C++ Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.552531 milliseconds
Time for 1000 deallocations: 0.105166 milliseconds
-----------------------Byte New Bench--------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.389316 milliseconds
Time for 1000 deallocations: 0.070782 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 3e-05 milliseconds
Time to allocate and free 1000 complex objects: 0.195806 milliseconds
-----------------------Allocator bench-------------------------------------
Time for 10 allocations: 2 milliseconds
Max memory used: 1077760 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 156 milliseconds
Max memory used: 7356416 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.00515 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 3e-05 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 5e-05 milliseconds

### Java Benchmark Results
-----------------------Byte Bench------------------------------------------
Time for 1000 allocations of size 1000 bytes: 0.0 milliseconds
Time for 1000 deallocations: 0.0 milliseconds
-----------------------Complex Object Bench--------------------------------
Time to allocate and free 1000 primitive integers: 0.0 milliseconds
Time to allocate and free 1000 complex objects: 3.0 milliseconds
-----------------------Allocator Bench-------------------------------------
Time for 10 allocations: 5 milliseconds
Max memory used: 1077760 bytes
-----------------------Allocator Thread Bench------------------------------
Threads Num is: 20
Time for 10 allocations: 1465 milliseconds
Max memory used: 7356416 bytes
-----------------------Memory Fragmentation Bench--------------------------
Fragmentation test for 10 allocations with predefined sizes and free patterns: 0.095659 milliseconds
-----------------------Recursive Allocation Bench--------------------------
Recursive allocation test with depth 1000 and allocation size 128 bytes: 0.250797 milliseconds
-----------------------Memory Access Bench---------------------------------
Memory access test for 10 accesses in memory block of size 4000 bytes: 0.012152 milliseconds
```
