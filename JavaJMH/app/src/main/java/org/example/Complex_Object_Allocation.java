package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.util.concurrent.TimeUnit;

class ComplexObject {
    private int size;
    private byte[] data;

    public ComplexObject(int size) {
        this.size = size;
        this.data = new byte[size];
        for (int i = 0; i < size; ++i) {
            data[i] = (byte) i;
        }
    }
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx16g", "-Xms4g", "-Xss8m"})
public class Complex_Object_Allocation {

    @Param({"10"}) 
    private int iterations;
    @Param({"10"}) 
    private int complexSize;

    private final int bufferSize = 10000;
    private ComplexObject[] buffer = new ComplexObject[bufferSize];
    int bufferIndex = 0;

    @Benchmark
    public void measureAllocateComplex() {
        for (int i = 0; i < iterations; ++i) {
        buffer[bufferIndex] = new ComplexObject(complexSize);
        bufferIndex++;
        
        if (bufferIndex >= bufferSize) {
            for (int j = 0; j < bufferSize; j++) {
                buffer[j] = null;
            }
            System.gc();
            bufferIndex = 0;
        }
    }
    }

    public void run(int iterations, int complexSize) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName() + ".measureAllocatePrimitive")
                .include(this.getClass().getSimpleName() + ".measureAllocateComplex")
                .param("iterations", String.valueOf(iterations))
                .param("complexSize", String.valueOf(complexSize))
                .forks(1)
                .output("Buf.txt")
                .result("Complex_Object_Allocation.json")
                .resultFormat(ResultFormatType.JSON) 
                .build();

        new Runner(opt).run();
    }
}
