package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day10 extends AbstractDay {
    private final int height;
    private final int width;
    private final int[][] data;
    public Day10() {
        this(IOUtil.readInput(10));
    }
    public Day10(String input) {
        super(input, "Hoof It", 10);
        data = IOUtil.readIntGrid(INPUT);
        height = data.length;
        width = data[0].length;
    }

    @Override
    public String part1() {
        int sum = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(data[y][x]==0) {
                    int score = calcScoreOf(x,y);
                    sum+=score;
                }
            }
        }
        return ""+sum;
    }

    private int calcScoreOf(int x, int y) {
        Coord start = new Coord(x,y);
        Set<Coord> visited = new HashSet<>(List.of(start));
        Set<Coord> reached9s = new HashSet<>();
        Queue<Coord> toProcess = new ArrayDeque<>(List.of(start));
        while(!toProcess.isEmpty()) {
            Coord c = toProcess.poll();
            int currentHeight = heightOf(c);
            if(currentHeight==9) {
                reached9s.add(c);
            } else {
                c.surrounding().stream()
                        .filter(this::isValid)
                        .filter(n -> heightOf(n) == currentHeight + 1)
                        .filter(visited::add)
                        .forEach(toProcess::add);
            }
        }
        return reached9s.size();
    }

    private boolean isValid(Coord c) {
        return 0<=c.x() && 0<=c.y() && c.x()<width && c.y()<height;
    }

    private int heightOf(Coord c) {
        return data[c.y()][c.x()];
    }

    @Override
    public String part2() {
        int sum = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(data[y][x]==0) {
                    int score = calcTrailScoreOf(x,y);
                    sum+=score;
                }
            }
        }
        return Integer.toString(sum);
    }

    private int calcTrailScoreOf(int x, int y) {
        SequencedSet<Coord> start = new LinkedHashSet<>(List.of(new Coord(x,y)));
        Set<SequencedSet<Coord>> visited = new HashSet<>(List.of(start));
        Set<SequencedSet<Coord>> reached9s = new HashSet<>();
        Queue<SequencedSet<Coord>> toProcess = new LinkedList<>(List.of(start));
        while(!toProcess.isEmpty()) {
            SequencedSet<Coord> c = toProcess.poll();
            int currentHeight = heightOf(c.getLast());
            if(currentHeight==9) {
                reached9s.add(c);
            } else {
                c.getLast().surrounding().stream()
                        .filter(this::isValid)
                        .filter(n -> heightOf(n) == currentHeight + 1)
                        .map(n->extend(c, n))
                        .filter(visited::add)
                        .forEach(toProcess::add);
            }
        }
        return reached9s.size();
    }

    private SequencedSet<Coord> extend(SequencedSet<Coord> base, Coord newCord) {
        SequencedSet<Coord> extended = new LinkedHashSet<>(base);
        extended.add(newCord);
        return extended;
    }

    private record Coord(int x, int y) {
        List<Coord> surrounding() {
            return List.of(new Coord(x, y+1), new Coord(x+1, y), new Coord(x, y-1), new Coord(x-1, y));
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(10);
        Day10 day10 = new Day10(input);
        System.out.println(day10.part1());
        System.out.println(day10.part2());
    }
}
