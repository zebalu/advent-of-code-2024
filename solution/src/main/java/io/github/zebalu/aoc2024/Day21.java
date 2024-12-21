package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 extends AbstractDay {
    private final char[][] keyPad = new char[][] {
            new char[]{'7', '8', '9'},
            new char[]{'4', '5', '6'},
            new char[]{'1', '2', '3'},
            new char[]{'#', '0', 'A'}
    };
    private final char[][] dirPad = new char[][] {
            new char[]{'#', '^', 'A'},
            new char[]{'<', 'v', '>'}
    };

    private final Map<Character, Coord> coordOf = Map.of (
            '^', new Coord(1,0),
            'A', new Coord(2, 0),
            '<', new Coord(0,1),
            'v', new Coord(1,1),
            '>', new Coord(2,1)
    );

    private final List<String> toType;

    private Coord keyRobotPosition = new Coord (2, 3);
    private Coord robot1Position = new Coord(2,0);
    private Coord robot2Position = new Coord(2,0);

    public Day21() {
        this(IOUtil.readInput(21));
    }
    public Day21(String input) {
        super(input, "Keypad Conundrum", 21);
        toType = INPUT.lines().toList();
    }

    @Override
    public String part1() {
        int sum = 0;
//        for(String code: toType) {
//            if(code.equals("179A")) {
//                System.out.println("check");
//            }
//            keyRobotPosition = new Coord(2,3);
//            robot1Position = new Coord(2,0);
//            robot2Position = new Coord(2,0);
//            StringBuilder r1B = new StringBuilder();
//            StringBuilder r2B = new StringBuilder();
//            StringBuilder mB = new StringBuilder();
//            Map<Path, StringBuilder> map = new HashMap<>();
//            List<List<StringBuilder>> mBss = new ArrayList<>();
//            for(char c: code.toCharArray()) {
//                //System.out.println("  c: "+c);
//                String bestK = null;
//                for(var kPath:  pushKey(c)) {
//                    map.put(kPath, new StringBuilder());
//                    mBss.add(new ArrayList<>());
//                    keyRobotPosition = kPath.end;
//                    String r1Path = kPath.steps;
//                    r1B.append(r1Path);
//                    r1B.append("A");
//                    //System.out.println(" r1: " + r1B.toString());
//                    robot1Position = new Coord(2,0);
//                    String bestR1 = null;
//                    for (char cr1 : (r1Path + 'A').toCharArray()) {
//                    //    System.out.println("cr1: " + cr1);
//                        String r1Buid = "";
//                        for(var r1p: pushDir(cr1, 1)) {
//                            mBss.getLast().add(new StringBuilder());
//                            String r2Path = r1p.steps;
//                            robot1Position = r1p.end; //pushDir(cr1, 1);
//                            r2B.append(r2Path);
//                            r2B.append("A");
//                      //      System.out.println(" r2: " + r2B.toString());
//                            robot2Position = new Coord(2,0);
//                            String bestR2 = "";
//                            for (char cr2 : (r2Path + 'A').toCharArray()) {
//                        //        System.out.println("cr2: " + cr2);
//                                Path best = pushDir(cr2, 2).stream().min(Comparator.comparingInt(p->p.steps.length())).orElseThrow();
//                                robot2Position = best.end;
//                                //String mPath = pushDir(cr2, 2);
//                                //mB.append(mPath);
//                                //mB.append("A");
//                                //System.out.println("  m: " + mB.toString());
//                                mBss.getLast().getLast().append(best.steps);
//                                mBss.getLast().getLast().append("A");
//                                bestR2 = bestR2 + best.steps + "A";
//                            }
//                            r1Buid = r1Buid + bestR2 + "A";
//                        }
//                        if(bestR1 == null) {
//                            bestR1 = r1Buid;
//                        } else if (bestR1.length()>r1Buid.length()) {
//                            bestR1 = r1Buid;
//                        }
//                    }
//
//                    if(bestK == null) {
//                        bestK = bestR1;
//                    } else if(bestK.length() > bestR1.length()) {
//                        bestK = bestR1;
//                    }
//                }
//                mB.append(bestK);
//            }
//            //StringBuilder bestMB = mBss.stream().flatMap(List::stream).min(Comparator.comparingInt(StringBuilder::length)).orElseThrow();
//            //mB.append(bestMB.toString());
//            System.out.println("----");
//            System.out.println(mB.toString());
//            System.out.println(r2B.toString());
//            System.out.println(r1B.toString());
//            System.out.println(code);
//            System.out.println("code as int: " + codeToNumber(code));
//            System.out.println("type length: " + mB.length());
//            int complexity = codeToNumber(code)*mB.length();
//            System.out.println("complexity: "+complexity);
//            System.out.println("*****");
//            sum += complexity;
//            /*
//            String pushCode = code.chars().mapToObj(c -> (char)c +"A").collect(Collectors.joining());
//            List<String> keyPaths = pushCode.chars().mapToObj(c -> pushKey((char)c)+"A").toList();
//
//            List<String> r1Path = keyPaths.stream().map(s->s+'A').map(s->s.chars().mapToObj(c -> pushDir((char)c)).collect(Collectors.joining())).toList();
//            //List<String> r2Path = r1Path.stream().map(s->s+'A').map(s->s.chars().mapToObj(c -> pushDir((char)c)).collect(Collectors.joining())).toList();
//            String myPath = r1Path.stream().map(s->s+'A').map(s->s.chars().mapToObj(c -> pushDir((char)c)).collect(Collectors.joining())).collect(Collectors.joining());
//            System.out.println(myPath);
//            System.out.println(String.join("", r1Path));
//            System.out.println(String.join("", keyPaths));
//            System.out.println(code);
//            System.out.println("_____");
//
//             */
//
//        }
//        System.out.println("-------------------------------------------------");
//
//        String best=null;
//        var rrr = codeToKeypadString("029A", new Coord(2,3), "");
//        for(var rr: rrr) {
//            var r = robotize(rr, new Coord(2,0), "");
//            for(var m: r) {
//                var mm = robotize(m, new Coord(2,0), "");
//                var mmm = mm.stream().min(Comparator.comparingInt(String::length)).orElseThrow();
//                if(best ==null) {
//                    best = mmm;
//                } else {
//                    if(best.length()>mmm.length()) {
//                        best = mmm;
//                    }
//                }
//                System.out.println(mmm);
//                System.out.println(m);
//                System.out.println(rr);
//                System.out.println("029A");
//                System.out.println(mmm.length());
//            }
//        }
//        System.out.println(best);
//        System.out.println(best.length());
        sum = 0;
        for(String code: toType) {
            String myBest = null;
            List<String> robot1Moves = codeToKeypadString(code, new Coord(2,3), "");
            for(String r1: robot1Moves) {
                List<String> robot2Moves = robotize(r1, new Coord(2,0), "");
                for(String r2: robot2Moves) {
                    //List<List<String>> mmm3 = robotize3(r2, new Coord(2,0), List.of());
                    //String mmm3S = String.join("", mmm3.getFirst());
                    List<String> myMoves = robotize(r2, new Coord(2,0), "");
                    String bestPossible = myMoves.stream().min(Comparator.comparingInt(String::length)).orElseThrow();
                    //System.out.println(bestPossible.length()+"\t"+bestPossible);
                    //System.out.println(mmm3S.length()+"\t"+mmm3S);
                    if(myBest == null) {
                        myBest = bestPossible;
                    } else if(myBest.length()>bestPossible.length()) {
                        myBest = bestPossible;
                    }
                }
            }
            sum += myBest.length() * codeToNumber(code);
            System.out.println("done: "+code+" with sum: "+sum);
        }
        return ""+sum;
    }

    private int codeToNumber(String code) {
        return Integer.parseInt(code.substring(0,code.length()-1));
    }

    @Override
    public String part2() {
        long sum = 0;
        for(String code: toType) {
            long myBest = Long.MAX_VALUE;
            List<String> robot1Moves = codeToKeypadString(code, new Coord(2,3), "");
            for(String r1: robot1Moves) {
                var m = new HashMap<Memo, Long>();
                long length = robotize2(r1, new Coord(2,0), 3, m);//new HashMap<>());
                myBest = Math.min(myBest, length);
            }
            sum += myBest * codeToNumber(code);
            System.out.println("done: "+code+" with sum: "+sum);
        }
        var m = new HashMap<Memo, Long>();
        System.out.println("test: "+robotize2("A", new Coord(2,0), 1, m));
        System.out.println("test: "+robotize2("<A", new Coord(2,0), 1, m));
        System.out.println("test: "+robotize2("v<A", new Coord(2,0), 1, m));
        System.out.println("test: "+robotize2(">>^A", new Coord(2,0), 1, m));
        System.out.println(m);
        return ""+sum;
    }

    private List<Path> pushKey(char key) {
        return shortestToKey(keyRobotPosition, key);
        /*
        Path shortest = shortestToKey(keyRobotPosition, key);
        keyRobotPosition = shortest.end;
        return shortest.steps;

         */
    }

    private List<Path> pushDir(char key, int robot) {
        Coord start = robot == 1 ? robot1Position : robot2Position;
        return shortestToDir(start, key);
        /*
        Path shortest = shortestToDir(start, key);
        if(robot == 1) {
            robot1Position = shortest.end;
        } else {
            robot2Position = shortest.end;
        }
        return shortest.steps;
         */
    }

    private List<Path> pushDir(char key, Coord start) {
        return shortestToDir(start, key);
    }

    private List<Path> shortestToKey(Coord start, char goal) {
        if(isValidKeyCoord(start) && getKeyChar(start) == goal) {
            return List.of(new Path(start, ""));
        }
        //Set<Coord> visited = new HashSet<>(List.of(start));
        Queue<Path> queue = new ArrayDeque<>(List.of(new Path(start, "")));
        List<Path> result = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Path p = queue.poll();
            for(var n: p.end.next()) {
                if(isValidKeyCoord(n) /*&& visited.add(n)*/) {
                    Path nP = new Path(n, p.steps+p.end.asDir(n));
                    if(getKeyChar(n) == goal) {
                        if(nP.steps.length()<best) {
                            best = nP.steps.length();
                            result.clear();
                            result.add(nP);
                        } else if(nP.steps.length()==best) {
                            result.add(nP);
                        }
                    } else if(nP.steps.length() < best) {
                        queue.add(nP);
                    }
                }
            }
        }
        //throw new IllegalStateException("There should be a path!");
        return result;
    }

    private List<Path> shortestToDir(Coord start, char goal) {
        if(isValidDirCoord(start) && getDirChar(start) == goal) {
            return List.of(new Path(start, ""));
        }
        //Set<Coord> visited = new HashSet<>(List.of(start));
        Queue<Path> queue = new ArrayDeque<>(List.of(new Path(start, "")));
        List<Path> result = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Path p = queue.poll();
            for(var n: p.end.next()) {
                if(isValidDirCoord(n) /*&& visited.add(n)*/) {
                    Path nP = new Path(n, p.steps+p.end.asDir(n));
                    if(getDirChar(n) == goal) {
                        if (nP.steps.length() < best) {
                            best = nP.steps.length();
                            result.clear();
                            result.add(nP);
                            //return List.of(nP);
                        } else if (nP.steps.length() == best) {
                            result.add(nP);
                        }
                    } else if(nP.steps.length() < best) {
                        queue.add(nP);
                    }
                }
            }
        }
        //System.out.println(start +" "+ goal);
        //throw new IllegalStateException("There should be a path!");
        return result;
    }

    private List<String> codeToKeypadString(String code, Coord start, String core) {
        if(code.isEmpty()) {
            return List.of(core);
        }
        keyRobotPosition = start;
        char c = code.charAt(0);
        List<String> collect = new ArrayList<>();
        for(var p: pushKey(c)) {
            collect.addAll(codeToKeypadString(code.substring(1), p.end, core+p.steps+"A"));
        }
        int minLength = collect.stream().mapToInt(String::length).min().orElseThrow();
        return collect.stream().filter(s->s.length()==minLength).distinct().collect(Collectors.toList());
    }

    private List<String> robotize(String code, Coord start, String core) {
        if(code.isEmpty()) {
            return List.of(core);
        }
        char c = code.charAt(0);
        List<String> collect = new ArrayList<>();
        for(var p: pushDir(c, start)) {
            collect.addAll(robotize(code.substring(1), p.end, core+p.steps+"A"));
        }
        int minLength = collect.stream().mapToInt(String::length).min().orElseThrow();
        return collect.stream().filter(s->s.length()==minLength).distinct().collect(Collectors.toList());
    }

    private List<List<String>> robotize3(String code, Coord start, List<String> soFar) {
        if(code.isEmpty()) {
            return List.of(soFar);
        }
        char c = code.charAt(0);
        List<List<String>> collect = new ArrayList<>();
        for(var p: pushDir(c, start)) {
            List<String> copy = new ArrayList<>(soFar);
            copy.add(p.steps+"A");
            var res1 = robotize3(code.substring(1), new Coord(2,0), copy);
            /*
            if(res1.size() != 1) {
                System.out.println("!!!!!!!!!");
            }*/
            if(res1.isEmpty()) {
                System.out.println("????");
            }
            collect.addAll(res1);
        }
        int minLength = collect.stream().mapToInt(this::innerStringSize).min().orElseThrow();
        return collect.stream().filter(l->innerStringSize(l)==minLength).distinct().collect(Collectors.toList());
    }

    private int innerStringSize(List<String> strings) {
        int sum =0;
        for(String s: strings) {
            sum += s.length();
        }
        return sum;
    }

    private long robotize2(String code, Coord start, int depth, Map<Memo, Long> map) {
//        var sequences = robotize(code, start, "");
//        if(depth==0) {
//            sequences.stream().mapToLong(String::length)
//        }
//        for(var sequence: sequences) {
//
//        }
        /*if(map.isEmpty()) {
            for(Coord coord: coordOf.values()) {
                for(char c: coordOf.keySet()) {
                    var best = robotize(""+c, coord, "").getFirst();
                    long length = best.length();
                    map.put(new Memo(coord, ""+c, 0), length);
                }
            }
        }
         */
        if(depth==0) {
            return code.length();
            /*
            long sum = 0L;
            Coord prev = start;
            //String e = "";
            for(char c: code.toCharArray()) {
                sum += map.get(new Memo(prev, ""+c, 0));
                //e += robotize(""+c, prev, "").getFirst();
                prev=coordOf.get(c);
            }
            //System.out.println(start+"\t"+code+"\t"+sum+"\t"+e);
            return sum;

             */
        }
        Memo thisMemo = new Memo(start, code, depth);
        if(map.containsKey(thisMemo)) {
            return map.get(thisMemo);
        }
        long sum = 0L;
        Coord prev = start;
        for(char c: code.toCharArray()) {
            Coord p = prev;
            sum += robotize(""+c, p, "").stream().mapToLong(option->robotize2(option, new Coord(2,0), depth-1, map)).min().orElseThrow();
            //sum += pushDir(c, prev).stream().mapToLong(push -> robotize2(push.steps, p, depth-1, map)).min().orElseThrow();
            prev = coordOf.get(c);
        }
//        List<List<String>> robotized = robotize3(code, start/*new Coord(2,0)*/, List.of());
//        long min = Long.MAX_VALUE;
//        for(var option: robotized) {
//            Coord prev = start;
//            long psum = 0L;
//            for(int i=0; i<option.size(); i++) {
//                char c = code.charAt(i);
//                for (var o : option) {
//                    psum += robotize2(o, prev, depth - 1, map);
//                    prev = coordOf.get(c);
//                }
//            }
//            min = Math.min(min, psum);
//        }
//        sum = min; //robotized.stream().mapToLong(l->l.stream().mapToLong(String::length).sum()).min().orElseThrow();
//        Coord prev = start;
//        for(int i=0; i<code.length(); i++) {
//            char c = code.charAt(i);
//            long min = Long.MAX_VALUE;
//            for (List<String> option : robotized) {
//                //System.out.println(String.join( "", option));
//                String encoded = option.get(i);
//                long length = robotize2(encoded, /*prev*/new Coord(2,0), depth - 1, map);
//                min = Math.min(min, length);
//                //robotize2(encoded, /*prev*/new Coord(2,0), depth - 1, map));
//            }
//            //bestbest = bestbest + best;
//            //map.put(new Memo(prev, ""+c, depth), min);
//            sum+=min;
//            prev = coordOf.get(c);
//        }
        /*
        prev = start;
        for(char c: code.toCharArray()) {
            sum += map.get(new Memo(prev, ""+c, depth));
            prev=coordOf.get(c);
        }*/
        //if(sum!=bestbest.length()) {
//            System.out.println(sum + " ?:? "+bestbest+"\t"+bestbest.length());
//        }
        map.put(thisMemo, sum);
        return sum;
    }

    private char getKeyChar(Coord coord) {
        return keyPad[coord.y][coord.x];
    }

    private char getDirChar(Coord coord) {
        return dirPad[coord.y][coord.x];
    }

    private boolean isValidKeyCoord(Coord coord) {
        return 0<= coord.y && coord.y < keyPad.length && 0<= coord.x && coord.x < keyPad[0].length && keyPad[coord.y][coord.x] != '#';
    }

    private boolean isValidDirCoord(Coord coord) {
        return 0<= coord.y && coord.y < dirPad.length && 0<= coord.x && coord.x < dirPad[0].length && dirPad[coord.y][coord.x] != '#';
    }

    private record Coord(int x, int y) {
        List<Coord> next() {
            return List.of(
                    new Coord(x+1, y), new Coord(x, y+1),
                    new Coord(x-1, y), new Coord(x, y-1));
        }
        char asDir(Coord other) {
            if(x < other.x()) {
                return '>';
            } else if(x > other.x()) {
                return '<';
            } else if(y < other.y()) {
                return 'v';
            } else if(y > other.y()) {
                return '^';
            } else {
                throw new IllegalArgumentException("Same coord was given: "+this+" to: " + other);
            }
        }
    }

    private record Path(Coord end, String steps) {

    }

    private record Memo(Coord from, String c, int depth) {

    }

    long part3() {
        System.out.println("#########");
        long sum = 0;
        for(String code: toType) {
            long myBest = Long.MAX_VALUE;
            List<String> robot1Moves = codeToKeypadString(code, new Coord(2,3), "");
            for(String robot1Move: robot1Moves) {
                long countP = countPushes(robot1Move, 25);
                myBest = Math.min(myBest, countP);
            }
            sum += myBest * codeToNumber(code);
        }

        System.out.println("167389793580400");
        return sum;
    }

    private long solve (int depth) {
        long sum = 0;
        for(String code: toType) {
            long myBest = Long.MAX_VALUE;
            List<String> robot1Moves = codeToKeypadString(code, new Coord(2,3), "");
            for(String robot1Move: robot1Moves) {
                long countP = countPushes(robot1Move, depth);
                myBest = Math.min(myBest, countP);
            }
            sum += myBest * codeToNumber(code);
        }
        return sum;
    }

    private Map<RoadDepth, Long> rdm = new HashMap<>();
    private long countPushes(String robot1Move, int depth) {
        if(depth==0) {
            return robot1Move.length();
        }
        var key = new RoadDepth(robot1Move, depth);
        if(rdm.containsKey(key)) {
            return rdm.get(key);
        }
        Coord position = new Coord(2,0);
        long sum = 0L;
        for(char c: robot1Move.toCharArray()) {
            long min = Long.MAX_VALUE;
            for(String road: roads(c, position)) {
                min = Long.min(min, countPushes(road, depth-1));
            }
            sum += min;
            position = coordOf.get(c);
        }
        rdm.put(key, sum);
        return sum;
    }

    List<String> roads (char to, Coord at) {
        return shortestToDir(at, to).stream().map(p->p.steps()+"A").toList();
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(21);
        Day21 day = new Day21(input); //EXAMPLE); //input);//EXAMPLE);//input);
        //System.out.println(day.part1());
        //System.out.println(day.part2());
        System.out.println(day.part3());
        System.out.println(day.solve(2));
        System.out.println(day.solve(25));
    }

    record RoadDepth(String road, int depth) {

    }

    private static final String EXAMPLE = """
            029A
            980A
            179A
            456A
            379A""";
}
