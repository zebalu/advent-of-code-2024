package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day08 extends AbstractDay {
    private final Map<Character, List<Coord>> antennaMap = new HashMap<>();
    private final int height;
    private final int width;

    public Day08() {
        this(IOUtil.readInput(8));
    }

    public Day08(String input) {
        super(input, "Resonant Collinearity", 8);
        var lines = INPUT.lines().toList();
        height = lines.size();
        width = lines.getFirst().length();
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (c != '.') {
                    Coord coord = new Coord(x, y);
                    antennaMap.computeIfAbsent(c, _ -> new ArrayList<>()).add(coord);
                }
            }
        }
    }

    @Override
    public String part1() {
        Set<Coord> antinodes = antennaMap.values().stream()
                .flatMap(v->collectCloseAntinodesFor(v).stream())
                .collect(Collectors.toSet());
        return Integer.toString(antinodes.size());
    }

    private Set<Coord> collectCloseAntinodesFor(List<Coord> coords) {
        Set<Coord> collector = new HashSet<>();
        for (int i = 0; i < coords.size(); i++) {
            for (int j = i + 1; j < coords.size(); j++) {
                Coord c1 = coords.get(i);
                Coord c2 = coords.get(j);
                var diff = c1.minus(c2);
                Stream.of(c1.add(diff), c2.minus(diff))
                        .filter(this::isValid)
                        .forEach(collector::add);
            }
        }
        return collector;
    }

    private Set<Coord> collectAllAntinodes(List<Coord> coords) {
        Set<Coord> collector = new HashSet<>();
        for (int i = 0; i < coords.size(); i++) {
            for (int j = i + 1; j < coords.size(); j++) {
                Coord c1 = coords.get(i);
                Coord c2 = coords.get(j);
                Coord diff = c2.minus(c1);
                collectFrom(c1, collector, c->c.add(diff));
                collectFrom(c2, collector, c->c.minus(diff));
            }
        }
        return collector;
    }

    private void collectFrom(Coord origin, Set<Coord> collector, Function<Coord, Coord> next) {
        Stream.iterate(origin, this::isValid, next::apply).forEach(collector::add);
    }

    private boolean isValid(Coord c) {
        return 0 <= c.x && c.x < width && 0 <= c.y && c.y < height;
    }

    @Override
    public String part2() {
        Set<Coord> antinodes = antennaMap.values().stream()
                .flatMap(v->collectAllAntinodes(v).stream())
                .collect(Collectors.toSet());
        return Integer.toString(antinodes.size());
    }

    private record Coord(int x, int y) {
        Coord add(Coord c) {
            return new Coord(x + c.x, y + c.y);
        }

        Coord minus(Coord c) {
            return new Coord(x - c.x, y - c.y);
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(8);
        Day08 day08 = new Day08(input);
        System.out.println(day08.part1());
        System.out.println(day08.part2());
    }
}
