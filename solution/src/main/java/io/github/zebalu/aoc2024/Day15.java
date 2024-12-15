package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day15 extends AbstractDay {
    private char[][] data;
    private final String move;
    private final String gridString;
    private int width;
    private int height;
    public Day15(String input) {
        super(input, "Warehouse Woes", 15);
        StringBuilder sb = new StringBuilder();
        String gs = null;
        for (String s : INPUT.lines().toList()) {
            if(s.isBlank()) {
                if(gs == null) {
                    gs = sb.toString();
                    sb.setLength(0);
                }
            } else {
                sb.append(s);
                if(gs == null) {
                    sb.append('\n');
                }
            }
        }
        gridString = gs;
        move = sb.toString();
    }

    @Override
    public String part1() {
        data = IOUtil.readCharGrid(gridString);
        height = data.length;
        width = data[0].length;
        int robotX = 0, robotY = 0;
        boolean found = false;
        for(int y=0; y<height && !found; y++) {
            for(int x=0; x<width && !found; x++) {
                if(data[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                    found = true;
                }
            }
        }
        for(int i = 0; i<move.length(); i++) {
            char mv = move.charAt(i);
            try {
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
            } catch (Exception e) {
                System.out.println("mv: "+mv);
                System.out.println("i: "+i);
                throw e;
            }
        }
        long sum = 0L;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(data[y][x] == 'O') {
                    sum += (100L*y+x);
                }
            }
        }
        return Long.toString(sum);
    }

    private boolean canMoveUp(int x, int y) {
        for(int iY = y-1; iY>0; iY--) {
            if(data[iY][x] == '.') {
                return true;
            } else if(data[iY][x] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveUp(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move");
        }
        int firstSpace = -1;
        int iY = y;
        while (firstSpace == -1) {
            if(data[iY][x] == '.') {
                firstSpace = iY;
            } else {
                --iY;
            }
        }
        iY = firstSpace;
        for(; iY<y; ++iY) {
            char tmp = data[iY][x];
            data[iY][x] = data[iY+1][x];
            data[iY+1][x] = tmp;
        }
    }

    private boolean canMoveDown(int x, int y) {
        for (int iY = y + 1; iY < height; ++iY) {
            if (data[iY][x] == '.') {
                return true;
            } else if(data[iY][x] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveDown(int x, int y) {
        if(data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move");
        }
        int firstSpace = height;
        int iY = y;
        while (firstSpace == height) {
            if(data[iY][x] == '.') {
                firstSpace = iY;
            } else {
                ++iY;
            }
        }
        iY = firstSpace;
        for(; iY>y; --iY) {
            char tmp = data[iY][x];
            data[iY][x] = data[iY-1][x];
            data[iY-1][x] = tmp;
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
        data = IOUtil.readCharGrid(gridString.lines().map(this::expand).collect(Collectors.joining("\n")));
        height = data.length;
        width = data[0].length;
        int robotX = 0, robotY = 0;
        boolean found = false;
        for(int y=0; y<height && !found; y++) {
            for(int x=0; x<width && !found; x++) {
                if(data[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                    found = true;
                }
            }
        }
        for(int i = 0; i<move.length(); i++) {
            char mv = move.charAt(i);
            try {
                if (mv == '^' && canMoveUpP2(robotX, robotY)) {
                    moveUpP2(robotX, robotY);
                    --robotY;
                } else if (mv == 'v' && canMoveDownP2(robotX, robotY)) {
                    moveDownP2(robotX, robotY);
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
            } catch (Exception e) {
                System.out.println("mv: "+mv);
                System.out.println("i: "+i);
                throw e;
            }
        }
        long sum = 0L;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(data[y][x] == '[') {
                    sum += (100L*y+x);
                }
            }
        }
        return Long.toString(sum);
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

    private boolean canMoveUpP2(int x, int y) {
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

    private void moveUpP2(int x, int y) {
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

    private boolean canMoveDownP2(int x, int y) {
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

    private void moveDownP2(int x, int y) {
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

    public static void main(String[] args) {
        String input = IOUtil.readInput(15);
        Day15 day15 = new Day15(input);//EXAMPLE2);//EXAMPLE1);//input);
        System.out.println(day15.part1());
        System.out.println(day15.part2());
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
}
