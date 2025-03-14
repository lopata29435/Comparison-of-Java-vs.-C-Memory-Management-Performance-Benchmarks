cmake_minimum_required(VERSION 3.14)

project(ContestX_skeleton CXX)

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Debug)
endif(NOT CMAKE_BUILD_TYPE)

set(CMAKE_CXX_STANDARD 23)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3")

find_package(Threads REQUIRED)

add_executable(P1   main.cpp
                    ByteBench.hpp
                    ComplexObjectBench.hpp
                    AllocatorThreadBench.hpp
                    AllocatorBench.hpp
                    MemoryAccessBench.hpp
                    MemoryFragmentationBench.hpp
                    RecursiveAllocationBench.hpp
                    )
                    
target_link_libraries(P1 PRIVATE Threads::Threads)
