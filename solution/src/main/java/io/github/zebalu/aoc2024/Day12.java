package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.function.UnaryOperator;

public class Day12 extends AbstractDay {
    private final int height;
    private final int width;
    private final char[][] data;
    private final Set<Set<Coord>> plots;

    public Day12() {
        this(IOUtil.readInput(12));
    }

    Day12(String input) {
        super(input, "Garden Groups", 12);
        data = IOUtil.readCharGrid(INPUT);
        height = data.length;
        width = data[0].length;
        plots = new HashSet<>();
        Set<Coord> processed = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Coord current = new Coord(x, y);
                if (!processed.contains(current)) {
                    Set<Coord> plot = findPlot(current);
                    processed.addAll(plot);
                    plots.add(plot);
                }
            }
        }
    }

    @Override
    public String part1() {
        long sum = plots.stream().mapToLong(plot -> {
            long area = areaOf(plot);
            long perimeter = perimeterOf(plot);
            return area * perimeter;
        }).sum();
        return Long.toString(sum);
    }

    private long perimeterOf(Set<Coord> plot) {
        long count = 0;
        for (Coord current : plot) {
            for (Coord neighbour : current.neighbours()) {
                if (!plot.contains(neighbour)) {
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
        while (!queue.isEmpty()) {
            Coord current = queue.poll();
            current.neighbours().stream()
                    .filter(seen::add)
                    .filter(this::isValid)
                    .filter(c -> getType(c) == type)
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
        long sum = plots.stream().mapToLong(plot->{
            long area = areaOf(plot);
            long sides = sidesOf(plot);
            return area * sides;
        }).sum();
        return Long.toString(sum);
    }

    private long sidesOf(Set<Coord> plot) {
        Set<Set<Border>> edges = new HashSet<>();
        for (Coord spot : plot) {
            edges.addAll(edgesOf(spot, plot, Coord.horizontal, Coord.vertical, edges));
            edges.addAll(edgesOf(spot, plot, Coord.vertical, Coord.horizontal, edges));
        }
        return edges.size();
    }

    private Set<Set<Border>> edgesOf(Coord spot, Set<Coord> plot, List<UnaryOperator<Coord>> walks, List<UnaryOperator<Coord>> checks, Set<Set<Border>> foundEdges) {
        Set<Set<Border>> edges = new HashSet<>();
        for (UnaryOperator<Coord> innerCheck : checks) {
            UnaryOperator<Coord> outerCheck = innerCheck == checks.getFirst() ? checks.getLast() : checks.getFirst();
            Coord current = outerCheck.apply(spot);
            Set<Border> edge = new HashSet<>();
            Border start = new Border(spot, current);
            if (!plot.contains(current) && !alreadyProcessed(start, foundEdges)) {
                edge.add(start);
                for (UnaryOperator<Coord> walker : walks) {
                    Coord walk = walker.apply(current);
                    while (plot.contains(innerCheck.apply(walk)) && !plot.contains(walk)) {
                        edge.add(new Border(innerCheck.apply(walk), walk));
                        walk = walker.apply(walk);
                    }
                }
                edges.add(edge);
            }
        }
        return edges;
    }

    private boolean alreadyProcessed(Border start, Set<Set<Border>> foundEdges) {
        return foundEdges.stream().anyMatch(edge -> edge.contains(start));
    }

    private record Coord(int x, int y) {
        final static List<UnaryOperator<Coord>> horizontal = List.of(Coord::left, Coord::right);
        final static List<UnaryOperator<Coord>> vertical = List.of(Coord::above, Coord::below);

        List<Coord> neighbours() {
            return List.of(new Coord(x - 1, y), new Coord(x + 1, y), new Coord(x, y - 1), new Coord(x, y + 1));
        }

        Coord above() {
            return new Coord(x, y - 1);
        }

        Coord below() {
            return new Coord(x, y + 1);
        }

        Coord left() {
            return new Coord(x - 1, y);
        }

        Coord right() {
            return new Coord(x + 1, y);
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
