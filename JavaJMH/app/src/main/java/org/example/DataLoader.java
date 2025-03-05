package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static List<Integer> loadIntegerListFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = reader.readLine()) != null) {
            list.add(Integer.parseInt(line.trim()));
        }

        reader.close();
        return list;
    }

    public static List<Boolean> loadBooleanListFromFile(String filename) throws IOException {
        List<Boolean> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = reader.readLine()) != null) {
            int value = Integer.parseInt(line.trim());
            if (value != 0 && value != 1) {
                throw new RuntimeException("Invalid value in file: " + filename + ". Expected 0 or 1.");
            }
            list.add(value == 1);
        }

        reader.close();
        return list;
    }
}