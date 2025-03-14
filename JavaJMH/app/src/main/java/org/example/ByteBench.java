package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class ByteBench {

    @Param({"1000"}) 
    private long iterations;
    @Param({"1000"}) 
    private int allocationSize;

    private List<byte[]> allocations;

    @Setup(Level.Iteration)
    public void setup() {
        allocations = new ArrayList<>((int) iterations);
    }

    @Benchmark
    public void measureAllocation() {
        if (iterations <= 0 || allocationSize <= 0) {
            throw new IllegalStateException("Iterations and allocationSize must be set before running the benchmark.");
        }

        for (int i = 0; i < iterations; i++) {
            byte[] allocation = new byte[allocationSize];
            if (allocation == null) {
                throw new RuntimeException("Allocation failed at iteration: " + i);
            }
            allocations.add(allocation);
        }
    }

    @Benchmark
    public void measureDeallocation() {
        if (allocations == null) {
            throw new IllegalStateException("Allocations list must be initialized before running the benchmark.");
        }

        allocations.clear();
    }

    public void run(long iterations, int allocationSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ByteBench.class.getSimpleName())
                .param("iterations", String.valueOf(iterations))
                .param("allocationSize", String.valueOf(allocationSize))
                .forks(1)
                .output("ByteBenchmark_results.txt")
                .build();
    
        new Runner(opt).run();
    }
}
