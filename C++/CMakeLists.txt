cmake_minimum_required(VERSION 3.14)

project(ContestX_skeleton CXX)

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Debug)
endif(NOT CMAKE_BUILD_TYPE)

set(CMAKE_CXX_STANDARD 20)

add_executable(P1   main.cpp
                    ByteBench.hpp
                    ByteNewBench.hpp
                    ComplexObjectBench.hpp
                    AllocatorThreadBench.hpp
                    AllocatorBench.hpp
                    )