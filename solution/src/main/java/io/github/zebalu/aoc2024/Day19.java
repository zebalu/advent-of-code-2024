package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day19 extends AbstractDay {
    private final List<String> towels;
    private final List<String> designs;
    private final Map<String, Long> memory = new HashMap<>();

    public Day19() {
        this(IOUtil.readInput(19));
    }

    public Day19(String input) {
        super(input, "Linen Layout", 19);
        var data = IOUtil.groupByEmptyLines(INPUT);
        towels = Arrays.asList(data.getFirst().getFirst().split(", "));
        designs = data.getLast();
        designs.forEach(this::countPossible);
    }

    @Override
    public String part1() {
        long count = designs.stream().filter(d->memory.get(d)>0L).count();
        return Long.toString(count);
    }

    @Override
    public String part2() {
        long count = designs.stream().mapToLong(memory::get).sum();
        return Long.toString(count);
    }

    private long countPossible(String design) {
        if (memory.containsKey(design)) {
            return memory.get(design);
        }
        long count = 0L;
        for (String towel : towels) {
            if (design.equals(towel)) {
                ++count;
            } else if (design.startsWith(towel)) {
                count += countPossible(design.substring(towel.length()));
            }
        }
        memory.put(design, count);
        return count;
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(19);
        Day19 day19 = new Day19(input);
        System.out.println(day19.part1());
        System.out.println(day19.part2());
    }
}
