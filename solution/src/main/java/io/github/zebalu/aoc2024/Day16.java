package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;
import io.github.zebalu.aoc2024.utils.Map2D;

import java.util.*;

public class Day16 extends AbstractDay {
    private final char[][] grid;
    private final int height;
    private final int width;
    private final Coord start;
    private final Coord end;
    private final Map2D bestPathPoints;
    private final Map<Raindeer, Integer> seen = new HashMap<>();

    private int minPrice = Integer.MAX_VALUE;

    public Day16() {
        this(IOUtil.readInput(16));
    }

    public Day16(String input) {
        super(input, "Reindeer Maze", 16);
        grid = IOUtil.readCharGrid(INPUT);
        height = grid.length;
        width = grid[0].length;
        Coord s = null;
        Coord e = null;
        for (int y = 0; y < height & (s == null || e == null); y++) {
            for (int x = 0; x < width & (s == null || e == null); x++) {
                if (grid[y][x] == 'S') {
                    s = new Coord(x, y);
                } else if (grid[y][x] == 'E') {
                    e = new Coord(x, y);
                }
            }
        }
        start = s;
        end = e;
        bestPathPoints = new Map2D(width, height);
        findPaths();
    }

    @Override
    public String part1() {
        return Integer.toString(minPrice);
    }

    @Override
    public String part2() {
        return Integer.toString(bestPathPoints.size());
    }

    private void findPaths() {
        Queue<State> priorityQueue = new PriorityQueue<>(State.PRICE_COMPARATOR);
        Map<Coord, Map2D> bests = new HashMap<>();
        Raindeer startDeer = new Raindeer(start, Coord.EAST);
        seen.put(startDeer, 0);
        Map2D startMap = new Map2D(width, height);
        startMap.mark(start.x, start.y);
        priorityQueue.add(new State(startDeer, 0, startMap));
        boolean weAreOverBestPaths = false;
        while (!priorityQueue.isEmpty() && !weAreOverBestPaths) {
            State state = priorityQueue.poll();
            for (State next : state.next()) {
                if (isFree(next.raindeer.position) && next.price <= seen.getOrDefault(next.raindeer, Integer.MAX_VALUE)) {
                    if (next.price < seen.getOrDefault(next.raindeer, Integer.MAX_VALUE)) {
                        priorityQueue.add(next);
                        seen.put(next.raindeer, next.price);
                        if (end.equals(next.raindeer.position)) {
                            if (minPrice < next.price) {
                                weAreOverBestPaths = true;
                            }
                            minPrice = Math.min(minPrice, next.price);
                            bestPathPoints.markAll(next.visited);
                        }
                        bests.put(next.raindeer.position, next.visited);
                    } else {
                        bests.get(next.raindeer.position).markAll(next.visited);
                    }
                }
            }
        }
    }

    private boolean isFree(Coord position) {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height && grid[position.y][position.x] != '#';
    }


    private record Coord(int x, int y) {
        static final Coord EAST = new Coord(1, 0);
        Coord add(Coord other) {
            return new Coord(this.x + other.x, this.y + other.y);
        }
    }

    private record Raindeer(Coord position, Coord orientation) {
        Raindeer turnLeft() {
            return new Raindeer(position, new Coord(-orientation.y, orientation.x));
        }

        Raindeer turnRight() {
            return new Raindeer(position, new Coord(orientation.y, -orientation.x));
        }

        Raindeer step() {
            return new Raindeer(position.add(orientation), orientation);
        }
    }

    private record State(Raindeer raindeer, int price, Map2D visited) {
        static Comparator<State> PRICE_COMPARATOR = Comparator.comparing(State::price);

        State turnLeft() {
            return new State(raindeer.turnLeft(), price + 1000, visited);
        }

        State turnRight() {
            return new State(raindeer.turnRight(), price + 1000, visited);
        }

        State step() {
            Raindeer step = raindeer.step();
            var v = visited.clone();
            v.mark(step.position.x, step.position.y);
            return new State(step, price + 1, v);
        }

        List<State> next() {
            return List.of(turnLeft(), turnRight(), step());
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(16);
        Day16 day16 = new Day16(input);
        System.out.println(day16.part1());
        System.out.println(day16.part2());
    }
}
