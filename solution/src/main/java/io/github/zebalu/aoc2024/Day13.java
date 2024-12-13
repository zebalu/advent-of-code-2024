package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day13 extends AbstractDay {
    private static final long EXPANSION = 10_000_000_000_000L;
    private final List<Machine> machines;
    public Day13() {
        this(IOUtil.readInput(13));
    }

    public Day13(String input) {
        super(input, "Claw Contraption", 13);
        List<List<String>> configs = IOUtil.groupByEmptyLines(INPUT);
        Pattern coordPattern = Pattern.compile(".*X.(\\d+), Y.(\\d+)");
        machines = configs.stream().map(cfs -> {
            List<Coord> coords = cfs.stream().map(line->{
                Matcher coordMatcher = coordPattern.matcher(line);
                if(!coordMatcher.find()) {
                    throw new IllegalArgumentException("Invalid line format: " + line);
                }
                long x = Long.parseLong(coordMatcher.group(1));
                long y = Long.parseLong(coordMatcher.group(2));
                return new Coord(x, y);
            }).toList();
            return new Machine(coords.get(0), coords.get(1), coords.get(2));
        }).toList();
    }

    @Override
    public String part1() {
        long sum = machines.stream().mapToLong(this::mathPriceOf).sum();
        return Long.toString(sum);
    }

    @Override
    public String part2() {
        long sum = expandPrizes().stream().mapToLong(this::mathPriceOf).sum();
        return Long.toString(sum);
    }

    /// Calculates price by equation.
    ///
    /// where:
    /// * __a__ is the number of times _a_ was pushed
    /// * __b__ is the number of times _b_ was pushed
    /// * __x1__ is the move on x-axis by _a_ push
    /// * __y1__ is the move on y-axis by _a_ push
    /// * __x2__ is the move on x-axis by _b_ push
    /// * __y2__ is the move on y-axis by _b_ push
    /// * __px__ is the prize's x coordinate
    /// * __py__ is the prize's y coordinate
    ///
    /// ```
    /// ax1+bx2 = px --> a = (px-bx2)/x1
    /// ay1+by2 = py --> ((px-bx2)/x1)y1+by2 = py
    ///
    /// (pxy1-bx2y1)/x1+by2 = py
    /// pxy1 - bx2y1 + by2x1 = pyx1
    /// b(y2x1-x2y1) = pyx1 - pxy1
    ///
    /// b = (pyx1 - pxy1) / (y2x1 - x2y1)
    /// a = (px-((pyx1-pxy1)/(y2x1-x2y1))*x2)/x1
    /// or
    /// a = (px - bx2) / x1
    /// ```
    private long mathPriceOf(Machine machine) {
        long b = (machine.prize.y*machine.aMove.x - machine.prize.x*machine.aMove.y) / (machine.bMove.y*machine.aMove.x-machine.bMove.x*machine.aMove.y);
        long a = (machine.prize.x-b*machine.bMove.x)/machine.aMove.x;
        Coord location = new Coord(a*machine.aMove.x+b*machine.bMove.x, a*machine.aMove.y+b*machine.bMove.y);
        if(location.equals(machine.prize)) {
            return a*3+b;
        }
        return 0L;
    }

    private List<Machine> expandPrizes() {
        return machines.stream().map(o->new Machine(o.aMove, o.bMove, new Coord(EXPANSION+o.prize.x, EXPANSION+o.prize.y))).collect(Collectors.toList());
    }

    private record Coord(long x, long y) { }

    private record Machine(Coord aMove, Coord bMove, Coord prize) { }

    public static void main(String[] args) {
        String input = IOUtil.readInput(13);
        Day13 day13 = new Day13(input);
        System.out.println(day13.part1());
        System.out.println(day13.part2());
    }
}
