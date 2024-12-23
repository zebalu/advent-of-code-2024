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

    private void saveConnection(String from, String to) {
        network.computeIfAbsent(from, _ -> new HashSet<>()).add(to);
    }

    private Set<Set<String>> interconnectedTrios() {
        Set<Set<String>> interconnectedTrios = new HashSet<>();
        for(String firstNode : network.keySet()) {
            Set<String> connections = network.get(firstNode);
            for (String secondNode : connections) {
                if(!secondNode.equals(firstNode)) {
                    for(String thirdNode : network.get(secondNode)) {
                        if(!thirdNode.equals(firstNode)) {
                            Set<String> thirdConnections = network.get(thirdNode);
                            if(thirdConnections.contains(secondNode) && thirdConnections.contains(firstNode)) {
                                interconnectedTrios.add(Set.of(firstNode, secondNode, thirdNode));
                            }
                        }
                    }
                }
            }
        }
        return interconnectedTrios;
    }

    private Set<Set<String>> findGroups() {
        Set<Set<String>> groups = new HashSet<>();
        for(String firstNode : network.keySet()) {
            Set<String> passed = new HashSet<>(Set.of(firstNode));
            Set<String> checked = new HashSet<>();
            Queue<String> queue = new ArrayDeque<>();
            queue.add(firstNode);
            while(!queue.isEmpty()) {
                String node = queue.poll();
                Set<String> helperSet = new HashSet<>(passed);
                helperSet.remove(node);
                if(!checked.contains(node) && network.get(node).containsAll(helperSet)) {
                    passed.add(node);
                    checked.add(node);
                    for(String next : network.get(node)) {
                        if(!checked.contains(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
            groups.add(passed);
        }
        return groups;
    }

    public boolean oneComputerStartsWithT(Set<String> computers) {
        for(String computer : computers) {
            if(computer.startsWith("t")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String part1() {
        Set<Set<String>> interconnectedTrios = interconnectedTrios();
        List<Set<String>> tContainers = interconnectedTrios.stream().filter(this::oneComputerStartsWithT).toList();
        return ""+tContainers.size();
    }

    @Override
    public String part2() {
        Set<Set<String>> groups = findGroups();
        Set<String> largest = groups.stream().max(Comparator.comparingInt(Set::size)).orElseThrow();
        return largest.stream().sorted().collect(Collectors.joining(","));
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(23);
        Day23 day23 = new Day23(input);
        System.out.println(day23.part1());
        System.out.println(day23.part2());
    }
}
