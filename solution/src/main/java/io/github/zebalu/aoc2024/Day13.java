package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day13 extends AbstractDay {
    private final List<Machine> machines;
    public Day13() {
        this(IOUtil.readInput(13));
    }

    public Day13(String input) {
        super(input, "Claw Contraption", 13);
        List<List<String>> configs = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line: INPUT.lines().toList()) {
            if(line.isBlank()) {
                configs.add(current);
                current = new ArrayList<>();
            } else {
                current.add(line);
            }
        }
        if(!current.isEmpty()) {
            configs.add(current);
        }
        Pattern number = Pattern.compile(".*X.(\\d+), Y.(\\d+)");
        machines = configs.stream().map(cfs -> {
            Matcher numberMatcher = number.matcher(cfs.get(0));
            numberMatcher.find();
            int x = Integer.parseInt(numberMatcher.group(1));
            int y = Integer.parseInt(numberMatcher.group(2));
            Coord aButton = new Coord(x, y);
            numberMatcher = number.matcher(cfs.get(1));
            numberMatcher.find();
            x = Integer.parseInt(numberMatcher.group(1));
            y = Integer.parseInt(numberMatcher.group(2));
            Coord bButton = new Coord(x, y);
            numberMatcher = number.matcher(cfs.get(2));
            numberMatcher.find();
            x = Integer.parseInt(numberMatcher.group(1));
            y = Integer.parseInt(numberMatcher.group(2));
            Coord prize = new Coord(x, y);
            return new Machine(aButton, bButton, prize);
        }).toList();
    }

    @Override
    public String part1() {
        long sum = 0L;
        for(Machine machine: machines) {
            sum += priceOf(machine);
        }
        return ""+sum;
    }

    @Override
    public String part2() {
        long sum = 0L;
        for(Machine machine: expandPrizes()) {
            sum += mathPriceOf(machine);
        }
        return ""+sum;
    }

    private long priceOf(Machine machine) {
        Set<Pushes> processed = new HashSet<>();
        PriorityQueue<Pushes> pushes = new PriorityQueue<>(Pushes.COMPARATOR);
        pushes.add(new Pushes(0,0 ,new Coord(0,0)));
        while(!pushes.isEmpty()) {
            Pushes push = pushes.poll();
            List<Pushes> nexts=List.of(
                    new Pushes(push.aPush+1, push.bPush, push.location.add(machine.aMove)),
                    new Pushes(push.aPush, push.bPush+1, push.location.add(machine.bMove))
            );
            for(Pushes next: nexts) {
                if(next.location.equals(machine.prize)) {
                    return next.price();
                }
                if(processed.add(next) && next.location.isBefore(machine.prize)) {
                    pushes.add(next);
                }
            }
        }
        return 0L;
    }

    private long mathPriceOf(Machine machine) {
        /// ax1+bx2 = px --> a = (px-bx2)/x1
        /// ay1+by2 = py --> ((px-bx2)/x1)y1+by2 = py
        ///
        /// (pxy1-bx2y1)/x1+by2 = py
        /// pxy1 - bx2y1 + by2x1 = pyx1
        /// b(y2x1-x2y1) = pyx1 - pxy1
        ///
        /// b = (pyx1-pxy1) / (y2x1-x2y1)
        /// a = (px - ((pyx1-pxy1) / (y2x1-x2y1))*x2)/x1
        long b = (machine.prize.y*machine.aMove.x - machine.prize.x*machine.aMove.y) / (machine.bMove.y*machine.aMove.x-machine.bMove.x*machine.aMove.y);
        long a = (machine.prize.x-b*machine.bMove.x)/machine.aMove.x;
        Coord location = new Coord(a*machine.aMove.x+b*machine.bMove.x, a*machine.aMove.y+b*machine.bMove.y);
        if(location.equals(machine.prize)) {
            return a*3+b;
        }
        return 0L;
    }

    private List<Machine> expandPrizes() {
        return machines.stream().map(o->new Machine(o.aMove, o.bMove, new Coord(10_000_000_000_000L+o.prize.x, 10_000_000_000_000L+o.prize.y))).collect(Collectors.toList());
    }

    private record Coord(long x, long y) {
        boolean isBefore(Coord other) {
            return x<other.x && y<other.y;
        }
        Coord add(Coord other) {
            return new Coord(x+other.x, y+other.y);
        }
    }

    private record Machine(Coord aMove, Coord bMove, Coord prize) {

    }

    private record Pushes(int aPush, int bPush, Coord location) {
        static final Comparator<Pushes> COMPARATOR = Comparator.comparingInt(Pushes::price);

        int price() {
            return 3*aPush+bPush;
        }

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(13);
        Day13 day13 = new Day13(input);
        System.out.println(day13.part1());
        System.out.println(day13.part2());
    }
}
