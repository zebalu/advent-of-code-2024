package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 extends AbstractDay {
    private static final int WIDTH = 101;
    private static final int HEIGHT = 103;

    public Day14() {
        this(IOUtil.readInput(14));
    }

    public Day14(String input) {
        super(input, "Restroom Redoubt", 14);
    }

    @Override
    public String part1() {
        List<Robot> robots = INPUT.lines().map(Robot::fromString).toList();
        for (int i = 0; i < 100; ++i) {
            robots.forEach(Robot::move);
        }
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;
        for (Robot robot : robots) {
            switch (robot.getQuadrant()) {
                case 1 -> q1++;
                case 2 -> q2++;
                case 3 -> q3++;
                case 4 -> q4++;
            }
        }
        int safetyScore = q1 * q2 * q3 * q4;
        return Integer.toString(safetyScore);
    }

    @Override
    public String part2() {
        record Coord(int x, int y) {
            static Coord fromRobot(Robot robot) {
                return new Coord(robot.x, robot.y);
            }
        }
        List<Robot> robots = INPUT.lines().map(Robot::fromString).toList();
        int stepcount = 0;
        boolean noCollisionDetected;
        do {
            ++stepcount;
            Set<Coord> positions = new HashSet<>();
            boolean alreadyCollided = false;
            for (Robot robot : robots) {
                robot.move();
                if(!alreadyCollided) {
                    alreadyCollided = !positions.add(Coord.fromRobot(robot));
                }
            }
            noCollisionDetected = !alreadyCollided;
        } while (!noCollisionDetected);
        return Integer.toString(stepcount);
    }

    private static class Robot {
        static final Pattern numberpattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

        private int x;
        private int y;
        private final int vx;
        private final int vy;


        public Robot(int x, int y, int vx, int vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void move() {
            x = (x + vx + WIDTH) % WIDTH;
            y = (y + vy + HEIGHT) % HEIGHT;
        }

        int getQuadrant() {
            int hMid = WIDTH / 2;
            int vMid = HEIGHT / 2;
            if (x < hMid) {
                if (y < vMid) {
                    return 1;
                } else if (y > vMid) {
                    return 2;
                }
            } else if (x > hMid) {
                if (y > vMid) {
                    return 3;
                } else if (y < vMid) {
                    return 4;
                }
            }
            return 0;
        }

        static Robot fromString(String line) {
            Matcher matcher = numberpattern.matcher(line);
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int vx = Integer.parseInt(matcher.group(3));
                int vy = Integer.parseInt(matcher.group(4));
                return new Robot(x, y, vx, vy);
            }
            throw new IllegalArgumentException("Can read line: " + line);
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(14);
        Day14 day14 = new Day14(input);
        System.out.println(day14.part1());
        System.out.println(day14.part2());
    }
}
