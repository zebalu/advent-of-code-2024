package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day11 extends AbstractDay {

    private final List<Long> stones;

    public Day11() {
        this(IOUtil.readInput(11));
    }

    public Day11(String input) {
        super(input, "Plutonian Pebbles", 11);
        stones = Collections.unmodifiableList(readStones());
    }

    @Override
    public String part1() {
        return Long.toString(countStonesInSteps(25));
    }

    @Override
    public String part2() {
        return Long.toString(countStonesInSteps(75));
    }

    private List<Long> readStones() {
        List<Long> stoneCollector = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(INPUT);
        while (matcher.find()) {
            stoneCollector.add(Long.parseLong(matcher.group(1)));
        }
        return stoneCollector;
    }

    private long countStonesInSteps(int stepCount) {
        Map<StoneStep, Long> stonesMemory = new HashMap<>();
        long count = 0L;
        for (long stone : stones) {
            count += countStones(new StoneStep(stone, stepCount), stonesMemory);
        }
        return count;
    }

    private long countStones(StoneStep stoneStep, Map<StoneStep, Long> stonesMemory) {
        if (stonesMemory.containsKey(stoneStep)) {
            return stonesMemory.get(stoneStep);
        }
        long stone = stoneStep.stone;
        if (stone == 0L) {
            if (stoneStep.step == 1) {
                stonesMemory.put(stoneStep, 1L);
            } else {
                StoneStep nextStep = new StoneStep(1L, stoneStep.step - 1);
                stonesMemory.put(stoneStep, countStones(nextStep, stonesMemory));
            }
        } else if (isEvenDigitCount(stone)) {
            if (stoneStep.step == 1) {
                stonesMemory.put(stoneStep, 2L);
            } else {
                long sum = 0L;
                for (var splitted : splitStone(stoneStep.stone)) {
                    sum += countStones(new StoneStep(splitted, stoneStep.step - 1), stonesMemory);
                }
                stonesMemory.put(stoneStep, sum);
            }
        } else {
            if (stoneStep.step == 1) {
                stonesMemory.put(stoneStep, 1L);
            } else {
                stonesMemory.put(stoneStep, countStones(new StoneStep(stoneStep.stone * 2024, stoneStep.step - 1), stonesMemory));
            }
        }
        return stonesMemory.get(stoneStep);
    }


    private boolean isEvenDigitCount(long stone) {
        String printed = Long.toString(stone);
        return printed.length() % 2 == 0;
    }

    private List<Long> splitStone(long stone) {
        String printed = Long.toString(stone);
        return List.of(Long.parseLong(printed.substring(0, printed.length() / 2)), Long.parseLong(printed.substring(printed.length() / 2)));
    }

    private record StoneStep(long stone, int step) {

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(11);
        Day11 day11 = new Day11(input);
        System.out.println(day11.part1());
        System.out.println(day11.part2());
    }
}
