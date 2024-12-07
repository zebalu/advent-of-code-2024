package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Day07 extends AbstractDay {
    private final List<Calibration> calibrations;

    public Day07() {
        this(IOUtil.readInput(7));
    }

    public Day07(String input) {
        super(input, "Bridge Repair", 7);
        var lines = INPUT.lines().toList();
        calibrations = lines.stream().map(Calibration::from).toList();
    }

    @Override
    public String part1() {
        Validator validator = new Validator(false);
        long res = calibrations.stream().filter(validator::isValid).mapToLong(Calibration::testValue).sum();
        return Long.toString(res);
    }

    @Override
    public String part2() {
        Validator validator = new Validator(true);
        long res = calibrations.stream().filter(validator::isValid).mapToLong(Calibration::testValue).sum();
        return Long.toString(res);
    }

    private record Calibration(long testValue, List<Long> parts) {
        static Calibration from(String line) {
            String[] parting1 = line.split(": ");
            long testValue = Long.parseLong(parting1[0]);
            List<Long> parts = new ArrayList<>();
            String[] parting2 = parting1[1].split(" ");
            for (String s : parting2) {
                parts.add(Long.parseLong(s));
            }
            return new Calibration(testValue, parts);
        }
    }

    private static class Validator {
        private final boolean canConcatenate;

        public Validator(boolean canConcatenate) {
            this.canConcatenate = canConcatenate;
        }

        public boolean isValid(Calibration calibration) {
            if (calibration.parts.isEmpty()) {
                return calibration.testValue == 0;
            }
            Queue<Long> toConsume = new ArrayDeque<>(calibration.parts.reversed());
            List<Long> current = new ArrayList<>();
            current.add(calibration.testValue);
            while (!toConsume.isEmpty() && !current.isEmpty()) {
                long num = toConsume.poll();
                List<Long> next = new ArrayList<>();
                for (var v : current) {
                    if (v == num && toConsume.isEmpty()) {
                        return true;
                    }
                    if (v > num) {
                        long changed = v - num;
                        next.add(changed);
                    }
                    if (v % num == 0) {
                        long changed = v / num;
                        next.add(changed);
                    }
                    if (canConcatenate) {
                        long retracted = retract(v, num);
                        if (retract(v, num) != v) {
                            next.add(retracted);
                        }
                    }
                }
                current = next;
            }
            return false;
        }

        private long retract(long left, long right) {
            if (left == right) {
                return 0;
            }
            String leftStr = Long.toString(left);
            String rightStr = Long.toString(right);
            if (leftStr.endsWith(rightStr)) {
                return Long.parseLong(leftStr.substring(0, leftStr.length() - rightStr.length()));
            }
            return left;
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(7);
        Day07 day07 = new Day07(input);
        System.out.println(day07.part1());
        System.out.println(day07.part2());
    }
}
