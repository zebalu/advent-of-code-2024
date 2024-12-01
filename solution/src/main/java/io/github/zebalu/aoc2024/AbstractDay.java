package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

public abstract class AbstractDay {
    protected final String INPUT;
    protected final String TITLE;
    protected final int dayNumber;
    AbstractDay(String title, int dayNumber) {
        this(IOUtil.readInput(dayNumber), title, dayNumber);
    }
    AbstractDay(String input, String title, int dayNumber) {
        this.INPUT = input;
        this.TITLE = title;
        this.dayNumber = dayNumber;
    }

    abstract String part1();
    abstract String part2();
}
