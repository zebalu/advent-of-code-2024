package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day22 extends AbstractDay {
    private static final long MODULO = 16_777_216L;
    private static final long MULTIPLIER1 = 64L;
    private static final long MULTIPLIER2 = 2048L;
    private static final long DIVISOR = 32L;
    private static final long STEP_COUNT = 2_000L;
    private final long[] secretNumbers;

    public Day22() {
        this(IOUtil.readInput(22));
    }

    public Day22(String input) {
        super(input, "Monkey Market", 22);
        secretNumbers = INPUT.lines().mapToLong(Long::parseLong).toArray();
    }

    @Override
    public String part1() {
        long sum = 0L;
        for (long sn : secretNumbers) {
            sum += generateNextNumber(sn);
        }
        return "" + sum;
    }

    @Override
    public String part2() {
        Map<List<Long>, Long> summingMap = new HashMap<>();
        for (long sn : secretNumbers) {
            generateSequences(sn).forEach(
                    (key, value) -> summingMap.compute(key, (_, v) -> v == null ? value : v + value)
            );
        }
        long res = summingMap.values().stream().mapToLong(Long::longValue).max().orElseThrow();
        return "" + res;
    }

    private long nextNumber(long seed) {
        long secretNumber = seed;

        secretNumber = ((secretNumber * MULTIPLIER1) ^ secretNumber) % MODULO;

        secretNumber = ((secretNumber / DIVISOR) ^ secretNumber) % MODULO;

        secretNumber = ((secretNumber * MULTIPLIER2) ^ secretNumber) % MODULO;

        return secretNumber;
    }

    private long generateNextNumber(long seed) {
        long result = seed;
        for (int i = 0; i < STEP_COUNT; i++) {
            result = nextNumber(result);
        }
        return result;
    }

    private Map<List<Long>, Long> generateSequences(long seed) {
        Map<List<Long>, Long> result = new HashMap<>();
        long prevSeed = seed;
        long prevPrice = seed % 10L;
        List<Long> collector = new LinkedList<>();
        for (int i = 0; i < STEP_COUNT; i++) {
            long nextSeed = nextNumber(prevSeed);
            long price = nextSeed % 10L;
            long diff = price - prevPrice;
            collector.add(diff);
            if (collector.size() == 4) {
                if (!result.containsKey(collector)) {
                    result.put(new ArrayList<>(collector), price);
                }
                collector.removeFirst();
            }
            prevPrice = price;
            prevSeed = nextSeed;
        }
        return result;
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(22);
        Day22 day22 = new Day22(input);
        System.out.println(day22.part1());
        System.out.println(day22.part2());
    }
}
