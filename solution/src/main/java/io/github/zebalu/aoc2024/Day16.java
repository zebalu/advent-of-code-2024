package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day16 extends AbstractDay {
    char[][] grid;
    int height;
    int width;
    Coord start;
    Coord end;
    public Day16() {
        this(IOUtil.readInput(16));
    }
    public Day16(String input) {
        super(input, "?????", 16);
        grid = IOUtil.readCharGrid(INPUT);
        height = grid.length;
        width = grid[0].length;
        for(int y=0; y<height; y++) {
            for(int x = 0; x < width; x++) {
                if (grid[y][x] == 'S') {
                    start = new Coord(x, y);
                } else if (grid[y][x] == 'E') {
                    end = new Coord(x, y);
                }
            }
        }
    }

    @Override
    public String part1() {
        //Set<Raindeer> seen = new HashSet<>();
        Set<Coord> pathPoints = new HashSet<>();
        Map<Raindeer, Integer> seen = new HashMap<>();
        Queue<State> priorityQueue = new PriorityQueue<>(State.PRICE_COMPARATOR);
        //seen.add(new Raindeer(start, new Coord(1, 0)));
        seen.put(new Raindeer(start, new Coord(1,0)), 0);
        priorityQueue.add(new State(new Raindeer(start, new Coord(1, 0)), 0, new HashSet<>(List.of(start))));
        int price = Integer.MAX_VALUE;
        int count = Integer.MIN_VALUE;
        boolean weAreOverBestPathes = false;
        while(!priorityQueue.isEmpty() && !weAreOverBestPathes) {
            State state = priorityQueue.poll();
            for(State next: state.next()) {
                if(isFree(next.raindeer.position) && seen.getOrDefault(next.raindeer, Integer.MAX_VALUE) >= next.price) {
                    priorityQueue.add(next);
                    seen.put(next.raindeer, next.price);
                    if(end.equals(next.raindeer.position)) {
                        if(next.price>price) {
                            weAreOverBestPathes = true;
                        }
                        price = Math.min(price, next.price);
                        pathPoints.addAll(next.visited);
                    }
                }
            }
            //System.out.println(priorityQueue.size());
        }
        System.out.println(priorityQueue.peek());
        System.out.println("copunt: "+pathPoints.size());
        /*
        State s = priorityQueue.poll();
        for(int y=0; y<height; y++) {
            for (int x = 0; x < width; x++) {
                Coord c=new Coord(x,y);
                if(!s.visited.contains(c)) {
                    System.out.print(grid[y][x]);
                } else {
                    System.out.print("O");
                }
            }
            System.out.println();
        }
        System.out.println(s.visited.size());

         */
        return ""+price;
    }

    private boolean isFree(Coord position) {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height && grid[position.y][position.x] != '#';
    }

    @Override
    public String part2() {
        return "";
    }

    private record Coord(int x, int y) {
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
    private record State(Raindeer raindeer, int price, Set<Coord> visited) {
        static Comparator<State> PRICE_COMPARATOR = Comparator.comparing(State::price);
        State turnLeft() {
            return new State(raindeer.turnLeft(), price+1000, visited);
        }
        State turnRight() {
            return new State(raindeer.turnRight(), price+1000, visited);
        }
        State step() {
            Raindeer step = raindeer.step();
            var v = new HashSet<>(visited);
            v.add(step.position);
            return new State(step, price+1, v);
        }
        List<State> next() {
            return List.of(turnLeft(), turnRight(), step());
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(16);
        Day16 day16 = new Day16(input);//EXAMPLE_2);//EXAMPLE_1);//input);//EXAMPLE_2);//EXAMPLE_1);//input);
        System.out.println(day16.part1());
        System.out.println(day16.part2());
    }

    private static final String EXAMPLE_1= """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############""";
    private static final String EXAMPLE_2= """
            #################
            #...#...#...#..E#
            #.#.#.#.#.#.#.#.#
            #.#.#.#...#...#.#
            #.#.#.#.###.#.#.#
            #...#.#.#.....#.#
            #.#.#.#.#.#####.#
            #.#...#.#.#.....#
            #.#.#####.#.###.#
            #.#.#.......#...#
            #.#.###.#####.###
            #.#.#...#.....#.#
            #.#.#.#####.###.#
            #.#.#.........#.#
            #.#.#.#########.#
            #S#.............#
            #################""";
}
