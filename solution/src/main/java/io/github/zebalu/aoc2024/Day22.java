package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Stream;

public class Day22 extends AbstractDay {
    private static final long MODULO = 16_777_216L;
    private static final long BIT_MASK = MODULO - 1L;
    private static final int MULTIPLIER1_STEPS_LEFT = 6; /// multiply by 64L;
    private static final int MULTIPLIER2_STEPS_LEFT = 11; /// multiply by 2048L;
    private static final int DIVISOR_STEPS_RIGHT = 5; /// divide by 32
    private static final long STEP_COUNT = 2_000L;

    /// genius speedup trick from @p-kovacs
    /// check original at: [solution of p-kovacs](https://github.com/p-kovacs/advent-of-code-2024/blob/master/src/main/java/com/github/pkovacs/aoc/y2024/Day22.java)
    private static final int RADIX = 19;
    private static final int RADIX3 = RADIX * RADIX * RADIX;

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
        Map<Long, Long> summingMap = new HashMap<>();
        Arrays.stream(secretNumbers).forEach(sn->{
            long[] prices = Stream.iterate(sn, this::nextNumber).mapToLong(Long::longValue).map(l->l%10L).limit(2001).toArray();
            Set<Long> seenDiffs = new HashSet<>();
            long runningDiff = 0L;
            for(int i=1; i<prices.length; i++) {
                runningDiff = (runningDiff % RADIX3) * RADIX + (prices[i] - prices[i-1] + 9);
                if(i>=4 && seenDiffs.add(runningDiff)) {
                    summingMap.merge(runningDiff, prices[i], Long::sum);
                }
            }
        });
        long max = summingMap.values().stream().mapToLong(Long::longValue).max().orElseThrow();
        return Long.toString(max);
    }

    private long nextNumber(long seed) {
        long secretNumber = seed;

        secretNumber = ((secretNumber << MULTIPLIER1_STEPS_LEFT) ^ secretNumber) & BIT_MASK;

        secretNumber = ((secretNumber >> DIVISOR_STEPS_RIGHT) ^ secretNumber) & BIT_MASK;

        secretNumber = ((secretNumber << MULTIPLIER2_STEPS_LEFT) ^ secretNumber) & BIT_MASK;

        return secretNumber;
    }

    private long generate2000thNumber(long seed) {
        long result = seed;
        for (int i = 0; i < STEP_COUNT; i++) {
            result = nextNumber(result);
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
