package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day23 extends AbstractDay {
    private final Map<String, Set<String>> network;

    public Day23() {
        this(IOUtil.readInput(23));
    }

    public Day23(String input) {
        super(input, "LAN Party", 23);
        network = new HashMap<>();
        INPUT.lines().forEach(line -> {
            String[] computers = line.split("-");
            saveConnection(computers[0], computers[1]);
            saveConnection(computers[1], computers[0]);
        });
    }

    @Override
    public String part1() {
        long interconnectedTriosContainingT = interconnectedTriosWithStartingT().size();
        return Long.toString(interconnectedTriosContainingT);
    }

    @Override
    public String part2() {
        Set<String> largest = findGroups().stream().max(Comparator.comparingInt(Set::size)).orElseThrow();
        return largest.stream().sorted().collect(Collectors.joining(","));
    }

    private void saveConnection(String from, String to) {
        network.computeIfAbsent(from, _ -> new HashSet<>()).add(to);
    }

    private Set<Set<String>> interconnectedTriosWithStartingT() {
        Set<Set<String>> interconnectedTrios = new HashSet<>();
        network.keySet().stream().filter(s -> s.startsWith("t"))
                .forEach(firstNode -> network.get(firstNode).stream().filter(secondNode -> !secondNode.equals(firstNode))
                        .forEach(secondNode -> network.get(secondNode).stream().filter(thirdNode -> !thirdNode.equals(firstNode) && network.get(thirdNode).contains(firstNode))
                                .forEach(thirdNode -> interconnectedTrios.add(Set.of(firstNode, secondNode, thirdNode)))));
        return interconnectedTrios;
    }

    private List<Set<String>> findGroups() {
        Set<String> processed = new HashSet<>();
        List<Set<String>> groups = new ArrayList<>();
        for (String firstNode : network.keySet()) {
            Set<Set<String>> seen = new HashSet<>();
            Queue<SequencedSet<String>> queue = new ArrayDeque<>();
            queue.add(new LinkedHashSet<>(Set.of(firstNode)));
            seen.add(queue.peek());
            while (!queue.isEmpty()) {
                SequencedSet<String> nodes = queue.poll();
                boolean extended = false;
                for (String connected : network.get(nodes.getLast())) {
                    if(!processed.contains(connected) && network.get(connected).containsAll(nodes)) {
                        SequencedSet<String> next = new LinkedHashSet<>(nodes);
                        next.add(connected);
                        if(seen.add(next)) {
                            queue.add(next);
                            extended = true;
                        }
                    }
                }
                if(!extended && nodes.size()>1) {
                    groups.add(nodes);
                }
            }
            processed.add(firstNode);
        }
        return groups;
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(23);
        Day23 day23 = new Day23(input);
        System.out.println(day23.part1());
        System.out.println(day23.part2());
    }
}
