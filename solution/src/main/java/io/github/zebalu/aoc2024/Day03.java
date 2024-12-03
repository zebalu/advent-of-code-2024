package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends AbstractDay {
    private final Pattern SIMPLE_MUL_PATTERN = Pattern.compile("(mul\\(\\d{1,3},\\d{1,3}\\))");
    private final Pattern COMPLEX_MUL_PATTERN = Pattern.compile("(do\\(\\)|mul\\(\\d{1,3},\\d{1,3}\\)|don't\\(\\))");

    public Day03() {
        this(IOUtil.readInput(3));
    }

    public Day03(String input) {
        super(input, "Mull It Over", 3);
    }

    @Override
    public String part1() {
        return Long.toString(collectByPattern(SIMPLE_MUL_PATTERN).stream().mapToLong(Pair::mul).sum());
    }

    @Override
    public String part2() {
        return Long.toString(collectByPattern(COMPLEX_MUL_PATTERN).stream().mapToLong(Pair::mul).sum());
    }

    private List<Pair> collectByPattern(Pattern pattern) {
        Matcher matcher = pattern.matcher(INPUT);
        boolean doAdd = true;
        List<Pair> pairs = new ArrayList<>();
        while (matcher.find()) {
            String grp = matcher.group();
            if (grp.equals("do()")) {
                doAdd = true;
            } else if (grp.equals("don't()")) {
                doAdd = false;
            } else if (doAdd) {
                pairs.add(Pair.from(grp));
            }
        }
        return pairs;
    }

    private record Pair(int a, int b) {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");

        long mul() {
            return a * b;
        }

        static Pair from(String mulString) {
            Matcher numMatcher = NUMBER_PATTERN.matcher(mulString);
            if (numMatcher.find()) {
                return new Pair(Integer.parseInt(numMatcher.group(1)), Integer.parseInt(numMatcher.group(2)));
            }
            throw new IllegalArgumentException("Invalid mul string: " + mulString);
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(3);
        Day03 day03 = new Day03(input);
        System.out.println(day03.part1());
        System.out.println(day03.part2());
    }
}
