package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx32g", "-Xms8g", "-Xss8m"})
public class Concurrent_Multithreaded_Allocation_Performance {

    private List<Integer> allocationSizes;
    private int threadsNum;
    private final ReentrantLock timeLock = new ReentrantLock();
    private List<Thread> threads;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.ini");
        BenchmarkConfig config = configs.get("Concurrent_Multithreaded_Allocation_Performance");

        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);
        threadsNum = config.threadsNum;

        threads = new ArrayList<>();
    }

    @Benchmark
    public void runBenchmark() throws InterruptedException{
        for (int i = 0; i < threadsNum; ++i) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                ArrayList<byte[]> allocations = new ArrayList<>();
                allocations.ensureCapacity(allocationSizes.size());

                for (int allocSize : allocationSizes) {
                    byte[] ptr = new byte[allocSize];
                    allocations.add(ptr);
                    
                    for (int j = 0; j < allocSize; ++j) {
                        ptr[j] = (byte) (threadIndex + j);
                    }
                }

                allocations.clear();
                System.gc();
            });

            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".runBenchmark")
                .forks(1)
                .output("Buf.txt")
                .result("Concurrent_Multithreaded_Allocation_Performance.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();

        new Runner(opt).run();
    }
}
