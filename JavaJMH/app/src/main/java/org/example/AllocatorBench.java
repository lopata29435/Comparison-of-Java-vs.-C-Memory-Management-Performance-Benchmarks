package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-Xms2G"})
public class AllocatorBench {

    private List<Integer> allocationSizes;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.txt");
        BenchmarkConfig config = configs.get("AllocatorBench");

        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);
    }

    @Benchmark
    public void runBenchmark() {
        if (allocationSizes == null) {
            throw new IllegalStateException("Allocation sizes must be set before running the benchmark.");
        }

        List<int[]> allocations = new ArrayList<>(allocationSizes.size());

        for (int allocSize : allocationSizes) {
            int[] arr = new int[allocSize];
            allocations.add(arr);

            for (int j = 0; j < allocSize; j++) {
                arr[j] = j;
            }

            if (allocations.size() > allocationSizes.size() / 2) {
                allocations.remove(allocations.size() - 1);
            }
        }

        allocations.clear();
    }

    public void run() throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName())
                .forks(1)
                .output("AllocatorBench_results.txt")
                .build();

        new Runner(opt).run();
    }
}
