package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day06 extends AbstractDay {
    private final int width;
    private final int height;
    private final Set<Coord> obstacles = new HashSet<>();
    private final Coord guardStartPosition;
    public Day06() {
        this(IOUtil.readInput(6));
    }
    public Day06(String input) {
        super(input, "Guard Gallivant", 6);
        var lines = INPUT.lines().toList();
        height = lines.size();
        width = lines.getFirst().length();
        Coord tempStart = new Coord(0, 0);
        for(int y=0; y<height; ++y) {
            String line = lines.get(y);
            for(int x=0; x<width; ++x) {
                char at = line.charAt(x);
                if('#' == at) {
                    obstacles.add(new Coord(x, y));
                } else if('^' == at) {
                    tempStart = new Coord(x, y);
                }
            }
        }
        guardStartPosition = tempStart;
    }

    @Override
    public String part1() {
        return Integer.toString(isLooping(null, new PositionDirection(guardStartPosition, new Coord(0, -1)), false).count);
    }

    private boolean isValid(Coord coord) {
        return 0<=coord.x && coord.x<width && 0<=coord.y && coord.y<height;
    }

    @Override
    public String part2() {
        var stepsLog = isLooping(null, new PositionDirection(guardStartPosition, new Coord(0, -1)), false);
        SequencedMap<Coord, PositionDirection> firstArrives = new LinkedHashMap<>();
        for(PositionDirection pd: stepsLog.log()) {
            firstArrives.putIfAbsent(pd.position, pd);
        }
        long count = firstArrives.keySet().stream()
                .filter(option -> !guardStartPosition.equals(option) && isLooping(option, firstArrives.get(option).prev(), true).looping)
                .count();
        return Long.toString(count);
    }

    private StepsLog isLooping(Coord newObstacle, PositionDirection start, boolean excludeInnerSteps) {
        PositionDirection current = start;
        SequencedSet<PositionDirection> visited = new LinkedHashSet<>();
        while (isValid(current.position) && !visited.contains(current)) {
            Coord nextGuard = current.position.add(current.facing);
            if(obstacles.contains(nextGuard) || nextGuard.equals(newObstacle)) {
                visited.add(current);
                current = new PositionDirection(current.position, current.facing.turnRight());
            } else {
                if(!excludeInnerSteps) {
                    visited.add(current);
                }
                current = new PositionDirection(nextGuard, current.facing);
            }
        }
        return new StepsLog(visited, visited.stream().map(s->s.position).collect(Collectors.toSet()).size(), isValid(current.position));
    }

    private record Coord(int x, int y) {
        public Coord turnRight() {
            return new Coord(-y, x);
        }
        public Coord add(Coord other) {
            return new Coord(other.x + x, other.y + y);
        }
        public Coord minus(Coord other) {
            return new Coord(x-other.x, y-other.y);
        }
    }

    private record PositionDirection(Coord position, Coord facing) {
        PositionDirection prev() {
            return new PositionDirection(position.minus(facing), facing);
        }
    }
    private record StepsLog(SequencedSet<PositionDirection> log, int count, boolean looping) {}

    public static void main(String[] args) {
        String input = IOUtil.readInput(6);
        Day06 day06 = new Day06(input);
        System.out.println(day06.part1());
        System.out.println(day06.part2());
    }
}
