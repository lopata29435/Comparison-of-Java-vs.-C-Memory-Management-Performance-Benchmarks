package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;


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
public class Fragmentation_Test_Random_Allocation_Release {

    private List<Integer> allocationSizes;
    private List<Boolean> freePatterns;
    private List<byte[]> allocations;
    final int gcInterval = 1000; 
    int gcCounter = 0;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.ini");
        BenchmarkConfig config = configs.get("Fragmentation_Test_Random_Allocation_Release");

        freePatterns = DataLoader.loadBooleanListFromFile(config.freePatternsFile);
        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);

        allocations = new ArrayList<>(allocationSizes.size());
        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.add(null);
        }
    }

    @Benchmark
    public void measureFragmentation() {
        for (int i = 0; i < allocationSizes.size(); ++i) {
            allocations.set(i, new byte[allocationSizes.get(i)]);
            if (freePatterns.get(i)) {
                allocations.set(i, null);
                System.gc();
            }
        }
    }


    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureFragmentation")
                .forks(1)
                .output("Buf.txt")
                .result("Fragmentation_Test_Random_Allocation_Release.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();
        new Runner(opt).run();
    }
}
