package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 extends AbstractDay {
    char[][] data;
    int width;
    int height;
    Coord start;
    Coord end;
    public Day20(String input) {
        super(input, "Race Condition", 20);
        data = IOUtil.readCharGrid(INPUT);
        height = data.length;
        width = data[0].length;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if('S' == data[y][x]) {
                    start = new Coord(x, y);
                } else if ('E' == data[y][x]) {
                    end = new Coord(x, y);
                }
            }
        }
    }

    @Override
    public String part1() {
        List<Coord> noCheatPath = pathLength(new LinkedHashSet<>(), Integer.MAX_VALUE, List.of());
        SequencedSet<Coord> noCheat = new LinkedHashSet<>(noCheatPath);
        List<List<SequencedSet<Coord>>> cheatOptions = new ArrayList<>(noCheat.size()*2);
        System.out.println("ncs: "+noCheat.size());
        int step = 0;
        for(Coord c : noCheat) {
            ++step;
            List<SequencedSet<Coord>> l = new ArrayList<>();
            for(var n: c.steps()) {
                if(!noCheat.contains(n)) {
                    for(var nn: n.steps()) {
                        if(!noCheat.contains(nn)) {
                            l.add(new LinkedHashSet<>(List.of(c, n, nn)));
                        }
                    }
                }
            }
            cheatOptions.add(l);
        }
        System.out.println(noCheatPath.getFirst().equals(start) && noCheatPath.getLast().equals(end));
        System.out.println("options: "+cheatOptions.size());
        Map<Coord, Integer> fromStart = distance(start);
        Map<Coord, Integer> fromEnd = distance(end);
        System.out.println("fromStart: "+fromStart.get(end));
        int sum = 0;
        step = 0;
        List<SequencedSet<Coord>> otherCheats = new ArrayList<>();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x, y);
                if(isValid(c)) {
                    for(var w: c.steps()) {
                        if(!isValid(w)) {
                            SequencedSet<Coord> cheats = new LinkedHashSet<>(List.of(c, w));
                            for(var e: w.steps()) {
                                if(!cheats.contains(e) && isValid(e)) {
                                    SequencedSet<Coord> cs = new LinkedHashSet<>(cheats);
                                    cs.add(e);
                                    otherCheats.add(cs);
                                }
                            }
                        }
                    }
                }
            }
        }
        for(var ch: otherCheats) {
            if(fromStart.containsKey(ch.getFirst()) && fromEnd.containsKey(ch.getLast())) {
                    int cost = fromStart.get(ch.getFirst())+2+fromEnd.get(ch.getLast());
                    if(cost <= noCheat.size()-100) {
                        System.out.println(ch);
                        System.out.println("cost: "+cost);
                        ++sum;
                    }
                }
        }

