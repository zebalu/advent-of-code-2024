package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day15w extends AbstractDay {
    private final char[][] data;
    private final String move;
    private final int width;
    private final int height;
    public Day15w(String input) {
        super(input, "Warehouse Woes", 15);
        char[][] data = null;
        StringBuilder sb = new StringBuilder();
        for (String s : INPUT.lines().toList()) {
            if(s.isBlank()) {
                if(data == null) {
                    data = IOUtil.readCharGrid(sb.toString());
                    sb.setLength(0);
                }
            } else {
                if(data == null) {
                    sb.append(expand(s));
                    sb.append('\n');
                } else {
                    sb.append(s);
                }
            }
        }
        move = sb.toString();
        this.data = data;
        height = data.length;
        width = data[0].length;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                System.out.print(data[y][x]);
            }
            System.out.println();
        }
    }

    private String expand(String line) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if(c== '#') {
                sb.append("##");
            } else if(c=='O') {
                sb.append("[]");
            } else if (c=='.') {
                sb.append("..");
            } else if (c=='@') {
                sb.append("@.");
            } else {
                throw new IllegalArgumentException("invalid character: " + c);
            }
        }
        return sb.toString();
    }

    @Override
    public String part1() {
        int robotX = 0, robotY = 0;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(data[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                }
            }
        }
        int oldRX=robotX;
        int oldRY=robotY;
        for(int i = 0; i<move.length(); i++) {
            char mv = move.charAt(i);
            try {
                System.out.println();
                System.out.println("Move: "+mv+":");
                if (mv == '^' && canMoveUp(robotX, robotY)) {
                    moveUp(robotX, robotY);
                    --robotY;
                } else if (mv == 'v' && canMoveDown(robotX, robotY)) {
                    moveDown(robotX, robotY);
                    ++robotY;
                } else if (mv == '<' && canMoveLeft(robotX, robotY)) {
                    moveLeft(robotX, robotY);
                    --robotX;
                } else if (mv == '>' && canMoveRight(robotX, robotY)) {
                    moveRight(robotX, robotY);
                    ++robotX;
                }
                if(data[robotY][robotX] != '@') {
                    throw new IllegalStateException("Illegal move: " + mv+" at "+i);
                }
                for(int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        if((x!=robotX || y!=robotY) && data[y][x] == '@') {
                            throw new IllegalStateException("Illegal robotPosition: " + mv+" at "+i+" x "+x+" y "+y);
                        }
                    }
                }
                //printDat();
                System.out.println("next: "+(i<move.length()-1?move.charAt(i+1):' '));
                if(oldRX == robotX && oldRY == robotY) {
                    System.out.println("STUCK! "+mv+"\t"+i+"\tx "+robotX+"\ty "+robotY);
                    //System.in.read();
                }
                //System.in.read();
                //Thread.sleep(100);

                System.out.println();
                System.out.println();
                System.out.print("\033[H\033[2J");
                System.out.flush();
                if(Math.abs(oldRX+oldRY - (robotX + robotY)) > 1) {
                    System.out.println("something is flaky");
                    throw new RuntimeException("something is flaky");
                }
                oldRX = robotX;
                oldRY = robotY;
            } catch (Exception e) {
                System.out.println("mv: "+mv);
                System.out.println("i: "+i);
                throw new RuntimeException(e);
            }
        }
        long sum = 0L;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                System.out.print(data[y][x]);
                if(data[y][x] == '[') {
                    sum += (100L*y+x);
                }
            }
            System.out.println();
        }
        return ""+sum;
    }

    private List<String> toLines() {
        List<String> lines = new ArrayList<>();
        for(int y=0; y<height; y++) {
            StringBuilder sb = new StringBuilder();
            for(int x=0; x<width; x++) {
                sb.append(data[y][x]);
            }
            lines.add(sb.toString());
        }
        return lines;
    }

    private boolean canMoveUp(int x, int y) {
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for(int iY = y-1; iY<height; --iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for(int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if(!allSpaces) {
                boolean anyWall = false;
                for(int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if(!anyWall) {
                    SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
                    for(int mx : prevMoves) {
                        if(data[iY][mx] == '[') {
                            nextmoves.add(mx);
                            nextmoves.add(mx+1);
                        } else if(data[iY][mx] == ']') {
                            nextmoves.add(mx-1);
                            nextmoves.add(mx);
                        }
                    }
                    wantsToMove.addLast(new ArrayList<>(nextmoves));
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void moveUp(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move");
        }
        boolean canStop = false;
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for(int iY = y-1; iY<height && !canStop; --iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for(int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if(!allSpaces) {
                boolean anyWall = false;
                for(int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if(!anyWall) {
                    SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
                    for(int mx : prevMoves) {
                        if(data[iY][mx] == '[') {
                            nextmoves.add(mx);
                            nextmoves.add(mx+1);
                        } else if(data[iY][mx] == ']') {
                            nextmoves.add(mx-1);
                            nextmoves.add(mx);
                        }
                    }
                    wantsToMove.addLast(new ArrayList<>(nextmoves));
                } else {
                    return;
                }
            } else {
                canStop = true;
            }
        }
        int iY = y;
        Map<Integer, Character> prevLine = new HashMap<>();
        Map<Integer, Character> currentLine = new HashMap<>();
        prevLine.put(x, '.');
        for(var toMove : wantsToMove) {
            currentLine = new HashMap<>();
            for(int mx: toMove) {
                currentLine.put(mx, data[iY][mx]);
                data[iY][mx] = prevLine.getOrDefault(mx, '.');
            }
            for(int mx: prevLine.keySet()) {
                data[iY][mx] = prevLine.get(mx);
            }
            prevLine = currentLine;
            --iY;
        }
        for(int mx: prevLine.keySet()) {
            data[iY][mx] = prevLine.get(mx);
        }
    }

    private List<Character> copyLine(int fromX, int toX, int y) {
        List<Character> result = new ArrayList<>();
        for(int x=fromX; x<=toX; ++x) {
            result.add(data[y][x]);
        }
        return result;
    }

    private void putLine(int fromX, int toX, int y, List<Character> line) {
        int lastEdited = -1;
        for(int i=0; i<line.size(); ++i) {
            data[y][fromX+i] = line.get(i);
            lastEdited = fromX+i;
        }
        if(lastEdited != toX) {
            throw new IllegalStateException("Illegal put");
        }
    }

    private boolean canMoveDown(int x, int y) {
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for(int iY = y+1; iY<height; ++iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for(int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if(!allSpaces) {
                boolean anyWall = false;
                for(int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if(!anyWall) {
                    SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
                    for(int mx : prevMoves) {
                        if(data[iY][mx] == '[') {
                            nextmoves.add(mx);
                            nextmoves.add(mx+1);
                        } else if(data[iY][mx] == ']') {
                            nextmoves.add(mx-1);
                            nextmoves.add(mx);
                        }
                    }
                    wantsToMove.addLast(new ArrayList<>(nextmoves));
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void moveDown(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move");
        }
        boolean canStop = false;
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for(int iY = y+1; iY<height && !canStop; ++iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for(int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if(!allSpaces) {
                boolean anyWall = false;
                for(int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if(!anyWall) {
                    SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
                    for(int mx : prevMoves) {
                        if(data[iY][mx] == '[') {
                            nextmoves.add(mx);
                            nextmoves.add(mx+1);
                        } else if(data[iY][mx] == ']') {
                            nextmoves.add(mx-1);
                            nextmoves.add(mx);
                        }
                    }
                    wantsToMove.addLast(new ArrayList<>(nextmoves));
                } else {
                    return;
                }
            } else {
                canStop = true;
            }
        }
        int iY = y;
        Map<Integer, Character> prevLine = new HashMap<>();
        Map<Integer, Character> currentLine = new HashMap<>();
        prevLine.put(x, '.');
        for(var toMove : wantsToMove) {
            currentLine = new HashMap<>();
            for(int mx: toMove) {
                currentLine.put(mx, data[iY][mx]);
                data[iY][mx] = prevLine.getOrDefault(mx, '.');
            }
            for(int mx: prevLine.keySet()) {
                data[iY][mx] = prevLine.get(mx);
            }
            prevLine = currentLine;
            ++iY;
        }
        for(int mx: prevLine.keySet()) {
            data[iY][mx] = prevLine.get(mx);
        }
    }

    private boolean canMoveLeft(int x, int y) {
        for(int iX = x-1; iX>=0; --iX) {
            if(data[y][iX] == '.') {
                return true;
            } else if(data[y][iX] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveLeft(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move");
        }
        int firstSpace = -1;
        int iX = x;
        while (firstSpace == -1) {
            if(data[y][iX] == '.') {
                firstSpace = iX;
            } else {
                --iX;
            }
        }
        iX = firstSpace;
        for(; iX<x; ++iX) {
            char tmp = data[y][iX];
            data[y][iX] = data[y][iX+1];
            data[y][iX+1] = tmp;
        }
    }

    private boolean canMoveRight(int x, int y) {
        for(int iX = x+1; iX<width; ++iX) {
            if(data[y][iX] == '.') {
                return true;
            } else if(data[y][iX] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveRight(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move "+x+", "+y+" = "+data[y][x]);
        }
        int firstSpace = width;
        int iX = x;
        while (firstSpace == width) {
            if(data[y][iX] == '.') {
                firstSpace = iX;
            } else {
                ++iX;
            }
        }
        iX = firstSpace;
        for(; iX>x; --iX) {
            char tmp = data[y][iX];
            data[y][iX] = data[y][iX-1];
            data[y][iX-1] = tmp;
        }
    }

    @Override
    public String part2() {
        return "";
    }

    private void printDat() {
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                System.out.print(data[y][x]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        String input = IOUtil.readInput(15);
        Day15w day15w = new Day15w(input); //EXAMPLE1);//EXAMPLE3);//input);//EXAMPLE2);//EXAMPLE1);//input);
        System.out.println(day15w.part1());
        System.out.println(day15w.part2());
    }

    private static final String EXAMPLE1 = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########
            
            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""";

    private static final String EXAMPLE2 = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
            
            <^^>>>vv<v>>v<<""";

    private static final String EXAMPLE3 = """
            #######
            #...#.#
            #.....#
            #..OO@#
            #..O..#
            #.....#
            #######
            
            <vv<<^^<<^^""";
}
