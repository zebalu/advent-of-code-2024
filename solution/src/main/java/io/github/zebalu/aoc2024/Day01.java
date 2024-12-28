package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01 extends AbstractDay {
    private final int[] list1;
    private final int[] list2;

    public Day01() {
        this(IOUtil.readInput(1));
    }

    public Day01(String input) {
        super(input, "Historian Hysteria", 1);
        List<String> lines = new ArrayList<>();
        for(var line: INPUT.split("\n")) {
            lines.add(line.trim());
        }
        //var lines = INPUT.lines().toList();
        list1 = new int[lines.size()];
        list2 = new int[lines.size()];
        Pattern pattern = Pattern.compile("(\\d+)\\W+(\\d+)");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                list1[i] = Integer.parseInt(matcher.group(1));
                list2[i] = Integer.parseInt(matcher.group(2));
            }
        }
        Arrays.sort(list1);
        Arrays.sort(list2);
    }

    @Override
    public String part1() {
        long sum = 0L;
        for (int i = 0; i < list1.length; i++) {
            sum += Math.abs(list1[i] - list2[i]);
        }
        return Long.toString(sum);
    }

    @Override
    public String part2() {
        long sum = 0L;
        int i = 0;
        int j = 0;
        while (i < list1.length && j < list2.length) {
            if (list1[i] == list2[j]) {
                long num = list1[i];
                int count1 = 0;
                while (i < list1.length && list1[i] == num) {
                    ++count1;
                    ++i;
                }
                int count2 = 0;
                while (j < list2.length && list2[j] == num) {
                    ++count2;
                    ++j;
                }
                sum += num * count1 * count2;
            } else if (list1[i] < list2[j]) {
                ++i;
            } else if (list1[i] > list2[j]) {
                ++j;
            }
        }
        return Long.toString(sum);
    }

    public static void main(String[] args) {
        AbstractDay day01 = new Day01();
        System.out.println(day01.part1());
        System.out.println(day01.part2());
    }
}
