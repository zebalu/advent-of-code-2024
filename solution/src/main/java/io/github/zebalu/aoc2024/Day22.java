package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Day22 extends AbstractDay {
    private static final long MODULO = 16_777_216L;
    private static final int MULTIPLIER1_STEPS_LEFT = 6; /// multiply by 64L;
    private static final int MULTIPLIER2_STEPS_LEFT = 11; /// multiply by 2048L;
    private static final int DIVISOR_STEPS_RIGHT = 5; /// divide by 32
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
            sum += generate2000thNumber(sn);
        }
        return Long.toString(sum);
    }

    @Override
    public String part2() {
        Map<List<Long>, Long> summingMap = new ConcurrentHashMap<>();
        Arrays.stream(secretNumbers).parallel().forEach(sn ->
            generateSequences(sn)
                    .forEach(
                            (key, value) ->
                                    summingMap.compute(key, (_, v) -> v == null ? value : v + value)
                    )
        );
        long max = summingMap.values().stream().mapToLong(Long::longValue).max().orElseThrow();
        return Long.toString(max);
    }

    private long nextNumber(long seed) {
        long secretNumber = seed;

        secretNumber = ((secretNumber << MULTIPLIER1_STEPS_LEFT) ^ secretNumber) % MODULO;

        secretNumber = ((secretNumber >> DIVISOR_STEPS_RIGHT) ^ secretNumber) % MODULO;

        secretNumber = ((secretNumber << MULTIPLIER2_STEPS_LEFT) ^ secretNumber) % MODULO;

        return secretNumber;
    }

    private long generate2000thNumber(long seed) {
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
        List<Long> collector = new ArrayList<>(); //LinkedList<>();
        for (int i = 0; i < STEP_COUNT; i++) {
            long nextSeed = nextNumber(prevSeed);
            long price = nextSeed % 10L;
            long diff = price - prevPrice;
            collector.add(diff);
            if (collector.size() == 4) {
                result.putIfAbsent(new ArrayList<>(collector), price);
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
