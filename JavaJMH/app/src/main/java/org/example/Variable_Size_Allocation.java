package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

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
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class Variable_Size_Allocation {

    private List<Integer> allocationSizes;
    private List<byte[]> allocations;
    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.ini");
        BenchmarkConfig config = configs.get("Variable_Size_Allocation");

        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);
        allocations = new ArrayList<>(allocationSizes.size());
    }

    @Benchmark
    public void runBenchmark() {
        for (int allocSize : allocationSizes) {
            byte[] arr = new byte[allocSize];
            allocations.add(arr);

            for (int j = 0; j < allocSize; j++) {
                arr[j] = (byte) j;
            }
        }

        allocations.clear();
        System.gc();
    }

    public void run() throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName())
                .forks(1)
                .output("Buf.txt")
                .result("Variable_Size_Allocation.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();

        new Runner(opt).run();
    }
}
