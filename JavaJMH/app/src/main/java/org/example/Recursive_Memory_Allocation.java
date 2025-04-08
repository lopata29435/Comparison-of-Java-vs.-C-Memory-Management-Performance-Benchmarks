package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss16m"})
public class Recursive_Memory_Allocation {

    @Param({"1000"}) 
    private int depth;
    @Param({"128"}) 
    private int allocationSize;

    private byte[][] blocks;

    @Setup(Level.Iteration)
    public void setup() {
        blocks = new byte[depth][];
    }

    @Benchmark
    public void measureRecursiveAllocation() {
        recursiveAllocation(blocks, allocationSize, 0);
        for (int i = 0; i < depth; i++) {
            blocks[i] = null;
        }
        System.gc();
    }
    
    private void recursiveAllocation(byte[][] blocks, int size, int index) {
        if (index >= blocks.length) return;
        byte[] ptr = new byte[size];
        Arrays.fill(ptr, (byte) 0);
        blocks[index] = ptr;
        recursiveAllocation(blocks, size, index + 1);
        Arrays.fill(ptr, (byte) 1);
        System.arraycopy(ptr, 0, blocks[blocks.length - 1], 0, size);
    }

    public void run(long depth, long allocationSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureRecursiveAllocation")
                .param("depth", String.valueOf(depth))
                .param("allocationSize", String.valueOf(allocationSize))
                .forks(1)
                .output("Buf.txt")
                .result("Recursive_Memory_Allocation.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();

        new Runner(opt).run();
    }
}
