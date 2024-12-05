package io.github.zebalu.aoc2024;

import java.util.*;

public class Day05 extends AbstractDay {
    private final List<Rule> rules;
    private final List<PrintQueue> printQueues;
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
        printQueues = queueLines.stream().map(l->l.split(",")).map(ss->Arrays.stream(ss).map(Integer::parseInt).toList()).map(PrintQueue::new).toList();
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
        int i = 0;
        while (i < rules.size()) {
            Rule rule = rules.get(i);
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
        private final Map<Integer, Integer> pageToOrder = new HashMap<>();
        public PrintQueue(List<Integer> printOrder) {
            for(int i = 0; i < printOrder.size(); i++) {
                pageToOrder.put(printOrder.get(i), i);
            }
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
    }

    public static void main(String[] args) {
        Day05 day05 = new Day05();
        System.out.println(day05.part1());
        System.out.println(day05.part2());
    }

}
