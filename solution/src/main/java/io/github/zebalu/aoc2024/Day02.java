package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day02 extends AbstractDay {
    private final List<List<Integer>> data;

    public Day02() {
        this(IOUtil.readInput(2));
    }

    public Day02(String input) {
        super(input, "Red-Nosed Reports", 2);
        data = IOUtil.readIntLines(input, " ");
    }

    @Override
    public String part1() {
        return Long.toString(data.stream().filter(this::isSafe).count());
    }

    private boolean isSafe(List<Integer> list) {
        boolean isSafe = true;
        boolean isGrowing = list.get(0) < list.get(1);
        int idx = 0;
        while (isSafe && idx < list.size() - 1) {
            isSafe = isGrowing ? list.get(idx) < list.get(idx + 1) : list.get(idx) > list.get(idx + 1);
            int change = Math.abs(list.get(idx) - list.get(idx + 1));
            isSafe &= (1 <= change && change <= 3);
            idx++;
        }
        return isSafe;
    }

    @Override
    public String part2() {
        return Long.toString(data.stream().filter(this::isSafeDampened).count());
    }

    private boolean isSafeDampened(List<Integer> list) {
        return isSafe(list) || IntStream.range(0, list.size()).mapToObj(idx -> {
            List<Integer> dampened = new ArrayList<>(list);
            dampened.remove(idx);
            return dampened;
        }).anyMatch(this::isSafe);
    }

    public static void main(String[] args) {
        Day02 day02 = new Day02();
        System.out.println(day02.part1());
        System.out.println(day02.part2());
    }

}
