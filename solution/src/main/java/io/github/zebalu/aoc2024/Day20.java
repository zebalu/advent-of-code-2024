package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day20 extends AbstractDay {
    private final char[][] data;
    private final int width;
    private final int height;
    private final Map<Coord, Integer> fromStart;
    private final Map<Coord, Integer> fromEnd;
    private final int originalCost;
    public Day20() {
        this(IOUtil.readInput(20));
    }
    public Day20(String input) {
        super(input, "Race Condition", 20);
        data = IOUtil.readCharGrid(INPUT);
        height = data.length;
        width = data[0].length;
        Coord start = null;
        Coord end = null;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if('S' == data[y][x]) {
                    start = new Coord(x, y);
                } else if ('E' == data[y][x]) {
                    end = new Coord(x, y);
                }
            }
        }
        fromStart = distance(start);
        fromEnd = distance(end);
        originalCost = fromStart.get(end);
    }

    @Override
    public String part1() {
        int sum = countWithCheats(findCheats(2));
        return Integer.toString(sum);
    }

    @Override
    public String part2() {
        int sum = countWithCheats(findCheats(20));
        return Integer.toString(sum);
    }

    private int countWithCheats(Set<Cheat> cheats) {
        int sum = 0;
        for (var ch : cheats) {
            int cost = fromStart.get(ch.first()) + ch.length() + fromEnd.get(ch.last());
            if (cost <= originalCost - 100) {
                ++sum;
            }
        }
        return sum;
    }

    private Map<Coord, Integer> distance(Coord start) {
        Map<Coord, Integer> priceMap = new HashMap<>();
        priceMap.put(start, 0);
        Queue<Coord> queue = new ArrayDeque<>(List.of(start));
        Set<Coord> visited = new HashSet<>(List.of(start));
        while(!queue.isEmpty()) {
            Coord cur = queue.poll();
            for(Coord n: cur.steps()) {
                if(isValid(n) && visited.add(n)) {
                    priceMap.compute(n, (_,_) -> priceMap.get(cur)+1);
                    queue.add(n);
                }
            }
        }
        return priceMap;
    }

    private Set<Cheat> findCheats(int distance) {
        Set<Cheat> cheats = new HashSet<>();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x, y);
                if(isValid(c)) {
                    for(int y2=y-distance; y2<=y+distance; y2++) {
                        for(int x2=x-distance; x2<=x+distance; x2++) {
                            Coord c2 = new Coord(x2, y2);
                            if(isValid(c2) && c.distance(c2)<=distance) {
                                cheats.add(new Cheat(c, c2));
                            }
                        }
                    }
                }
            }
        }
        return cheats;
    }

    private boolean isValid(Coord coord) {
        return 0<=coord.x && coord.x<width && 0<=coord.y && coord.y<height && data[coord.y][coord.x] != '#';
    }

    private record Coord(int x, int y) {
        List<Coord> steps() {
            return List.of(new Coord(x-1, y), new Coord(x+1, y), new Coord(x, y-1), new Coord(x, y+1));
        }
        int distance(Coord coord) {
            return Math.abs(coord.x-x)+Math.abs(coord.y-y);
        }
    }

    private record Cheat(Coord first, Coord last) {
        int length() {
            return first.distance(last)-1;
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(20);
        Day20 day20 = new Day20(input);
        System.out.println(day20.part1());
        System.out.println(day20.part2());
    }
}
