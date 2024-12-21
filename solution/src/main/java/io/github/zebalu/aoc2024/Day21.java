package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 extends AbstractDay {
    private final char[][] keyPad = new char[][]{
            new char[]{'7', '8', '9'},
            new char[]{'4', '5', '6'},
            new char[]{'1', '2', '3'},
            new char[]{'#', '0', 'A'}
    };
    private final char[][] dirPad = new char[][]{
            new char[]{'#', '^', 'A'},
            new char[]{'<', 'v', '>'}
    };

    private final Map<Character, Coord> coordOf = Map.of(
            '^', new Coord(1, 0),
            'A', new Coord(2, 0),
            '<', new Coord(0, 1),
            'v', new Coord(1, 1),
            '>', new Coord(2, 1)
    );

    private final List<String> toType;
    private final Map<RoadDepth, Long> roadDepthMap = new HashMap<>();

    private Coord keyRobotPosition = new Coord(2, 3);

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

    private List<Path> pushKey(char key) {
        return shortestToKey(keyRobotPosition, key);
    }

    private List<Path> pushDir(char key, Coord start) {
        return shortestToDir(start, key);
    }

    private List<Path> shortestToKey(Coord start, char goal) {
        if (isValidKeyCoord(start) && getKeyChar(start) == goal) {
            return List.of(new Path(start, ""));
        }
        //Set<Coord> visited = new HashSet<>(List.of(start));
        Queue<Path> queue = new ArrayDeque<>(List.of(new Path(start, "")));
        List<Path> result = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Path p = queue.poll();
            for (var n : p.end.next()) {
                if (isValidKeyCoord(n) /*&& visited.add(n)*/) {
                    Path nP = new Path(n, p.steps + p.end.asDir(n));
                    if (getKeyChar(n) == goal) {
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
        //throw new IllegalStateException("There should be a path!");
        return result;
    }

    private List<Path> shortestToDir(Coord start, char goal) {
        if (isValidDirCoord(start) && getDirChar(start) == goal) {
            return List.of(new Path(start, ""));
        }
        //Set<Coord> visited = new HashSet<>(List.of(start));
        Queue<Path> queue = new ArrayDeque<>(List.of(new Path(start, "")));
        List<Path> result = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Path p = queue.poll();
            for (var n : p.end.next()) {
                if (isValidDirCoord(n) /*&& visited.add(n)*/) {
                    Path nP = new Path(n, p.steps + p.end.asDir(n));
                    if (getDirChar(n) == goal) {
                        if (nP.steps.length() < best) {
                            best = nP.steps.length();
                            result.clear();
                            result.add(nP);
                            //return List.of(nP);
                        } else if (nP.steps.length() == best) {
                            result.add(nP);
                        }
                    } else if (nP.steps.length() < best) {
                        queue.add(nP);
                    }
                }
            }
        }
        //System.out.println(start +" "+ goal);
        //throw new IllegalStateException("There should be a path!");
        return result;
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

    private List<String> codeToKeypadString(String code, Coord start, String core) {
        if (code.isEmpty()) {
            return List.of(core);
        }
        keyRobotPosition = start;
        char c = code.charAt(0);
        List<String> collect = new ArrayList<>();
        for (var p : pushKey(c)) {
            collect.addAll(codeToKeypadString(code.substring(1), p.end, core + p.steps + "A"));
        }
        int minLength = collect.stream().mapToInt(String::length).min().orElseThrow();
        return collect.stream().filter(s -> s.length() == minLength).distinct().collect(Collectors.toList());
    }

    private List<String> turnToDirections(String code, Coord start, String core, char[][] pad) {
        if (code.isEmpty()) {
            return List.of(core);
        }
        char c = code.charAt(0);
        List<String> collect = new ArrayList<>();
        for (var p : shortestsToGoal(start, c, keyPad)) {
            collect.addAll(turnToDirections(code.substring(1), p.end, core + p.steps + "A", pad));
        }
        int minLength = collect.stream().mapToInt(String::length).min().orElseThrow();
        return collect.stream().filter(s -> s.length() == minLength).distinct().collect(Collectors.toList());
    }

    private char getButton(Coord c, char[][] pad) {
        return pad[c.y][c.x];
    }

    private boolean isValidButton(Coord c, char[][] pad) {
        return 0 <= c.y && c.y < pad.length && 0 <= c.x && c.x < pad[0].length;
    }

    private char getKeyChar(Coord coord) {
        return keyPad[coord.y][coord.x];
    }

    private char getDirChar(Coord coord) {
        return dirPad[coord.y][coord.x];
    }

    private boolean isValidKeyCoord(Coord coord) {
        return 0 <= coord.y && coord.y < keyPad.length && 0 <= coord.x && coord.x < keyPad[0].length && keyPad[coord.y][coord.x] != '#';
    }

    private boolean isValidDirCoord(Coord coord) {
        return 0 <= coord.y && coord.y < dirPad.length && 0 <= coord.x && coord.x < dirPad[0].length && dirPad[coord.y][coord.x] != '#';
    }

    private long solve(int depth) {
        long sum = 0;
        for (String code : toType) {
            long myBest = Long.MAX_VALUE;
            List<String> robot1Moves = codeToKeypadString(code, new Coord(2, 3), "");
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
            position = coordOf.get(c);
        }
        roadDepthMap.put(key, sum);
        return sum;
    }

    List<String> roads(char to, Coord at) {
        return shortestToDir(at, to).stream().map(p -> p.steps() + "A").toList();
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
