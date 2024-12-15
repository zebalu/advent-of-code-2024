package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class Day15 extends AbstractDay {
    private char[][] data;
    private final String move;
    private final String gridString;
    private int width;
    private int height;
    private int robotX;
    private int robotY;

    public Day15() {
        this(IOUtil.readInput(15));
    }

    public Day15(String input) {
        super(input, "Warehouse Woes", 15);
        StringBuilder sb = new StringBuilder();
        String gs = null;
        for (String s : INPUT.lines().toList()) {
            if (s.isBlank()) {
                if (gs == null) {
                    gs = sb.toString();
                    sb.setLength(0);
                }
            } else {
                sb.append(s);
                if (gs == null) {
                    sb.append('\n');
                }
            }
        }
        gridString = gs;
        move = sb.toString();
    }

    @Override
    public String part1() {
        createDataFrom(gridString);
        for (int i = 0; i < move.length(); i++) {
            applyMove(move.charAt(i), true);
        }
        long sum = getGPS('O');
        return Long.toString(sum);
    }

    @Override
    public String part2() {
        createDataFrom(gridString.lines().map(this::expand).collect(Collectors.joining("\n")));
        for (int i = 0; i < move.length(); i++) {
            applyMove(move.charAt(i), false);
        }
        long sum = getGPS('[');
        return Long.toString(sum);
    }

    private void applyMove(char mv, boolean part1) {
        BiFunction<Integer, Integer, Boolean> checkUp = part1 ? this::canMoveUp : this::canMoveUpP2;
        BiConsumer<Integer, Integer> doMoveUp = part1 ? this::moveUp : this::moveUpP2;
        BiFunction<Integer, Integer, Boolean> checkDown = part1 ? this::canMoveDown : this::canMoveDownP2;
        BiConsumer<Integer, Integer> doMoveDown = part1 ? this::moveDown : this::moveDownP2;
        if (mv == '^' && checkUp.apply(robotX, robotY)) {
            doMoveUp.accept(robotX, robotY);
            --robotY;
        } else if (mv == 'v' && checkDown.apply(robotX, robotY)) {
            doMoveDown.accept(robotX, robotY);
            ++robotY;
        } else if (mv == '<' && canMoveLeft(robotX, robotY)) {
            moveLeft(robotX, robotY);
            --robotX;
        } else if (mv == '>' && canMoveRight(robotX, robotY)) {
            moveRight(robotX, robotY);
            ++robotX;
        }
    }

    private boolean canMoveUp(int x, int y) {
        for (int iY = y - 1; iY > 0; iY--) {
            if (data[iY][x] == '.') {
                return true;
            } else if (data[iY][x] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveUp(int x, int y) {
        chekcRobotOn(x, y);
        int firstSpace = -1;
        int iY = y;
        while (firstSpace == -1 && iY > 0) {
            if (data[iY][x] == '.') {
                firstSpace = iY;
            } else {
                --iY;
            }
        }
        iY = firstSpace;
        for (; iY < y; ++iY) {
            char tmp = data[iY][x];
            data[iY][x] = data[iY + 1][x];
            data[iY + 1][x] = tmp;
        }
    }

    private boolean canMoveDown(int x, int y) {
        for (int iY = y + 1; iY < height; ++iY) {
            if (data[iY][x] == '.') {
                return true;
            } else if (data[iY][x] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveDown(int x, int y) {
        chekcRobotOn(x, y);
        int firstSpace = height;
        int iY = y;
        while (firstSpace == height && iY < height) {
            if (data[iY][x] == '.') {
                firstSpace = iY;
            } else {
                ++iY;
            }
        }
        iY = firstSpace;
        for (; iY > y; --iY) {
            char tmp = data[iY][x];
            data[iY][x] = data[iY - 1][x];
            data[iY - 1][x] = tmp;
        }
    }

    private boolean canMoveLeft(int x, int y) {
        for (int iX = x - 1; iX >= 0; --iX) {
            if (data[y][iX] == '.') {
                return true;
            } else if (data[y][iX] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveLeft(int x, int y) {
        chekcRobotOn(x, y);
        int firstSpace = -1;
        int iX = x;
        while (firstSpace == -1) {
            if (data[y][iX] == '.') {
                firstSpace = iX;
            } else {
                --iX;
            }
        }
        iX = firstSpace;
        for (; iX < x; ++iX) {
            char tmp = data[y][iX];
            data[y][iX] = data[y][iX + 1];
            data[y][iX + 1] = tmp;
        }
    }

    private void chekcRobotOn(int x, int y) {
        if (data[y][x] != '@') {
            throw new IllegalArgumentException("Illegal move, robot is not on x: " + x + ", y: " + y);
        }
    }

    private boolean canMoveRight(int x, int y) {
        for (int iX = x + 1; iX < width; ++iX) {
            if (data[y][iX] == '.') {
                return true;
            } else if (data[y][iX] == '#') {
                return false;
            }
        }
        return false;
    }

    private void moveRight(int x, int y) {
        chekcRobotOn(x, y);
        int firstSpace = width;
        int iX = x;
        while (firstSpace == width && iX < width) {
            if (data[y][iX] == '.') {
                firstSpace = iX;
            } else {
                ++iX;
            }
        }
        iX = firstSpace;
        for (; iX > x; --iX) {
            char tmp = data[y][iX];
            data[y][iX] = data[y][iX - 1];
            data[y][iX - 1] = tmp;
        }
    }

    private void createDataFrom(String grid) {
        data = IOUtil.readCharGrid(grid);
        height = data.length;
        width = data[0].length;
        boolean found = false;
        for (int y = 0; y < height && !found; y++) {
            for (int x = 0; x < width && !found; x++) {
                if (data[y][x] == '@') {
                    robotX = x;
                    robotY = y;
                    found = true;
                }
            }
        }
        if (!found) {
            throw new IllegalStateException("Robot can not be found");
        }
    }

    private long getGPS(char sumChar) {
        long sum = 0L;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (data[y][x] == sumChar) {
                    sum += (100L * y + x);
                }
            }
        }
        return sum;
    }

    private String expand(String line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '#') {
                sb.append("##");
            } else if (c == 'O') {
                sb.append("[]");
            } else if (c == '.') {
                sb.append("..");
            } else if (c == '@') {
                sb.append("@.");
            } else {
                throw new IllegalArgumentException("invalid character: " + c);
            }
        }
        return sb.toString();
    }

    private boolean canMoveUpP2(int x, int y) {
        return canMoveDirection(x, y, i -> i - 1);
    }

    private Optional<Boolean> checkMoves(int y, List<Integer> prevMoves, List<List<Integer>> wantsToMove) {
        boolean allSpaces = prevMoves.stream().allMatch(mx -> data[y][mx] == '.');
        if (!allSpaces) {
            boolean anyWall = prevMoves.stream().anyMatch(mx -> data[y][mx] == '#');
            if (!anyWall) {
                wantsToMove.addLast(new ArrayList<>(findNextMoves(prevMoves, y)));
            } else {
                return Optional.of(false);
            }
        } else {
            return Optional.of(true);
        }
        return Optional.empty();
    }

    private void moveUpP2(int x, int y) {
        chekcRobotOn(x, y);
        boolean canStop = false;
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for (int iY = y - 1; iY < height && !canStop; --iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for (int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if (!allSpaces) {
                boolean anyWall = false;
                for (int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if (!anyWall) {
                    SequencedSet<Integer> nextmoves = findNextMoves(prevMoves, iY);
                    wantsToMove.addLast(new ArrayList<>(nextmoves));
                } else {
                    return;
                }
            } else {
                canStop = true;
            }
        }
        copyLines(wantsToMove, y, i -> i - 1);
    }

    private void copyLines(List<List<Integer>> wantsToMove, int y, IntUnaryOperator direction) {
        int iY = y;
        Map<Integer, Character> prevLine = new HashMap<>();
        Map<Integer, Character> currentLine;
        for (var toMove : wantsToMove) {
            currentLine = new HashMap<>();
            for (int mx : toMove) {
                currentLine.put(mx, data[iY][mx]);
                data[iY][mx] = prevLine.getOrDefault(mx, '.');
            }
            for (int mx : prevLine.keySet()) {
                data[iY][mx] = prevLine.get(mx);
            }
            prevLine = currentLine;
            iY = direction.applyAsInt(iY);
        }
        for (int mx : prevLine.keySet()) {
            data[iY][mx] = prevLine.get(mx);
        }
    }

    private boolean canMoveDownP2(int x, int y) {
        return canMoveDirection(x, y, i -> i + 1);
    }

    private boolean canMoveDirection(int x, int y, IntUnaryOperator direction) {
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        int iY = direction.applyAsInt(y);
        while (0 < iY && iY < height) {
            Optional<Boolean> canMove = checkMoves(iY, wantsToMove.getLast(), wantsToMove);
            if (canMove.isPresent()) {
                return canMove.get();
            }
            iY = direction.applyAsInt(iY);
        }
        return false;
    }

    private SequencedSet<Integer> findNextMoves(List<Integer> prevMoves, int iY) {
        SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
        for (int mx : prevMoves) {
            if (data[iY][mx] == '[') {
                nextmoves.add(mx);
                nextmoves.add(mx + 1);
            } else if (data[iY][mx] == ']') {
                nextmoves.add(mx - 1);
                nextmoves.add(mx);
            }
        }
        return nextmoves;
    }

    private void moveDownP2(int x, int y) {
        chekcRobotOn(x, y);
        boolean canStop = false;
        List<List<Integer>> wantsToMove = new ArrayList<>();
        wantsToMove.add(List.of(x));
        for (int iY = y + 1; iY < height && !canStop; ++iY) {
            boolean allSpaces = true;
            List<Integer> prevMoves = wantsToMove.getLast();
            for (int mx : prevMoves) {
                allSpaces &= data[iY][mx] == '.';
            }
            if (!allSpaces) {
                boolean anyWall = false;
                for (int mx : prevMoves) {
                    anyWall |= data[iY][mx] == '#';
                }
                if (!anyWall) {
                    wantsToMove.addLast(new ArrayList<>(findNextMoves(prevMoves, iY)));
                } else {
                    return;
                }
            } else {
                canStop = true;
            }
        }
        copyLines(wantsToMove, y, i -> i + 1);
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(15);
        Day15 day15 = new Day15(input);
        System.out.println(day15.part1());
        System.out.println(day15.part2());
    }
}
