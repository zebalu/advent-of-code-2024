package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day18 extends AbstractDay {
    int width = 70;
    int height = 70;
    List<Coord> fallingBytes;
    public Day18() {
        this(IOUtil.readInput(18));
    }
    public Day18(String input) {
        super(input, "RAM Run", 18);
        fallingBytes = INPUT.lines().map(Coord::parse).toList();
    }

    @Override
    public String part1() {
        Set<Coord> positions = new HashSet<>();
        Iterator<Coord> iterator = fallingBytes.iterator();
        while (positions.size() < 1024 && iterator.hasNext()) {
            positions.add(iterator.next());
        }
        Coord start = new Coord(0, 0);
        Coord end = new Coord(width, height);
        Queue<StepCount> queue = new ArrayDeque<>();
        Set<Coord> visited = new HashSet<>();
        visited.add(start);
        queue.add(new StepCount(start, 0, new ArrayList<>(List.of(start))));
        int min = Integer.MAX_VALUE;
        List<Coord> path = new ArrayList<>();
        while (!queue.isEmpty() &&  min == Integer.MAX_VALUE) {
            StepCount cur = queue.poll();
            if(cur.position.equals(end)) {
                min = cur.count;
                path = cur.history;
            }
            for(var neighbour: cur.position.neighbours()) {
                if(!visited.contains(neighbour) && isValid(neighbour) && !positions.contains(neighbour)) {
                    visited.add(neighbour);
                    List<Coord> h = new ArrayList<>(cur.history);
                    h.add(neighbour);
                    queue.add(new StepCount(neighbour, cur.count+1, h));
                }
            }
        }
        for(int y = 0; y <= height; y++) {
            for(int x = 0; x <= width; x++) {
                Coord c = new Coord(x, y);
                if(positions.contains(c)) {
                    System.out.print('#');
                } else if(path.contains(c)) {
                    System.out.print('0');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println(path.size());
        return ""+min;
    }

    @Override
    public String part2() {
        Set<Coord> positions = new HashSet<>();
        int cuttingIndex = Integer.MIN_VALUE;
        for (int i=0; i<fallingBytes.size() && cuttingIndex == Integer.MIN_VALUE; ++i) {
            positions.add(fallingBytes.get(i));
            if(!isTherePath(positions)) {
                cuttingIndex = i;
            }
        }
        return fallingBytes.get(cuttingIndex).toString();
    }

    private boolean isTherePath(Set<Coord> positions) {
        Coord start = new Coord(0, 0);
        Coord end = new Coord(width, height);
        Queue<StepCount> queue = new ArrayDeque<>();
        Set<Coord> visited = new HashSet<>();
        visited.add(start);
        queue.add(new StepCount(start, 0, new ArrayList<>(List.of(start))));
        int min = Integer.MAX_VALUE;
        List<Coord> path = new ArrayList<>();
        while (!queue.isEmpty() &&  min == Integer.MAX_VALUE) {
            StepCount cur = queue.poll();
            if(cur.position.equals(end)) {
                return true;
            }
            for(var neighbour: cur.position.neighbours()) {
                if(!visited.contains(neighbour) && isValid(neighbour) && !positions.contains(neighbour)) {
                    visited.add(neighbour);
                    List<Coord> h = new ArrayList<>(cur.history);
                    h.add(neighbour);
                    queue.add(new StepCount(neighbour, cur.count+1, h));
                }
            }
        }
        return false;
    }

    boolean isValid(Coord coord) {
        return 0<=coord.x && coord.x<=width && 0<=coord.y && coord.y<=height;
    }

    private record Coord(int x, int y) {
        static Coord parse(String line) {
            String[] parts = line.split(",");
            return new Coord(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        List<Coord> neighbours() {
            return List.of(new Coord(x-1,  y), new Coord(x+1,  y), new Coord(x,  y-1), new Coord(x,  y+1));
        }

        @Override
        public String toString() {
            return x+","+y;
        }
    }

    private record StepCount(Coord position, int count, List<Coord> history) {}

    public static void main(String[] args) {
        String input = IOUtil.readInput(18);
        Day18 day18 = new Day18(input);//EXAMPLE);//input);
        day18.width=70;
        day18.height=70;
        System.out.println(day18.part1());
        System.out.println(day18.part2());
    }

    private static final String EXAMPLE= """
            5,4
            4,2
            4,5
            3,0
            2,1
            6,3
            2,4
            1,5
            0,6
            3,3
            2,6
            5,1
            1,2
            5,5
            2,5
            6,5
            1,4
            0,4
            6,4
            1,1
            6,1
            1,0
            0,5
            1,6
            2,0""";
}
