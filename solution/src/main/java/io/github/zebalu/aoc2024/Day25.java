package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.List;
import java.util.stream.IntStream;

public class Day25 extends AbstractDay {
    private final List<List<Integer>> keys;
    private final List<List<Integer>> locks;

    public Day25() {
        this(IOUtil.readInput(25));
    }

    public Day25(String input) {
        super(input, "Code Chronicle", 25);
        var groups = IOUtil.groupByEmptyLines(INPUT);
        this.keys = countAllColumns(groups.stream().filter(l -> l.getLast().charAt(0) == '#').toList());
        this.locks = countAllColumns(groups.stream().filter(l -> l.getFirst().charAt(0) == '#').toList());
    }

    @Override
    public String part1() {
        long count = locks.stream()
                .flatMap(lock -> keys.stream().map(key -> IntStream.range(0, key.size())
                        .mapToObj(i -> lock.get(i) + key.get(i))
                        .toList()))
                .filter(l -> l.stream().allMatch(i -> i <= 5))
                .count();
        return Long.toString(count);
    }

    @Override
    public String part2() {
        return "Merry Christmas!";
    }

    private List<Integer> countColumns(List<String> keyOrLock) {
        Integer[] countArray = new Integer[keyOrLock.getFirst().length()];
        for (int i = 0; i < keyOrLock.getFirst().length(); i++) {
            int count = 0;
            for (String s : keyOrLock) {
                if (s.charAt(i) == '#') {
                    ++count;
                }
            }
            countArray[i] = count - 1;
        }
        return List.of(countArray);
    }

    private List<List<Integer>> countAllColumns(List<List<String>> keyOrLocks) {
        return keyOrLocks.stream().map(this::countColumns).toList();
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(25);
        Day25 day25 = new Day25(input);
        System.out.println(day25.part1());
        System.out.println(day25.part2());
    }

}
