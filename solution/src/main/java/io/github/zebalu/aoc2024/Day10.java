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
        var lines = INPUT.lines().toList();
        height = lines.size();
        width= lines.getFirst().length();
        data = new int[height][width];
        for(int y = 0; y < height; y++) {
            String line = lines.get(y);
            data[y] = new int[width];
            for(int x = 0; x < width; x++) {
                int v = Integer.parseInt(line.substring(x, x + 1));
                data[y][x] = v;
            }
        }
    }

    @Override
    public String part1() {
        int sum = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int v = data[y][x];
                if(v==0) {
                    int score = calcScoreOf(x,y);
                    sum+=score;
                }
            }
        }
        return ""+sum;
    }

    private int calcScoreOf(int x, int y) {
        Set<Coord> visited = new HashSet<>();
        Set<Coord> reached9s = new HashSet<>();
        Queue<Coord> toProcess = new LinkedList<>();
        toProcess.add(new Coord(x, y));
        while(!toProcess.isEmpty()) {
            Coord c = toProcess.poll();
            int currentHeight = heightOf(c);
            if(currentHeight==9) {
                reached9s.add(c);
            } else {
                c.surrounding().stream().filter(this::isValid).filter(n ->
                        heightOf(n) == currentHeight + 1
                ).filter(visited::add).forEach(toProcess::add);
            }
        }
        return reached9s.size();
    }

    boolean isValid(Coord c) {
        return 0<=c.x() && 0<=c.y() && c.x()<width && c.y()<height;
    }

    int heightOf(Coord c) {
        return data[c.y()][c.x()];
    }

    @Override
    public String part2() {
        int sum = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int v = data[y][x];
                if(v==0) {
                    int score = calcTrailScoreOf(x,y);
                    sum+=score;
                }
            }
        }
        return Integer.toString(sum);
    }

    private int calcTrailScoreOf(int x, int y) {
        Set<SequencedSet<Coord>> visited = new HashSet<>();
        Set<SequencedSet<Coord>> reached9s = new HashSet<>();
        Queue<SequencedSet<Coord>> toProcess = new LinkedList<>();
        SequencedSet<Coord> start = new LinkedHashSet<>();
        start.add(new Coord(x, y));
        toProcess.add(start);
        while(!toProcess.isEmpty()) {
            SequencedSet<Coord> c = toProcess.poll();
            int currentHeight = heightOf(c.getLast());
            if(currentHeight==9) {
                reached9s.add(c);
            } else {
                c.getLast().surrounding().stream().filter(this::isValid).filter(n ->
                        heightOf(n) == currentHeight + 1
                ).map(n->{
                    SequencedSet<Coord> nS = new LinkedHashSet<>(c);
                    nS.add(n);
                    return nS;
                }).filter(visited::add).forEach(toProcess::add);
            }
        }
        return reached9s.size();
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
