package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 extends AbstractDay {
    private static final Pattern BIT_PATTERN = Pattern.compile("[xyz]\\d+");
    private static final Comparator<Map.Entry<String, Integer>> KEY_COMPARATOR = Comparator.comparing((Map.Entry<String, Integer> e) -> e.getKey()).reversed();
    private final Map<String, Integer> values = new HashMap<>();
    private final List<Rule> rules;
    private final Map<Character, List<String>> bitMap = new HashMap<>();
    private final int inputLength;
    private final int outputLength;

    public Day24() {
        this(IOUtil.readInput(24));
    }

    public Day24(String input) {
        super(input, "Crossed Wires", 24);
        var groups = IOUtil.groupByEmptyLines(INPUT);
        for (var initial : groups.getFirst()) {
            String[] parts = initial.split(": ");
            values.put(parts[0], Integer.parseInt(parts[1]));
            if (BIT_PATTERN.matcher(parts[0]).matches()) {
                addToBitMap(parts[0]);
            }
        }
        rules = groups.getLast().stream().map(Rule::parse).toList();
        rules.stream().filter(r -> r.result.startsWith("z")).forEach(r -> addToBitMap(r.result));
        if (bitMap.get('x').size() != bitMap.get('y').size()) {
            throw new IllegalArgumentException("Invalid input! We should have as many Xes as many Ys!");
        }
        inputLength = bitMap.get('x').size();
        outputLength = bitMap.get('z').size();
    }

    @Override
    public String part1() {
        String binary = execute(new HashMap<>(values), rules);
        long value = Long.parseLong(binary, 2);
        return "" + value;
    }

    @Override
    public String part2() {
        var allBits = rules.stream().map(r -> List.of(r.first, r.second, r.result)).flatMap(List::stream).filter(this::isBit).collect(Collectors.toSet());
        var xs = allBits.stream().filter(s -> s.startsWith("x")).toList();
        var zs = allBits.stream().filter(s -> s.startsWith("z")).toList();
        List<String> swapped = new ArrayList<>();
        String carry = null;
        for (int i = 0; i < xs.size(); i++) {
            String xKey = String.format("x%02d", i);
            String yKey = String.format("y%02d", i);
            List<String> outputs = fullAdder(xKey, yKey, carry, rules, swapped);
            if (outputs.getLast() != null && outputs.getLast().startsWith("z") && !outputs.getLast().equals("z" + (zs.size() - 1))) {
                outputs = outputs.reversed();
                swapped.add(outputs.getFirst());
                swapped.add(outputs.getLast());
            }
            if (outputs.getLast() != null) {
                carry = outputs.getLast();
            } else {
                carry = findMatchinRule(xKey, yKey, Rule.AND, rules).map(r -> r.result).orElse(null);
            }

        }
        return swapped.stream().sorted().collect(Collectors.joining(","));
    }

    public String part2_2() {
        return findSwaps(rules).stream().sorted().collect(Collectors.joining(","));
    }

    private void addToBitMap(String key) {
        bitMap.compute(key.charAt(0), (_, v) -> {
            if (v == null) {
                return new ArrayList<>(List.of(key));
            } else {
                if (Collections.binarySearch(v, key) < 0) {
                    v.add(key);
                    Collections.sort(v);
                }
                return v;
            }
        });
    }


    private boolean isBit(String str) {
        return BIT_PATTERN.matcher(str).matches();
    }

    private String execute(Map<String, Integer> copyOfValues, List<Rule> rules) {
        Queue<Rule> toDo = new ArrayDeque<>(rules);
        Set<Rule> seen = new HashSet<>();
        boolean loop = false;
        while (!toDo.isEmpty() && !loop) {
            Rule next = toDo.poll();
            loop = seen.contains(next);
            if (next.canBecalcualted(copyOfValues)) {
                next.calculate(copyOfValues);
                seen.clear();
            } else {
                toDo.add(next);
                seen.add(next);
            }
        }
        if (loop) {
            return "1".repeat(64);
        }
        return copyOfValues.entrySet().stream().filter(e -> e.getKey().startsWith("z")).sorted(KEY_COMPARATOR).map(Map.Entry::getValue).map(i -> Integer.toString(i)).collect(Collectors.joining(""));
    }

    private record Rule(String first, String second, String result, IntBinaryOperator operator) {
        private static final Pattern RULE_PATTERN = Pattern.compile("(\\w+) (XOR|AND|OR) (\\w+) -> (\\w+)");
        private static final IntBinaryOperator AND = (a, b) -> a == 1 && b == 1 ? 1 : 0;
        private static final IntBinaryOperator OR = (a, b) -> a == 1 || b == 1 ? 1 : 0;
        private static final IntBinaryOperator XOR = (a, b) -> a != b ? 1 : 0;

        boolean canBecalcualted(Map<String, Integer> values) {
            return values.containsKey(this.first) && values.containsKey(this.second);
        }

        void calculate(Map<String, Integer> values) {
            values.put(result, operator().applyAsInt(values.get(first), values.get(second)));
        }

        boolean isTop() {
            return result.startsWith("z");
        }

        List<String> wires() {
            return List.of(first, second, result);
        }

        Rule swap(String from, String to) {
            if (from.equals(first)) {
                return new Rule(to, second, result, operator);
            } else if (from.equals(second)) {
                return new Rule(first, to, result, operator);
            } else if (from.equals(result)) {
                return new Rule(first, second, to, operator);
            } else {
                throw new IllegalArgumentException("Unknown wire: " + from + "; (valids: " + wires() + ")");
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(first);
            result.append(" ");
            switch (operator) {
                case IntBinaryOperator o when o == AND -> result.append("AND");
                case IntBinaryOperator o when o == OR -> result.append("OR");
                case IntBinaryOperator o when o == XOR -> result.append("XOR");
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            }
            result.append(" ");
            result.append(second);
            result.append(" -> ");
            result.append(this.result);
            return result.toString();
        }

        static Rule parse(String input) {
            var matcher = RULE_PATTERN.matcher(input);
            if (matcher.matches()) {
                var first = matcher.group(1);
                var second = matcher.group(3);
                var result = matcher.group(4);
                var op = matcher.group(2);
                return switch (op) {
                    case "XOR" -> new Rule(first, second, result, XOR);
                    case "AND" -> new Rule(first, second, result, AND);
                    case "OR" -> new Rule(first, second, result, OR);
                    default -> throw new IllegalStateException("Unexpected value: " + op + " in line: " + input);
                };
            }
            throw new IllegalStateException("Unexpected input: " + input);
        }
    }

    private record Swap(Rule a, Rule b, Rule aS, Rule bS, Set<String> swapped) {
        static List<Swap> swapAll(Rule a, Rule b) {
            List<Swap> result = new ArrayList<>(9);
            if (!a.equals(b)) {
                for (var aW : a.wires()) {
                    for (var bW : b.wires()) {
                        if (!aW.equals(bW)) {
                            result.add(new Swap(a, b, a.swap(aW, bW), b.swap(bW, aW), Set.of(aW, bW)));
                        }
                    }
                }
            }
            return result;
        }
    }

    private Set<String> findSwaps(List<Rule> rules) {
        record State(List<Rule> rules, Set<String> swaps, Set<Rule> trusted, int min) {
        }
        Queue<State> queue = new ArrayDeque<>();
        queue.add(new State(rules, Set.of(), Set.of(), 0));
        while (!queue.isEmpty()) {
            State actual = queue.poll();
            Set<Rule> actualTrusted = new HashSet<>(actual.trusted);
            boolean failed = false;
            for (int i = actual.min; i < inputLength && !failed; ++i) {
                if (!check(actual.rules, i)) {
                    failed = true;
                    Set<Rule> culprits = findContributors(bitMap.get('z').get(i), actual.rules);
                    culprits.removeAll(actualTrusted);
                    for (var pair : pairAll(culprits, actual.rules, actualTrusted)) {
                        for (var swap : Swap.swapAll(pair.getFirst(), pair.getLast())) {
                            Set<String> swapsCopy = new HashSet<>(actual.swaps);
                            swapsCopy.addAll(swap.swapped);
                            if (swapsCopy.size() == actual.swaps.size() + 2) {
                                List<Rule> rulesCopy = new ArrayList<>(actual.rules);
                                rulesCopy.remove(swap.a);
                                rulesCopy.remove(swap.b);
                                rulesCopy.add(swap.aS);
                                rulesCopy.add(swap.bS);
                                if (check(rulesCopy, i)) {
                                    queue.add(new State(rulesCopy, swapsCopy, actualTrusted, i));
                                }
                            }
                        }
                    }
                } else {
                    actualTrusted.addAll(findContributors(bitMap.get('z').get(i), actual.rules));
                }
            }
            if (!failed) {
                return actual.swaps;
            }
        }
        return Set.of();
    }

    private List<List<Rule>> pairAll(Set<Rule> culprits, List<Rule> rules, Set<Rule> trusted) {
        List<List<Rule>> result = new ArrayList<>();
        for (var culprit : culprits) {
            for (var rule : rules) {
                if (!culprit.equals(rule) && !trusted.contains(rule)) {
                    result.add(List.of(culprit, rule));
                }
            }
        }
        return result;
    }

    private boolean check(List<Rule> rules, int to) {
        long one = 1L;
        long zero = 0L;
        long maxTo = (long) Math.pow(2L, to) - 1L;
        long maxMask = (long) Math.pow(2L, to + 1L) - 1L;
        long maxOnes = (long) Math.pow(2L, inputLength) - 1L;
        return check(maxTo, zero, maxTo, rules, -1L) &&
                check(zero, maxTo, maxTo, rules, -1L) &&
                check(maxTo, one, maxTo + one, rules, -1L) &&
                check(one, maxTo, maxTo + one, rules, -1L) &&
                check(maxOnes, maxOnes, maxOnes + maxOnes, rules, maxMask);
    }

    private boolean check(long x, long y, long z, List<Rule> rules, long bitMask) {
        //System.out.println(x+"\t"+y+"\t"+bitMask+"\t"+Long.toBinaryString(bitMask));
        //System.out.println(Long.toBinaryString(x)+"\t"+Long.toBinaryString(y)+"\t"+Long.toBinaryString(z));
        Map<String, Integer> values = new HashMap<>();
        long xCurrent = x;
        long yCurrent = y;
        for (int i = 0; i < inputLength; ++i) {
            values.put(bitMap.get('x').get(i), (int) (xCurrent % 2));
            values.put(bitMap.get('y').get(i), (int) (yCurrent % 2));
            xCurrent /= 2;
            yCurrent /= 2;
        }
        String executed = execute(values, rules);
        if (executed.length() > outputLength) {
            return false;
        }
        // System.out.println(executed+"\t"+Long.parseLong(executed, 2)+"\t"+(Long.parseLong(executed, 2) & bitMask)+"\t"+z);
        long result = Long.parseLong(executed, 2) & bitMask;

        return (z & bitMask) == result;
    }

    Set<Rule> findContributors(String key, List<Rule> rules) {
        Set<Rule> result = new HashSet<>();
        Set<String> endKeys = new HashSet<>(Set.of(key));
        while (!endKeys.isEmpty()) {
            Set<String> collector = new HashSet<>();
            for (var rule : rules) {
                if (endKeys.contains(rule.result)) {
                    collector.add(rule.first);
                    collector.add(rule.second);
                    result.add(rule);
                }
            }
            endKeys = collector;
        }
        return result;
    }

    /// https://www.geeksforgeeks.org/full-adder-in-digital-logic/
    /// ```
    /// A -|
    ///    |-XOR-> (A XOR B) // basicSum
    /// B -|
    ///
    /// C---------|
    ///           |-XOR-> (SUM) // finalSum
    ///(A XOR B)-|
    ///
    /// A-|
    ///   |-AND-> (A AND B) // basicCarry
    /// B-|
    ///
    /// C---------|
    ///           |-AND->((A XOR B) AND C) // carryForward1
    ///(A XOR B)-|
    ///
    ///((A XOR B) AND C)-|
    ///                   |-OR->((((A XOR B) XOR C)) OR (A AND B)) // finalCarry
    ///(A AND B)---------|
    ///```
    ///
    /// [Original Idea](https://github.com/xhyrom/aoc/blob/main/2024/24/part_2.py)
    /// All hail to @xhyrom
    List<String> fullAdder(String a, String b, String carry, List<Rule> rules, List<String> swapped) {
        Optional<Rule> basicSum = findMatchinRule(a, b, Rule.XOR, rules);
        basicSum.orElseThrow();
        Optional<Rule> basicCarry = findMatchinRule(a, b, Rule.AND, rules);
        basicCarry.orElseThrow();
        if (carry == null) {
            return List.of(basicSum.get().result, basicCarry.get().result);
        } else {
            Optional<Rule> carryForward1 = findMatchinRule(basicSum.get().result, carry, Rule.AND, rules);
            if (carryForward1.isEmpty()) {
                swapped.add(basicSum.get().result);
                swapped.add(basicCarry.get().result);
                var temp = basicSum;
                basicSum = basicCarry;
                basicCarry = temp;
                carryForward1 = findMatchinRule(basicSum.get().result, carry, Rule.AND, rules);
            }

            Optional<Rule> finalSum = findMatchinRule(basicSum.get().result, carry, Rule.XOR, rules);

            if (basicSum.get().isTop() && finalSum.isPresent()) {
                var temp = basicSum;
                basicSum = finalSum;
                finalSum = temp;
                swapped.add(finalSum.get().result);
                swapped.add(basicSum.get().result);
            }
            if (basicCarry.get().isTop() && finalSum.isPresent()) {
                var temp = basicCarry;
                basicCarry = finalSum;
                finalSum = temp;
                swapped.add(basicCarry.get().result);
                swapped.add(finalSum.get().result);
            }
            if (carryForward1.isPresent() && carryForward1.get().isTop() && finalSum.isPresent()) {
                var temp = carryForward1;
                carryForward1 = finalSum;
                finalSum = temp;
                swapped.add(carryForward1.get().result);
                swapped.add(finalSum.get().result);
            }
            Optional<Rule> finalCarry;
            if (carryForward1.isPresent()) {
                finalCarry = findMatchinRule(carryForward1.get().result, basicCarry.get().result, Rule.OR, rules);
            } else {
                finalCarry = Optional.empty();
            }
            List<String> result = new ArrayList<>();
            result.add(finalSum.map(Rule::result).orElse(null));
            result.add(finalCarry.map(Rule::result).orElse(null));
            return result;
        }
    }

    Optional<Rule> findMatchinRule(String a, String b, IntBinaryOperator operator, List<Rule> rules) {
        return rules.stream().filter(r -> r.operator == operator).filter(r -> (r.first.equals(a) && r.second.equals(b)) || (r.second.equals(a) && r.first.equals(b))).findFirst();
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(24);
        Day24 day24 = new Day24(input);
        System.out.println(day24.part1());
        System.out.println(day24.part2());
        //System.out.println(day24.part2_2());
    }
}
