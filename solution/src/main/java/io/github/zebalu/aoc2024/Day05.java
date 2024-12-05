package io.github.zebalu.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day05 extends AbstractDay {
    private final List<Rule> rules;
    private final List<List<Integer>> printQueues;
    public Day05() {
        super("Print Queue", 5);
        List<String> ruleLines = new ArrayList<>();
        List<String> queueLines = new ArrayList<>();
        boolean splitFound = false;
        var allLines = INPUT.lines().toList();
        for (var line : allLines) {
            if(line.isBlank()) {
                splitFound = true;
            } else if(splitFound) {
                queueLines.add(line);
            } else {
                ruleLines.add(line);
            }
        }
        rules = ruleLines.stream().map(Rule::from).toList();
        printQueues = queueLines.stream().map(l->l.split(",")).map(ss->Arrays.stream(ss).map(Integer::parseInt).toList()).toList();
    }

    @Override
    public String part1() {
        return Integer.toString(printQueues.stream().filter(this::isValid).mapToInt(q->q.get(q.size()/2)).sum());

    }

    private boolean isValid(List<Integer> printQueue) {
        for (Rule rule : rules) {
            int fIdx = printQueue.indexOf(rule.first);
            int sIdx = printQueue.indexOf(rule.second);
            if (0 <= fIdx && 0 <= sIdx && sIdx < fIdx) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String part2() {
        var invalids = printQueues.stream().filter(q->!isValid(q)).map(ArrayList::new).toList();
        invalids.forEach(this::fixInvalid);
        return Integer.toString(invalids.stream().mapToInt(q->q.get(q.size()/2)).sum());
    }

    private void fixInvalid(List<Integer> printQueue) {
        int i = 0;
        while (i < rules.size()) {
            Rule rule = rules.get(i);
            int fIdx = printQueue.indexOf(rule.first);
            int sIdx = printQueue.indexOf(rule.second);
            if (0 <= fIdx && 0 <= sIdx && sIdx < fIdx) {
                swap(printQueue, fIdx, sIdx);
                i = 0;
            } else {
                ++i;
            }
        }
    }

    private void swap(List<Integer> printQueue, int idx1, int idx2) {
        int tmp = printQueue.get(idx1);
        printQueue.set(idx1, printQueue.get(idx2));
        printQueue.set(idx2, tmp);
    }

    private record Rule(int first, int second) {
        static Rule from(String rulDesc) {
            String[] parts = rulDesc.split("\\|");
            return new Rule(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    public static void main(String[] args) {
        Day05 day05 = new Day05();
        System.out.println(day05.part1());
        System.out.println(day05.part2());
    }

}
