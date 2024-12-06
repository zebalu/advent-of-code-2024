package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.HashSet;
import java.util.Set;
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
        return Integer.toString(isLooping(null).count);
    }

    private boolean isValid(Coord coord) {
        return 0<=coord.x && coord.x<width && 0<=coord.y && coord.y<height;
    }

    @Override
    public String part2() {
        var stepsLog = isLooping(guardStartPosition);
        var validCoords = stepsLog.log.stream().map(s->s.position).collect(Collectors.toSet());
        int count = 0;
        for(Coord option: validCoords) {
            if(!guardStartPosition.equals(option) && isLooping(option).looping) {
                ++count;
            }
        }
        return Integer.toString(count);
    }

    private StepsLog isLooping(Coord newObstacle) {
        PositionDirection current = new PositionDirection(guardStartPosition, new Coord(0, -1));
        Set<PositionDirection> visited = new HashSet<>();
        while (isValid(current.position) && !visited.contains(current)) {
            Coord nextGuard = current.position.add(current.facing);
            if(obstacles.contains(nextGuard) || nextGuard.equals(newObstacle)) {
                current = new PositionDirection(current.position, current.facing.turnRight());
            } else {
                visited.add(current);
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
    }

    private record PositionDirection(Coord position, Coord facing) {}
    private record StepsLog(Set<PositionDirection> log, int count, boolean looping) {}

    public static void main(String[] args) {
        String input = IOUtil.readInput(6);
        Day06 day06 = new Day06(input);
        System.out.println(day06.part1());
        System.out.println(day06.part2());
    }
}
