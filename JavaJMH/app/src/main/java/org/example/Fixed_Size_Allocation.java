package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx32g", "-Xms4g", "-Xss8m"})
public class Fixed_Size_Allocation {

    @Param({"10"}) 
    private long iterations;
    @Param({"10"}) 
    private int allocationSize;

    private List<byte[]> allocations;

    @Setup(Level.Iteration)
    public void setup() {
        allocations = new ArrayList<>((int) iterations);
    }

    @Benchmark
    public void measureAllocation() {
        for (int i = 0; i < iterations; i++) {
            byte[] allocation = new byte[allocationSize];
            allocations.add(allocation);
        }
    }

    public void run(long iterations, int allocationSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Fixed_Size_Allocation.class.getSimpleName())
                .param("iterations", String.valueOf(iterations))
                .param("allocationSize", String.valueOf(allocationSize))
                .forks(1)
                .output("Buf.txt")
                .result("Fixed_Size_Allocation.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();
    
        new Runner(opt).run();
    }
}
