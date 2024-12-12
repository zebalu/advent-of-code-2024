package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day12 extends AbstractDay{
    private final int height;
    private final int width;
    private final char[][] data;

    public Day12() {
        this(IOUtil.readInput(12));
    }
    Day12(String input) {
        super(input, "Garden Groups", 12);
        data = IOUtil.readCharGrid(INPUT);
        height = data.length;
        width = data[0].length;
    }

    @Override
    public String part1() {
        long sum =0L;
        Set<Coord> processed = new HashSet<>();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Coord current = new Coord(x,y);
                if(!processed.contains(current)) {
                    Set<Coord> plot = findPlot(current);
                    processed.addAll(plot);
                    long area = areaOf(plot);
                    long perimiter = perimiterOf(plot);
                    sum += area*perimiter;
                }
            }
        }
        return ""+sum;
    }

    private long perimiterOf(Set<Coord> plot) {
        long count = 0;
        for(Coord current : plot) {
            for(Coord neighbour: current.neighbours()) {
                if(!plot.contains(neighbour)) {
                    ++count;
                }
            }
        }
        return count;
    }

    private long areaOf(Set<Coord> plot) {
        return plot.size();
    }

    private Set<Coord> findPlot(Coord start) {
        char type = getType(start);
        Set<Coord> plot = new HashSet<>();
        Set<Coord> seen = new HashSet<>();
        Queue<Coord> queue = new ArrayDeque<>();
        plot.add(start);
        seen.add(start);
        queue.add(start);
        while(!queue.isEmpty()) {
            Coord current = queue.poll();
            current.neighbours().stream()
                    .filter(seen::add)
                    .filter(this::isValid)
                    .filter(c->getType(c)==type)
                    .forEach(neighbour -> {
                        plot.add(neighbour);
                        queue.add(neighbour);
                    });
        }
        return plot;
    }

    private char getType(Coord coord) {
        return data[coord.y][coord.x];
    }

    private boolean isValid(Coord coord) {
        return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
    }

    @Override
    public String part2() {
        long sum =0L;
        Set<Coord> processed = new HashSet<>();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Coord current = new Coord(x,y);
                if(!processed.contains(current)) {
                    Set<Coord> plot = findPlot(current);
                    processed.addAll(plot);
                    long area = areaOf(plot);
                    long sides = sidesOf(plot);
                    sum += area*sides;
                }
            }
        }
        return ""+sum;
    }

    private long sidesOf(Set<Coord> plot) {
        Set<Coord> outerSpots = new HashSet<>();
        Set<Set<Border>> edges = new HashSet<>();
        for(Coord current : plot) {
            List<Coord> neighbours = current.neighbours();;
            Iterator<Coord> ni = neighbours.iterator();
            boolean foundMissing = false;
            while(ni.hasNext() && !foundMissing) {
                Coord next = ni.next();
                foundMissing = !plot.contains(next);
            }
            if(!foundMissing) {
                outerSpots.add(current);
            }
        }
        for (Coord spot : plot) {
            edges.addAll(verticalEdgesOf(spot, plot));
            edges.addAll(horizontalEdgesOf(spot, plot));
        }
        return edges.size();
    }

    private Set<Set<Border>> horizontalEdgesOf(Coord spot, Set<Coord> plot) {
        Set<Set<Border>> edges = new HashSet<>();
        Coord current = spot.above();
        Set<Border> edge = new HashSet<>();
        if(!plot.contains(current)) {
            edge.add(new Border(spot, current));
            Coord walk = current.left();
            while(plot.contains(walk.below())&&!plot.contains(walk)) {
                edge.add(new Border(walk.below(), walk));
                walk = walk.left();
            }
            walk = current.right();
            while(plot.contains(walk.below())&&!plot.contains(walk)) {
                edge.add(new Border(walk.below(), walk));
                walk = walk.right();
            }
            edges.add(edge);
        }
        current = spot.below();
        edge = new HashSet<>();
        if(!plot.contains(current)) {
            edge.add(new Border(spot, current));
            Coord walk = current.left();
            while(plot.contains(walk.above())&&!plot.contains(walk)) {
                edge.add(new Border(walk.above(), walk));
                walk = walk.left();
            }
            walk = current.right();
            while(plot.contains(walk.above())&&!plot.contains(walk)) {
                edge.add(new Border(walk.above(), walk));
                walk = walk.right();
            }
            edges.add(edge);
        }
        return edges;
    }

    private Set<Set<Border>> verticalEdgesOf(Coord spot, Set<Coord> plot) {
        Set<Set<Border>> edges = new HashSet<>();
        Coord current = spot.left();
        Set<Border> edge = new HashSet<>();
        if(!plot.contains(current)) {
            edge.add(new Border(spot, current));
            Coord walk = current.below();
            while(plot.contains(walk.right())&&!plot.contains(walk)) {
                edge.add(new Border(walk.right(), walk));
                walk = walk.below();
            }
            walk = current.above();
            while(plot.contains(walk.right())&&!plot.contains(walk)) {
                edge.add(new Border(walk.right(), walk));
                walk = walk.above();
            }
            edges.add(edge);
        }
        current = spot.right();
        edge = new HashSet<>();
        if(!plot.contains(current)) {
            edge.add(new Border(spot, current));
            Coord walk = current.below();
            while(plot.contains(walk.left())&&!plot.contains(walk)) {
                edge.add(new Border(walk.left(), walk));
                walk = walk.below();
            }
            walk = current.above();
            while(plot.contains(walk.left())&&!plot.contains(walk)) {
                edge.add(new Border(walk.left(), walk));
                walk = walk.above();
            }
            edges.add(edge);
        }
        return edges;
    }

    private record Coord(int x, int y) {
        List<Coord> neighbours() {
            return List.of(new Coord(x-1, y), new Coord(x+1, y), new Coord(x, y-1), new Coord(x, y+1));
        }
        Coord above() {
            return new Coord(x, y-1);
        }
        Coord below() {
            return new Coord(x, y+1);
        }
        Coord left() {
            return new Coord(x-1, y);
        }
        Coord right() {
            return new Coord(x+1, y);
        }
    }

    private record Border(Coord innerSide, Coord outerSide) {

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(12);
        Day12 day12 = new Day12(input);
        System.out.println(day12.part1());
        System.out.println(day12.part2());
    }
}
