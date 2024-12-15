package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.Arrays;

public class Day15 extends AbstractDay {
    private final char[][] data;
    private final String move;
    private final int width;
    private final int height;
    public Day15(String input) {
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
                sb.append(s);
                if(data == null) {
                    sb.append('\n');
                }
            }
        }
        move = sb.toString();
        this.data = data;
        height = data.length;
        width = data[0].length;
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
                System.out.print(data[y][x]);
                if(data[y][x] == 'O') {
                    sum += (100L*y+x);
                }
            }
            System.out.println();
        }
        return ""+sum;
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
        return "";
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
