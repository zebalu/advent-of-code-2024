package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 extends AbstractDay {
    private static final char[][] KEY_PAD = new char[][]{
            new char[]{'7', '8', '9'},
            new char[]{'4', '5', '6'},
            new char[]{'1', '2', '3'},
            new char[]{'#', '0', 'A'}
    };
    private static final char[][] DIR_PAD = new char[][]{
            new char[]{'#', '^', 'A'},
            new char[]{'<', 'v', '>'}
    };

    private static final Map<Character, Coord> COORD_OF = Map.of(
            '^', new Coord(1, 0),
            'A', new Coord(2, 0),
            '<', new Coord(0, 1),
            'v', new Coord(1, 1),
            '>', new Coord(2, 1)
    );

    private final List<String> toType;
    private final Map<RoadDepth, Long> roadDepthMap = new HashMap<>();

    public Day21() {
        this(IOUtil.readInput(21));
    }

    public Day21(String input) {
        super(input, "Keypad Conundrum", 21);
        toType = INPUT.lines().toList();
    }

    @Override
    public String part1() {
        return Long.toString(solve(2));
    }

    @Override
    public String part2() {
        return Long.toString(solve(25));
    }

    private int codeToNumber(String code) {
        return Integer.parseInt(code.substring(0, code.length() - 1));
    }

    private List<Path> shortestsToGoal(Coord start, char goal, char[][] pad) {
        if (isValidButton(start, pad) && getButton(start, pad) == goal) {
            return List.of(new Path(start, ""));
        }
        Queue<Path> queue = new ArrayDeque<>(List.of(new Path(start, "")));
        List<Path> result = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Path p = queue.poll();
            for (var n : p.end.next()) {
                if (isValidButton(n, pad)) {
                    Path nP = new Path(n, p.steps + p.end.asDir(n));
                    if (getButton(n, pad) == goal) {
                        if (nP.steps.length() < best) {
                            best = nP.steps.length();
                            result.clear();
                            result.add(nP);
                        } else if (nP.steps.length() == best) {
                            result.add(nP);
                        }
                    } else if (nP.steps.length() < best) {
                        queue.add(nP);
                    }
                }
            }
        }
        return result;
    }

    private List<String> turnToDirections(String code, Coord start, String core, char[][] pad) {
        if (code.isEmpty()) {
            return List.of(core);
        }
        char c = code.charAt(0);
        List<String> collect = new ArrayList<>();
        for (var p : shortestsToGoal(start, c, KEY_PAD)) {
            collect.addAll(turnToDirections(code.substring(1), p.end, core + p.steps + "A", pad));
        }
        int minLength = collect.stream().mapToInt(String::length).min().orElseThrow();
        return collect.stream().filter(s -> s.length() == minLength).distinct().collect(Collectors.toList());
    }

    private char getButton(Coord c, char[][] pad) {
        return pad[c.y][c.x];
    }

    private boolean isValidButton(Coord c, char[][] pad) {
        return 0 <= c.y && c.y < pad.length && 0 <= c.x && c.x < pad[0].length && pad[c.y][c.x] != '#';
    }

    private long solve(int depth) {
        long sum = 0;
        for (String code : toType) {
            long myBest = Long.MAX_VALUE;
            List<String> robot1Moves = turnToDirections(code, new Coord(2, 3), "", KEY_PAD);
            for (String robot1Move : robot1Moves) {
                long countP = countPushes(robot1Move, depth);
                myBest = Math.min(myBest, countP);
            }
            sum += myBest * codeToNumber(code);
        }
        return sum;
    }

    private long countPushes(String robot1Move, int depth) {
        if (depth == 0) {
            return robot1Move.length();
        }
        var key = new RoadDepth(robot1Move, depth);
        if (roadDepthMap.containsKey(key)) {
            return roadDepthMap.get(key);
        }
        Coord position = new Coord(2, 0);
        long sum = 0L;
        for (char c : robot1Move.toCharArray()) {
            long min = Long.MAX_VALUE;
            for (String road : roads(c, position)) {
                min = Long.min(min, countPushes(road, depth - 1));
            }
            sum += min;
            position = COORD_OF.get(c);
        }
        roadDepthMap.put(key, sum);
        return sum;
    }

    List<String> roads(char to, Coord at) {
        return shortestsToGoal(at, to, DIR_PAD).stream().map(p -> p.steps() + "A").toList();
    }

    private record Coord(int x, int y) {
        List<Coord> next() {
            return List.of(
                    new Coord(x + 1, y), new Coord(x, y + 1),
                    new Coord(x - 1, y), new Coord(x, y - 1));
        }

        char asDir(Coord other) {
            if (x < other.x()) {
                return '>';
            } else if (x > other.x()) {
                return '<';
            } else if (y < other.y()) {
                return 'v';
            } else if (y > other.y()) {
                return '^';
            } else {
                throw new IllegalArgumentException("Same coord was given: " + this + " to: " + other);
            }
        }
    }

    private record Path(Coord end, String steps) {

    }

    private record RoadDepth(String road, int depth) {

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(21);
        Day21 day = new Day21(input);
        System.out.println(day.part1());
        System.out.println(day.part2());
    }
}
