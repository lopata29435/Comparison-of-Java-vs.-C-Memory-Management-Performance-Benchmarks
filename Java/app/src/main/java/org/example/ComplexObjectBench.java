package org.example;

import java.util.concurrent.TimeUnit;

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