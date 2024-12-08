package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day08 extends AbstractDay {
    private final Map<Character, List<Coord>> antennaMap = new HashMap<>();
    private final Set<Coord> antennaLocations = new HashSet<>();
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
        for(int y=0; y<height; y++) {
            String line = lines.get(y);
            for(int x=0; x<width; x++) {
                char c = line.charAt(x);
                if(c !='.') {
                    Coord coord = new Coord(x,y);
                    antennaMap.computeIfAbsent(c, _ -> new ArrayList<>()).add(coord);
                    antennaLocations.add(coord);
                }
            }
        }
    }
    @Override
    public String part1() {
        System.out.println(antennaMap);
        Set<Coord> antinoeds = new HashSet<>();
        int count = 0;
        for(var v: antennaMap.values()) {
            var a = countAntinodesFor(v);
            count += a.size();
            antinoeds.addAll(a);
        }
        var lines = INPUT.lines().toList();
        for(int y=0; y<height; y++) {
            var line = lines.get(y);
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x,y);
                if(antinoeds.contains(c)) {
                    System.out.print('#');
                } else {
                    System.out.print(line.charAt(x));
                }
            }
            System.out.println();
        }
        return ""+antinoeds.size()+"\t"+count;
    }

    private Set<Coord> countAntinodesFor(List<Coord> coords) {
        Set<Coord> forbidden = new HashSet<>(coords);
        Set<Coord> collector = new HashSet<>();
        for(int i=0; i<coords.size(); i++) {
            for(int j=i+1; j<coords.size(); j++) {
                Coord c1 = coords.get(i);
                Coord c2 = coords.get(j);
                var options = List.of(
                c1.add(c2.minus(c1)),
                c1.add(c1.minus(c2)),
                c2.add(c1.minus(c2)),
                c2.add(c2.minus(c1)));
                for(Coord o : options) {
                    if(!coords.contains(o) && isValid(o)) {
                        collector.add(o);
                    }
                }
            }
        }
        return collector;
    }

    private Set<Coord> collectAntinodes(List<Coord> coords) {
        Set<Coord> forbidden = new HashSet<>(); //coords);
        Set<Coord> collector = new HashSet<>();
        for(int i=0; i<coords.size(); i++) {
            for(int j=i+1; j<coords.size(); j++) {
                Coord c1 = coords.get(i);
                Coord c2 = coords.get(j);
                var options = List.of(
                        c1.add(c2.minus(c1)),
                        c1.add(c1.minus(c2)),
                        c2.add(c1.minus(c2)),
                        c2.add(c2.minus(c1)));
                var coll = new ArrayList<Coord>();
                Coord dif = c2.minus(c1);
                var next = c1;
                while (isValid(next)) {
                    if(!forbidden.contains(next)) {
                        coll.add(next);
                    }
                    next = next.add(dif);
                }next = c1;
                while (isValid(next)) {
                    if(!forbidden.contains(next)) {
                        coll.add(next);
                    }
                    next = next.minus(dif);
                }
                collector.addAll(coll);
            }
        }
        return collector;
    }

    private boolean isValid(Coord c) {
        return 0 <= c.x && c.x < width && 0 <= c.y && c.y < height;
    }

    @Override
    public String part2() {
        Set<Coord> antinodes = new HashSet<>();
        int count = 0;
        for(var v: antennaMap.values()) {
            var a = collectAntinodes(v);
            count += a.size();
            antinodes.addAll(a);
        }
        var lines = INPUT.lines().toList();
        for(int y=0; y<height; y++) {
            var line = lines.get(y);
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x,y);
                if(antinodes.contains(c)) {
                    System.out.print('#');
                } else {
                    System.out.print(line.charAt(x));
                }
            }
            System.out.println();
        }
        return ""+antinodes.size()+"\t"+count;
    }

    private record Coord(int x, int y) {
        Coord add(Coord c) {
            return new Coord(x+c.x, y+c.y);
        }
        Coord minus(Coord c) {
            return new Coord(x-c.x, y-c.y);
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(8);
        Day08 day08 = new Day08(input);//EXAMPLE3);//EXAMPLE);//EXAMPLE3);//input);//EXAMPLE);//input);//EXAMPLE);
        System.out.println(day08.part1());
        System.out.println(day08.part2());
    }

    private static final String EXAMPLE = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............""";
    private static final String EXAMPLE2= """
            ..........
            ..........
            ..........
            ....a.....
            ..........
            .....a....
            ..........
            ..........
            ..........
            ..........""";
    private static final String EXAMPLE3= """
            T.........
            ...T......
            .T........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........""";
}

