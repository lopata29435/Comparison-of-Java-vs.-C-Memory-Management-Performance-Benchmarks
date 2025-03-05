package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
@Fork(1)
public class AllocatorThreadBench {

    private List<Integer> allocationSizes;
    private int threadsNum;
    private final ReentrantLock timeLock = new ReentrantLock();

    @Setup(Level.Trial)
    public void setup() throws IOException {
        Map<String, BenchmarkConfig> configs = BenchmarkConfigLoader.loadConfig("../../benchmarks_config.txt");
        BenchmarkConfig config = configs.get("AllocatorThreadBench");

        allocationSizes = DataLoader.loadIntegerListFromFile(config.allocationSizesFile);
        threadsNum = config.threadsNum;
    }

    @Benchmark
    public void runBenchmark() {
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < threadsNum; ++i) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                ArrayList<int[]> allocations = new ArrayList<>();
                allocations.ensureCapacity(allocationSizes.size());

                
                for (int allocSize : allocationSizes) {
                    int[] ptr = new int[allocSize];
                    allocations.add(ptr);
                    
                    for (int j = 0; j < allocSize; ++j) {
                        ptr[j] = threadIndex + j;
                    }

                    if (allocations.size() > allocationSizes.size() / 2) {
                        allocations.remove(allocations.size() - 1);
                    }
                }

                allocations.clear();
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".runBenchmark")
                .forks(1)
                .output("AllocatorThreadBench_results.txt")
                .build();

        new Runner(opt).run();
    }
}