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
        Map2D positions = new Map2D(height+1, width+1);
        fallingBytes.stream().limit(1024).forEach(positions::mark);
        return Integer.toString(requiredSteps(positions).size());
    }

    @Override
    public String part2() {
        int min = 0;
        int max = fallingBytes.size();
        int cuttingIndex = Integer.MIN_VALUE;
        while(cuttingIndex == Integer.MIN_VALUE) {
            int mid = (min + max)/2;
            Map2D positions = new Map2D(height+1, width+1);
            fallingBytes.stream().limit(mid).forEach(positions::mark);
            Map2D path = requiredSteps(positions);
            if(path.size()==0) {
                max = mid-1;
            } else {
                min = mid;
            }
            if(max == min) {
                cuttingIndex = min;
            }
        }
        return fallingBytes.get(cuttingIndex).toString();
    }

    private Map2D requiredSteps(Map2D positions) {
        Coord start = new Coord(0, 0);
        Coord end = new Coord(width, height);
        Queue<Steps> queue = new ArrayDeque<>();
        Map2D visited = new Map2D(height+1, width+1);
        visited.mark(start);
        queue.add(new Steps(start, new Map2D(height+1, width+1)));
        while (!queue.isEmpty()) {
            Steps cur = queue.poll();
            if (cur.position.equals(end)) {
                return cur.history;
            }
            for (var neighbour : cur.position.neighbours()) {
                if (!visited.isMarked(neighbour) && isValid(neighbour) && !positions.isMarked(neighbour)) {
                    visited.mark(neighbour);
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

        private Map2D(BitSet[] map) {
            this.map = map;
        }

        boolean isMarked(Coord coord) {
            if(coord.y<0||coord.y>=map.length || coord.x<0||coord.x>=map[0].size()) {
                return false;
            }
            return map[coord.y].get(coord.x);
        }

        void mark(Coord coord) {
            map[coord.y].set(coord.x);
        }

        int size() {
            return Arrays.stream(map).mapToInt(BitSet::cardinality).sum();
        }

        Map2D copy() {
            BitSet[] copy = new BitSet[map.length];
            for(int i = 0; i < map.length; ++i) {
                copy[i] = (BitSet) map[i].clone();
            }
            return new Map2D(copy);
        }

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(18);
        Day18 day18 = new Day18(input);
        System.out.println(day18.part1());
        System.out.println(day18.part2());
    }
}
