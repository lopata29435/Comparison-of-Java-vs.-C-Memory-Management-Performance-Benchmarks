package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-Xms2G"})
public class RecursiveAllocationBench {

    @Param({"1000"}) 
    private long depth;
    @Param({"128"}) 
    private long allocationSize;


    @Benchmark
    public void measureRecursiveAllocation() {
        if (depth < 0 || allocationSize <= 0) {
            throw new IllegalStateException("Depth and allocation size must be set before running the benchmark.");
        }

        byte[] last = recursiveAllocation(depth, allocationSize);
    }

    private byte[] recursiveAllocation(long depth, long size) {
        if (depth == 0) {
            return new byte[(int) size];
        }

        byte[] ptr = new byte[(int) size];
        byte[] child = recursiveAllocation(depth - 1, size);
        return child;
    }

    public void run(long depth, long allocationSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureRecursiveAllocation")
                .param("depth", String.valueOf(depth))
                .param("allocationSize", String.valueOf(allocationSize))
                .forks(1)
                .output("RecursiveAllocationBenchmark_results.txt")
                .build();

        new Runner(opt).run();
    }
}
