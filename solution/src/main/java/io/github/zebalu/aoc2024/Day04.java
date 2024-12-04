package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day04 extends AbstractDay {
    private static final List<Coord> DIRECTIONS = List.of(
            new Coord(1,0), new Coord(0,1),
            new Coord(-1, 0), new Coord(0, -1),
            new Coord(1,1), new Coord(-1,-1),
            new Coord(1, -1), new Coord(-1, 1)
    );

    private static final List<Coord> DIAGONAL_DIRECTIONS = List.of(
            new Coord(1,1), new Coord(-1,-1),
            new Coord(1, -1), new Coord(-1, 1)
    );

    private final char[][] matrix;

    public Day04() {
        this(IOUtil.readInput(4));
    }

    public Day04(String input) {
        super(input, "Ceres Search", 4);
        var lines = INPUT.lines().toList();
        matrix = new char[lines.size()][lines.getFirst().length()];
        for(int i = 0; i < matrix.length; i++) {
            String line = lines.get(i);
            for(int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = line.charAt(j);
            }
        }
    }

    @Override
    public String part1() {
        int sum = 0;
        for(int x = 0; x < matrix.length; x++) {
            for(int y = 0; y < matrix[x].length; y++) {
                sum += collectCoords(x,y, "XMAS", DIRECTIONS).size();
            }
        }
        return Integer.toString(sum);
    }

    @Override
    public String part2() {
        Map<Coord, List<List<Coord>>> masses = new HashMap<>();
        for(int x = 0; x < matrix.length; x++) {
            for(int y = 0; y < matrix[x].length; y++) {
                collectCoords(x,y, "MAS", DIAGONAL_DIRECTIONS).forEach(mass -> {
                    masses.computeIfAbsent(mass.get(1), _ -> new ArrayList<>()).add(mass);
                });
            }
        }
        return Long.toString(masses.values().stream().filter(v->v.size()==2).count());
    }

    private boolean isValid(Coord coord) {
        return coord.x() >= 0 && coord.y() >= 0 && coord.x() < matrix.length && coord.y() < matrix[coord.x()].length;
    }

    private List<List<Coord>> collectCoords(int x, int y, String string, List<Coord> directions) {
        List<List<Coord>> result = new ArrayList<>();
        for(var direction : directions) {
            boolean valid = true;
            List<Coord> used = new ArrayList<>();
            Coord current = new Coord(x,y);
            for(int i=0; i<string.length() && valid && isValid(current); i++) {
                used.add(current);
                valid = matrix[current.x()][current.y()] == string.charAt(i);
                current = current.add(direction);
            }
            if(valid && used.size() == string.length()) {
                result.add(used);
            }
        }
        return result;
    }

    private record Coord(int x, int y) {
        Coord add(Coord other) {
            return new Coord(x + other.x, y + other.y);
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(4);
        Day04 day04 = new Day04(input);
        System.out.println(day04.part1());
        System.out.println(day04.part2());
    }
}
