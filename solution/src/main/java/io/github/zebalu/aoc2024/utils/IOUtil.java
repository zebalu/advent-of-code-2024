package io.github.zebalu.aoc2024.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IOUtil {
    private IOUtil() {
        throw new IllegalAccessError("Utility class");
    }

    public static String readInput(int dayNumber) {
        String fileName = String.format("input-%02d.txt", dayNumber);
        try {
            File file = new File(fileName).getAbsoluteFile().getCanonicalFile();
            return Files.readString(file.toPath());
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Can not read input for day: "+dayNumber, ioe);
        }
    }

    public static List<List<Integer>> readIntLines(String data, String splitBy) {
       return data.lines().map(l -> l.split(splitBy)).map(splt -> {
            List<Integer> lst = new ArrayList<>(splt.length);
            Arrays.stream(splt).forEach(l -> lst.add(Integer.parseInt(l)));
            return lst;
        }).toList();
    }

    public static int[][] readIntGrid(String text) {
        var lines = text.lines().toList();
        int height = lines.size();
        int width = lines.getFirst().length();
        int[][] data = new int[height][width];
        for(int y = 0; y < height; y++) {
            String line = lines.get(y);
            data[y] = new int[width];
            for(int x = 0; x < width; x++) {
                int v = Integer.parseInt(line.substring(x, x + 1));
                data[y][x] = v;
            }
        }
        return data;
    }

    public static char[][] readCharGrid(String text) {
        var lines = text.lines().toList();
        int height = lines.size();
        int width = lines.getFirst().length();
        char[][] data = new char[height][width];
        for(int y = 0; y < height; y++) {
            String line = lines.get(y);
            data[y] = new char[width];
            for(int x = 0; x < width; x++) {
                data[y][x] = line.charAt(x);
            }
        }
        return data;
    }

}
