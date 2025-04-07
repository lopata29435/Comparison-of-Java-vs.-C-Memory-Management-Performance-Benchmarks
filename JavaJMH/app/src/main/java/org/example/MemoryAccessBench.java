package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Map;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class MemoryAccessBench {

    private int elementCount;
    private List<Integer> accessIndices;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.txt");
        BenchmarkConfig config = configs.get("MemoryAccessBench");

        accessIndices = DataLoader.loadIntegerListFromFile(config.accessIndicesFile);
        elementCount = config.elementCount;
    }

    @Benchmark
    public double measureMemoryAccess() {
        if (elementCount <= 0 || accessIndices == null || accessIndices.isEmpty()) {
            throw new IllegalStateException("Element count and access indices must be set before running the benchmark.");
        }

        int[] data = new int[elementCount];

        for (int i = 0; i < elementCount; ++i) {
            data[i] = i;
        }

        int sum = 0;

        for (int index : accessIndices) {
            sum += data[index % elementCount];
        }

        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    public void run() throws RunnerException {
    
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureMemoryAccess")
                .forks(1)
                .output("MemoryAccessBench_results.txt")
                .build();

        new Runner(opt).run();
    }
}
