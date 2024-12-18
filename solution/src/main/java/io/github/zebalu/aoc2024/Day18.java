package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day18 extends AbstractDay {
    private final int width = 70;
    private final int height = 70;
    private final List<Coord> fallingBytes;

    public Day18() {
        this(IOUtil.readInput(18));
    }

    public Day18(String input) {
        super(input, "RAM Run", 18);
        fallingBytes = INPUT.lines().map(Coord::parse).toList();
    }

    @Override
    public String part1() {
        Set<Coord> positions = new HashSet<>(fallingBytes.subList(0, 1024));
        return Integer.toString(requiredSteps(positions).size());
    }

    @Override
    public String part2() {
        Set<Coord> positions = new HashSet<>(fallingBytes.subList(0, 1024));
        int cuttingIndex = Integer.MIN_VALUE;
        Map2D path = requiredSteps(positions);
        for (int i = 1024; i < fallingBytes.size() && cuttingIndex == Integer.MIN_VALUE; ++i) {
            Coord next = fallingBytes.get(i);
            positions.add(fallingBytes.get(i));
            if (path.isMarked(next)) {
                path = requiredSteps(positions);
            }
            if(path.size()==0) {
                cuttingIndex = i;
            }
        }
        return fallingBytes.get(cuttingIndex).toString();
    }

    private Map2D requiredSteps(Set<Coord> positions) {
        Coord start = new Coord(0, 0);
        Coord end = new Coord(width, height);
        Queue<Steps> queue = new ArrayDeque<>();
        Set<Coord> visited = new HashSet<>();
        visited.add(start);
        queue.add(new Steps(start, new Map2D(height+1, width+1)));
        while (!queue.isEmpty()) {
            Steps cur = queue.poll();
            if (cur.position.equals(end)) {
                return cur.history;
            }
            for (var neighbour : cur.position.neighbours()) {
                if (!visited.contains(neighbour) && isValid(neighbour) && !positions.contains(neighbour)) {
                    visited.add(neighbour);
                    Map2D nh = cur.history.copy();
                    nh.mark(neighbour);
                    queue.add(new Steps(neighbour, nh));
                }
            }
        }
        return new Map2D(height+1, width+1);
    }

    boolean isValid(Coord coord) {
        return 0 <= coord.x && coord.x <= width && 0 <= coord.y && coord.y <= height;
    }

    private record Coord(int x, int y) {
        static Coord parse(String line) {
            String[] parts = line.split(",");
            return new Coord(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        List<Coord> neighbours() {
            return List.of(new Coord(x - 1, y), new Coord(x + 1, y), new Coord(x, y - 1), new Coord(x, y + 1));
        }

        @Override
        public String toString() {
            return x + "," + y;
        }

    }

    private record Steps(Coord position, Map2D history) {
    }

    private static class Map2D {
        BitSet[] map;
        public Map2D(int height, int width) {
            map = new BitSet[height];
            for(int i = 0; i < height; ++i) {
                map[i] = new BitSet(width);
            }
        }

        boolean isMarked(Coord coord) {
            return map[coord.y].get(coord.x);
        }

        void mark(Coord coord) {
            map[coord.y].set(coord.x);
        }

        int size() {
            return Arrays.stream(map).mapToInt(BitSet::cardinality).sum();
        }

        Map2D copy() {
            Map2D copy = new Map2D(map.length, map[0].length());
            for(int i = 0; i < map.length; ++i) {
                copy.map[i] = (BitSet) map[i].clone();
            }
            return copy;
        }

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(18);
        Day18 day18 = new Day18(input);
        System.out.println(day18.part1());
        System.out.println(day18.part2());
    }
}
