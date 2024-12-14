package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 extends AbstractDay {
    public Day14() {
        this(IOUtil.readInput(14));
    }

    public Day14(String input) {
        super(input, "Restroom Redoubt", 14);
    }

    @Override
    public String part1() {
        int width = 101;
        int height = 103;
        List<Robot> robots = INPUT.lines().map(l->Robot.fromString(l, width, height)).toList();
        for(int i=0; i<100; ++i) {
            robots.forEach(Robot::move);
        }
        int q0 = 0;
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;
        for(Robot robot: robots) {
            switch (robot.getQuadrant()) {
                case 0 -> q0++;
                case 1 -> q1++;
                case 2 -> q2++;
                case 3 -> q3++;
                case 4 -> q4++;
                default -> throw new IllegalStateException("Can not be in " + robot.getQuadrant());
            }
        }
        int safetyScore = q1*q2*q3*q4;
        return ""+safetyScore;
    }

    @Override
    public String part2() {
        int width = 101;
        int height = 103;
        List<Robot> robots = INPUT.lines().map(l->Robot.fromString(l, width, height)).toList();
        boolean noCollisionDetected = robots.stream().map(Coord::fromRobot).collect(Collectors.toSet()).size() == robots.size();
        int stepcount = 0;
        do {
            ++stepcount;
            Set<Coord> positions = new HashSet<>();
            for(Robot robot: robots) {
                robot.move();
                positions.add(Coord.fromRobot(robot));
            }
            noCollisionDetected = robots.size() == positions.size();
        } while (!noCollisionDetected);
        print(robots);
        return ""+stepcount;
    }
    private void print(List<Robot> robots) {
        record Coord(int x, int y) {}
        Map<Coord, Robot> map = new HashMap<>();
        robots.forEach(robot->{
            map.put(new Coord(robot.x, robot.y), robot);
        });
        System.out.println("-".repeat(101));
        for(int y=0; y<103; ++y) {
            for(int x=0; x<101; ++x) {
                Coord coord = new Coord(x, y);
                if(map.containsKey(coord)) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println("-".repeat(101));
    }

    private record Coord(int x, int y) {
        List<Record> neighbours() {
            return List.of(new Coord(x-1, y), new Coord(x+1, y), new Coord(x, y+1), new Coord(x, y-1));
        }
        static Coord fromRobot(Robot robot) {
            return new Coord(robot.x, robot.y);
        }
        int countNeighbours(Set<Coord> neighbours) {
            return (int)neighbours().stream().filter(neighbours::contains).count();
        }
    }

    private static class Robot {
        static final Pattern numberpattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

        private int x;
        private int y;
        private final int vx;
        private final int vy;
        private final int width;
        private final int height;


        public Robot(int x, int y, int vx, int vy, int width, int height) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.width = width;
            this.height = height;
        }

        void move() {
            //System.out.print(String.format("((%3d, %3d), (%3d,%3d)) --> ", x,y,vx,vy));
            x = (x + vx + width) % width;
            y = (y + vy + height) % height;
            //System.out.println(String.format("((%3d, %3d), (%3d,%3d))", x,y,vx,vy));
        }

        int getQuadrant() {
            if(x<width/2) {
                if(y<height/2) {
                    return 1;
                } else if (y > height/2) {
                    return 2;
                }
            } else if(x>width/2) {
                if(y>height/2) {
                    return 3;
                } else if (y < height/2) {
                    return 4;
                }
            }
            return 0;
        }

        static Robot fromString(String line, int width, int height) {
            Matcher matcher = numberpattern.matcher(line);
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int vx = Integer.parseInt(matcher.group(3));
                int vy = Integer.parseInt(matcher.group(4));
                return new Robot(x, y, vx, vy, width, height);
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
