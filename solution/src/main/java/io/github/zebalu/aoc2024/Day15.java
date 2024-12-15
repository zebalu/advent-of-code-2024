package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class Day15 extends AbstractDay {
    private char[][] data;
    private final String move;
    private final String gridString;
    private final List<List<Integer>> wantsToMove = new ArrayList<>();
    private int width;
    private int height;
    private int robotX;
    private int robotY;

    private final IntUnaryOperator GROWING = i -> i + 1;
    private final IntUnaryOperator SHRINKING = i -> i - 1;

    public Day15() {
        this(IOUtil.readInput(15));
    }

    public Day15(String input) {
        super(input, "Warehouse Woes", 15);
        List<List<String>> blocks = IOUtil.groupByEmptyLines(INPUT);
        gridString = String.join("\n", blocks.getFirst());
        move = String.join("", blocks.getLast());
    }

    @Override
    public String part1() {
        createDataFrom(gridString);
        for (int i = 0; i < move.length(); i++) {
            applyMove(move.charAt(i));
        }
        long sum = getGPS('O');
        return Long.toString(sum);
    }

    @Override
    public String part2() {
        createDataFrom(gridString.lines().map(this::expand).collect(Collectors.joining("\n")));
        for (int i = 0; i < move.length(); i++) {
            applyMove(move.charAt(i));
        }
        long sum = getGPS('[');
        return Long.toString(sum);
    }

    private void applyMove(char mv) {
        if (mv == '^' && canMoveUpP2()) {
            moveUpP2();
            --robotY;
        } else if (mv == 'v' && canMoveDownP2()) {
            moveDownP2();
            ++robotY;
        } else if (mv == '<' && canMoveLeft()) {
            moveLeft();
            --robotX;
        } else if (mv == '>' && canMoveRight()) {
            moveRight();
            ++robotX;
        }
    }

    private boolean canMoveLeft() {
        return findFirstHorizontalSpace(SHRINKING) > -1;
    }

    private void moveLeft() {
        horizontalCopy(SHRINKING);
    }

    private void horizontalCopy(IntUnaryOperator direction) {
        IntUnaryOperator copyDirection = direction == SHRINKING ? GROWING : SHRINKING;
        int iX = findFirstHorizontalSpace(direction);
        for (; iX != robotX; iX = copyDirection.applyAsInt(iX)) {
            int tX = copyDirection.applyAsInt(iX);
            char tmp = data[robotY][iX];
            data[robotY][iX] = data[robotY][tX];
            data[robotY][tX] = tmp;
        }
    }

    private int findFirstHorizontalSpace(IntUnaryOperator direction) {
        int iX = robotX;
        while (0 < iX && iX < width) {
            if (data[robotY][iX] == '.') {
                return iX;
            } else if (data[robotY][iX] == '#') {
                return -1;
            } else {
                iX = direction.applyAsInt(iX);
            }
        }
        return -1;
    }

    private boolean canMoveRight() {
        return findFirstHorizontalSpace(GROWING) > -1;
    }

    private void moveRight() {
        horizontalCopy(GROWING);
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
            switch (c) {
                case '#' -> sb.append("##");
                case 'O' -> sb.append("[]");
                case '.' -> sb.append("..");
                case '@' -> sb.append("@.");
            }
        }
        return sb.toString();
    }

    private boolean canMoveUpP2() {
        return canMoveDirection(SHRINKING);
    }

    private Optional<Boolean> checkVerticalMoves(int y) {
        boolean allSpaces = wantsToMove.getLast().stream().allMatch(mx -> data[y][mx] == '.');
        if (!allSpaces) {
            boolean anyWall = wantsToMove.getLast().stream().anyMatch(mx -> data[y][mx] == '#');
            if (!anyWall) {
                wantsToMove.addLast(new ArrayList<>(findNextVerticalMoves(y)));
                return Optional.empty();
            } else {
                return Optional.of(false);
            }
        } else {
            return Optional.of(true);
        }
    }

    private void moveUpP2() {
        copyLines(SHRINKING);
    }

    private void copyLines(IntUnaryOperator direction) {
        int iY = robotY;
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

    private boolean canMoveDownP2() {
        return canMoveDirection(GROWING);
    }

    private boolean canMoveDirection(IntUnaryOperator direction) {
        wantsToMove.clear();
        wantsToMove.add(List.of(robotX));
        int iY = direction.applyAsInt(robotY);
        Optional<Boolean> canMove = Optional.empty();
        while (0 < iY && iY < height && canMove.isEmpty()) {
            canMove = checkVerticalMoves(iY);
            iY = direction.applyAsInt(iY);
        }
        return canMove.orElse(false);
    }

    private SequencedSet<Integer> findNextVerticalMoves(int iY) {
        List<Integer> prevMoves = wantsToMove.getLast();
        SequencedSet<Integer> nextmoves = new LinkedHashSet<>();
        for (int mx : prevMoves) {
            if (data[iY][mx] == ']') {
                nextmoves.add(mx - 1);
            }
            if (data[iY][mx] != '.') {
                nextmoves.add(mx);
            }
            if (data[iY][mx] == '[') {
                nextmoves.add(mx + 1);
            }
        }
        return nextmoves;
    }

    private void moveDownP2() {
        copyLines(GROWING);
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(15);
        Day15 day15 = new Day15(input);
        System.out.println(day15.part1());
        System.out.println(day15.part2());
    }
}