//        for(var cheat: cheatOptions) {
//            ++step;
//            for(var ch: cheat) {
//                if(fromStart.containsKey(ch.getFirst()) && fromEnd.containsKey(ch.getLast())) {
//                    int cost = fromStart.get(ch.getFirst())+2+fromEnd.get(ch.getLast());
//                    if(cost <= noCheat.size()-41) {
//                        System.out.println(ch);
//                        System.out.println("cost: "+cost);
//                        ++sum;
//                    }
//                } /*else {
//                    int cost = noCheat.size();
//                    for(var p: ch.getLast().steps()) {
//                        if(fromEnd.containsKey(p)) {
//                            cost = Math.min(fromStart.get(ch.getFirst())+3+fromEnd.get(p), cost);
//                        }
//                    }
//                    if(cost <= noCheat.size()-64) {
//                        System.out.println("other: "+ch);
//                        ++sum;
//                    }
//                }*/
//                /*
//                var newPath = pathLength(ch, noCheat.size() - 99, noCheatPath.subList(0, step));
//                if (!newPath.isEmpty() && newPath.size() <= noCheat.size() - 100) {
//                    ++sum;
//                }*/
//            }
//            if(step % 1000 == 0) {
//                System.out.println(step+"\tof\t"+cheatOptions.size());
//                System.out.println("sum now: "+sum);
//            }
//        }
        //2977 too high
        return "sum: "+sum;
    }

    @Override
    public String part2() {
        List<Coord> noCheatPath = pathLength(new LinkedHashSet<>(), Integer.MAX_VALUE, List.of());
        SequencedSet<Coord> noCheat = new LinkedHashSet<>(noCheatPath);
        System.out.println(noCheatPath.getFirst().equals(start) && noCheatPath.getLast().equals(end));
        Map<Coord, Integer> fromStart = distance(start);
        Map<Coord, Integer> fromEnd = distance(end);
        System.out.println("fromStart: "+fromStart.get(end));
        int sum = 0;
        Set<Cheat> otherCheats = findCheats2();
        System.out.println("to try: "+otherCheats.size());
        int steps = 0;
        for(var ch: otherCheats) {
            ++steps;
            if(fromStart.containsKey(ch.first()) && fromEnd.containsKey(ch.last())) {
                int cost = fromStart.get(ch.first())+ch.length()+fromEnd.get(ch.last());
                if(cost <= noCheat.size()-100) {
                    //System.out.println(cost+"\tsave: "+(noCheat.size()-cost)+"\t"+ch);
                    ++sum;
                }
            }
            if(steps%100_000==0) {
                System.out.println("at: "+steps+"\tsum: "+sum);
            }
        }
        //426197
        //461036 too low
        //525509 too low
        //547239 too low
        //550291 too low
        //613860 no :(
        return ("sum2: "+sum);
    }

    private List<Coord> pathLength(SequencedSet<Coord> cheats, int maxLength, List<Coord> before) {
        Coord myStart = cheats.isEmpty()?start:cheats.getFirst();
        List<Coord> beforeStart = new ArrayList<>(before);
        beforeStart.add(myStart);
        Queue<Path> queue = new ArrayDeque<>();
        Set<Coord> visited = new HashSet<>(beforeStart);
        queue.add(new Path(myStart, beforeStart));
        while(!queue.isEmpty()) {
            Path cur = queue.poll();
            if(cur.log.size()<maxLength) {
                for (Coord s : cur.position.steps()) {
                    if (visited.add(s) && (cheats.contains(s) || isValid(s))) {
                        Path p = cur.extend(s);
                        if (p.position.equals(end)) {
                            return cur.log;
                        }
                        queue.add(cur.extend(s));
                    }

                }
            }
        }
        return List.of();
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
                    priceMap.compute(n, (k, v) -> priceMap.get(cur)+1);
                    queue.add(n);
                }
            }
        }
        return priceMap;
    }

    private Set<Cheat> findCheats() {
        Set<Cheat> cheats = new HashSet<>();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x, y);
                Set<Coord> visited = new HashSet<>();
                if(isValid(c)) {
                    Set<Coord> available = new HashSet<>(List.of(c));
                    for(int j=0; j<20; j++) {
//                        System.out.println(j+"\t"+available.size()+"\t"+available);
                        Set<Coord> border = new HashSet<>();
                        for(var a: available) {
//                            for(Coord e:a.steps()) {
//                                if(!available.contains(e) && isValid(e)) {
//                                    border.add(e);
//                                }
//                            }
//                            for(var b: a.steps()) {
//                                if(visited.add(b)) {
//                                    border.add(b);
//                                }
//                            }
                              border.addAll(a.steps().stream().filter(this::isInGrid).toList());
                        }
//                        if(j==10) {
//                            System.exit(1);
//                        }
                        available = border;
                    }
                    for(var e: available) {
                        if(isValid(e)) {
                            cheats.add(new Cheat(c, e));
                        }
                    }
                }
            }
        }
        return cheats;
    }

    private Set<Cheat> findCheats2() {
        Set<Cheat> cheats = new HashSet<>();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                Coord c = new Coord(x, y);
                if(isValid(c)) {
                    for(int y2=y-20; y2<=y+20; y2++) {
                        for(int x2=x-20; x2<=x+20; x2++) {
                            Coord c2 = new Coord(x2, y2);
                            if(isValid(c2) && c.distance(c2)<=20) {
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

    private boolean isInGrid(Coord coord) {
        return 0<=coord.x && coord.x<width && 0<=coord.y && coord.y<height;
    }

    private record Coord(int x, int y) {
        List<Coord> steps() {
            return List.of(new Coord(x-1, y), new Coord(x+1, y), new Coord(x, y-1), new Coord(x, y+1));
        }
        int distance(Coord coord) {
            return Math.abs(coord.x-x)+Math.abs(coord.y-y);
        }
    }

    private record Path(Coord position, List<Coord> log) {
        Path extend(Coord next) {
            ArrayList<Coord> steps = new ArrayList<Coord>(log);
            steps.add(next);
            return new Path(next, steps);
        }
    }

    private record Cheat(Coord first, Coord last) {
        int length() {
            return first.distance(last)-1;
        }

//        @Override
//        public int hashCode() {
//            return first.hashCode()+last.hashCode();
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if(obj instanceof Cheat) {
//                return (first.equals(((Cheat)obj).first) && last.equals(((Cheat)obj).last))
//                        ||(first.equals(((Cheat) obj).last) && last.equals(((Cheat)obj).first));
//            }
//            return false;
//        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(20);
        Day20 day20 = new Day20(input);//EXAMPLE); //input);//EXAMPLE);//input); //EXAMPLE);//input);
        System.out.println(day20.part1());
        System.out.println(day20.part2());
    }

    private static String EXAMPLE = """
            ###############
            #...#...#.....#
            #.#.#.#.#.###.#
            #S#...#.#.#...#
            #######.#.#.###
            #######.#.#...#
            #######.#.###.#
            ###..E#...#...#
            ###.#######.###
            #...###...#...#
            #.#####.#.###.#
            #.#...#.#.#...#
            #.#.#.#.#.#.###
            #...#...#...###
            ###############""";
}
