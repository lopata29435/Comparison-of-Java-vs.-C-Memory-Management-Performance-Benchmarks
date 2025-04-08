package org.example;

import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class BenchmarkRunner {
    public static void main(String[] args) throws RunnerException {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(System.getProperty("user.dir"));
            Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.ini");
  
            Fixed_Size_Allocation fixed_Size_Allocation = new Fixed_Size_Allocation();
            BenchmarkConfig fixed_Size_Allocation_Config = configs.get("Fixed_Size_Allocation");
            System.out.println("-----------------------Fixed_Size_Allocation--------------------------------------");
            fixed_Size_Allocation.run(fixed_Size_Allocation_Config.iterations, fixed_Size_Allocation_Config.allocationSize);

            Complex_Object_Allocation complex_Object_Allocation = new Complex_Object_Allocation();
            BenchmarkConfig complex_Object_Allocation_Config = configs.get("Complex_Object_Allocation");
            System.out.println("-----------------------Complex_Object_Allocation----------------------------------");
            complex_Object_Allocation.run(complex_Object_Allocation_Config.iterations, complex_Object_Allocation_Config.elementCount);

            Variable_Size_Allocation variable_Size_Allocation = new Variable_Size_Allocation();
            BenchmarkConfig variable_Size_Allocation_Config = configs.get("Variable_Size_Allocation");
            System.out.println("-----------------------Variable_Size_Allocation-----------------------------------");
            variable_Size_Allocation.run();

            Concurrent_Multithreaded_Allocation_Performance concurrent_Multithreaded_Allocation_Performance = new Concurrent_Multithreaded_Allocation_Performance();
            BenchmarkConfig concurrent_Multithreaded_Allocation_Performance_Config = configs.get("Concurrent_Multithreaded_Allocation_Performance");
            System.out.println("-----------------------Concurrent_Multithreaded_Allocation_Performance------------");
            concurrent_Multithreaded_Allocation_Performance.run();

            Fragmentation_Test_Random_Allocation_Release fragmentation_Test_Random_Allocation_Release = new Fragmentation_Test_Random_Allocation_Release();
            BenchmarkConfig fragmentation_Test_Random_Allocation_Release_Config = configs.get("Fragmentation_Test_Random_Allocation_Release");
            System.out.println("-----------------------Fragmentation_Test_Random_Allocation_Release---------------");
            fragmentation_Test_Random_Allocation_Release.run();

            Recursive_Memory_Allocation recursive_Memory_Allocation = new Recursive_Memory_Allocation();
            BenchmarkConfig recursive_Memory_Allocation_Config = configs.get("Recursive_Memory_Allocation");
            System.out.println("-----------------------Recursive_Memory_Allocation--------------------------------");
            recursive_Memory_Allocation.run(recursive_Memory_Allocation_Config.depth, recursive_Memory_Allocation_Config.allocationSize);
   
            BenchmarkParser.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}