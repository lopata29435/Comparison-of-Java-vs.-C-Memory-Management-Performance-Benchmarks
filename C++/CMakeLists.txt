cmake_minimum_required(VERSION 3.14)

project(ContestX_skeleton CXX)

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Debug)
endif(NOT CMAKE_BUILD_TYPE)

set(CMAKE_CXX_STANDARD 23)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3")

find_package(Threads REQUIRED)

add_executable(P1   main.cpp
                    Fixed_Size_Allocation.hpp
                    Complex_Object_Allocation.hpp
                    Concurrent_Multithreaded_Allocation_Performance.hpp
                    Variable_Size_Allocation.hpp
                    Fragmentation_Test_Random_Allocation_Release.hpp
                    Recursive_Memory_Allocation.hpp
                    )
                    
target_link_libraries(P1 PRIVATE Threads::Threads)
