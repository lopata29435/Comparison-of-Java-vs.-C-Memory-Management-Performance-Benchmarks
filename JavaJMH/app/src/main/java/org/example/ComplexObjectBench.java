package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

class ComplexObject {
    private int size;
    private int[] data;

    public ComplexObject(int size) {
        this.size = size;
        this.data = new int[size];
        for (int i = 0; i < size; ++i) {
            data[i] = i;
        }
    }
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class ComplexObjectBench {

    @Param({"1000"}) 
    private int iterations;
    @Param({"1000"}) 
    private int complexSize;

    @Benchmark
    public void measureAllocatePrimitive() {
        if (iterations <= 0) {
            throw new IllegalStateException("Iterations must be set before running the benchmark.");
        }

        for (int i = 0; i < iterations; ++i) {
            int[] ptr = new int[1];
            ptr[0] = i;
        }
    }

    @Benchmark
    public void measureAllocateComplex() {
        if (iterations <= 0 || complexSize <= 0) {
            throw new IllegalStateException("Iterations and complexSize must be set before running the benchmark.");
        }

        for (int i = 0; i < iterations; ++i) {
            ComplexObject obj = new ComplexObject(complexSize);
        }
    }

    public void run(int iterations, int complexSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureAllocatePrimitive")
                .include(this.getClass().getSimpleName() + ".measureAllocateComplex")
                .param("iterations", String.valueOf(iterations))
                .param("complexSize", String.valueOf(complexSize))
                .forks(1)
                .output("ComplexObjectBenchmark_results.txt")
                .build();

        new Runner(opt).run();
    }
}
