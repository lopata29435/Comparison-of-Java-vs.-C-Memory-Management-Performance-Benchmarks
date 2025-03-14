package org.example;

import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// Some complex object.
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

class ComplexObjectBench {

    public void run(int iterations, int complexSize) {
        double primitiveTime = measureAllocatePrimitive(iterations);
        System.out.println("Time to allocate and free " + iterations + " primitive integers: "
                + primitiveTime + " milliseconds");

        double complexTime = measureAllocateComplex(iterations, complexSize);
        System.out.println("Time to allocate and free " + iterations + " complex objects: "
                + complexTime + " milliseconds");

        try (FileWriter fileWriter = new FileWriter("Java_Benchmark_results.txt", true)) {
            fileWriter.write("Primitive object bench allocation and deallocation\n");
            fileWriter.write(String.valueOf(primitiveTime));
            fileWriter.write("\n\n");
            fileWriter.write("Complex object bench allocation and deallocation\n");
            fileWriter.write(String.valueOf(complexTime));
            fileWriter.write("\n\n");
        } catch (IOException e) {
            System.err.println("Failed to open the file for writing.");
            e.printStackTrace();
        }
    }

    // Measure primitive allocation time in milliseconds.
    private double measureAllocatePrimitive(int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; ++i) {
            int[] ptr = new int[1];
            ptr[0] = i;
        }
        long end = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    // Measure complex object allocation time in milliseconds.
    private double measureAllocateComplex(int count, int complexSize) {
        long start = System.nanoTime();
        for (int i = 0; i < count; ++i) {
            ComplexObject obj = new ComplexObject(complexSize);
        }
        long end = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }
}