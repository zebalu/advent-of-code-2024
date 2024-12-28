package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day05 extends AbstractDay {
    private final List<Rule> rules = new ArrayList<>();
    private final List<PrintQueue> printQueues = new ArrayList<>();
    public Day05() {
        this(IOUtil.readInput(5));
    }
    public Day05(String input) {
        super(input,"Print Queue", 5);
        boolean splitFound = false;
        var allLines = INPUT.lines().toList();
        for (var line : allLines) {
            if(line.isBlank()) {
                splitFound = true;
            } else if(splitFound) {
                printQueues.add(PrintQueue.from(line));
            } else {
                rules.add(Rule.from(line));
            }
        }
    }

    @Override
    public String part1() {
        return Integer.toString(printQueues.stream().filter(this::isValid).mapToInt(PrintQueue::getMiddleValue).sum());
    }

    @Override
    public String part2() {
        var invalids = printQueues.stream().filter(q->!isValid(q)).toList();
        invalids.forEach(this::fixInvalid);
        return Integer.toString(invalids.stream().mapToInt(PrintQueue::getMiddleValue).sum());
    }

    private boolean isValid(PrintQueue printQueue) {
        return rules.stream().allMatch(printQueue::matches);
    }

    private void fixInvalid(PrintQueue printQueue) {
        List<Rule> applicableRules = rules.stream().filter(printQueue::isApplicable).toList();
        int i = 0;
        while (i < applicableRules.size()) {
            Rule rule = applicableRules.get(i);
            if (!printQueue.matches(rule)) {
                printQueue.fixFor(rule);
                i = 0;
            } else {
                ++i;
            }
        }
    }

    private record Rule(int first, int second) {
        static Rule from(String rulDesc) {
            String[] parts = rulDesc.split("\\|");
            return new Rule(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    private static class PrintQueue {
        private static final Comparator<Map.Entry<Integer, Integer>> ORDER_COMPARATOR = Comparator.comparingInt(Map.Entry::getValue);
        private final Map<Integer, Integer> pageToOrder;
        public PrintQueue(Map<Integer, Integer> pageToOrder) {
            this.pageToOrder = pageToOrder;
        }
        boolean matches(Rule rule) {
            if(pageToOrder.containsKey(rule.first) && pageToOrder.containsKey(rule.second)) {
                return pageToOrder.get(rule.first) < pageToOrder.get(rule.second);
            }
            return true;
        }
        void fixFor(Rule rule) {
            int firstOrder = pageToOrder.get(rule.first);
            pageToOrder.put(rule.first, pageToOrder.get(rule.second));
            pageToOrder.put(rule.second, firstOrder);
        }
        int getMiddleValue() {
            return pageToOrder.entrySet().stream().sorted(ORDER_COMPARATOR).skip(pageToOrder.size() / 2).limit(1).findFirst().map(Map.Entry::getKey).orElseThrow();
        }
        boolean isApplicable(Rule rule) {
            return pageToOrder.containsKey(rule.first()) && pageToOrder.containsKey(rule.second());
        }
        static PrintQueue from(String queueDesc) {
            Map<Integer, Integer> pageToOrder = new HashMap<>();
            int order = 0;
            for(String s: queueDesc.split(",")) {
                pageToOrder.put(Integer.parseInt(s), order++);
            }
            return new PrintQueue(pageToOrder);
        }
    }

    public static void main(String[] args) {
        Day05 day05 = new Day05();
        System.out.println(day05.part1());
        System.out.println(day05.part2());
    }

}
