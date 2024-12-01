package io.github.zebalu.aoc2024.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

}
