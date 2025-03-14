package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class MemoryFragmentationBench {

    private List<Integer> allocationSizes;
    private List<Boolean> freePatterns;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.txt");
        BenchmarkConfig config = configs.get("MemoryFragmentationBench");

        freePatterns = DataLoader.loadBooleanListFromFile(config.freePatternsFile);
        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);
    }

    @Benchmark
    public double measureFragmentation() {
        if (allocationSizes == null || freePatterns == null || allocationSizes.size() != freePatterns.size()) {
            throw new IllegalStateException("Allocation sizes and free patterns must be set and have the same size.");
        }

        List<byte[]> allocations = new ArrayList<>(allocationSizes.size());
        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.add(null);
        }

        long start = System.nanoTime();

        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.set(i, new byte[allocationSizes.get(i)]);

            if (freePatterns.get(i)) {
                allocations.set(i, null);
            }
        }

        long end = System.nanoTime();

        System.gc();

        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureFragmentation")
                .forks(1)
                .output("MemoryFragmentationBench_results.txt")
                .build();
        new Runner(opt).run();
    }
}
