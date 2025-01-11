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
        Set<String> largest = bronKerboschCliquesFind().stream().max(Comparator.comparingInt(Set::size)).orElseThrow();
        return largest.stream().sorted().collect(Collectors.joining(","));
    }

    private void saveConnection(String from, String to) {
        network.computeIfAbsent(from, _ -> new HashSet<>()).add(to);
    }

    private Set<Set<String>> interconnectedTriosWithStartingT() {
        Set<Set<String>> interconnectedTrios = new HashSet<>();
        network.keySet().stream().filter(s -> s.startsWith("t"))
                .forEach(firstNode -> network.get(firstNode).stream()
                        .forEach(secondNode -> network.get(firstNode).stream().filter(thirdNode -> !thirdNode.equals(secondNode) && network.get(thirdNode).contains(secondNode))
                                .forEach(thirdNode -> interconnectedTrios.add(Set.of(firstNode, secondNode, thirdNode)))));
        return interconnectedTrios;
    }

    private List<Set<String>> bronKerboschCliquesFind() {
        record State(Set<String> clique, Set<String> candidates, Set<String> excludes) {

            boolean isFinalClique() {
                return candidates.isEmpty() && excludes.isEmpty();
            }

            State extend(String c, Set<String> neighbours, Set<String> remainingCandidates, Set<String> extendedExclude) {
                return new State(extend(clique, c), intersect(neighbours, remainingCandidates), intersect(neighbours, extendedExclude));
            }

            static Set<String> intersect(Set<String> a, Set<String> b) {
                HashSet<String> cpy = new HashSet<>(a.size() < b.size() ? a : b);
                cpy.retainAll(a.size() < b.size() ? b : a);
                return cpy;
            }

            static Set<String> extend(Set<String> a, String b) {
                Set<String> cpy = new HashSet<>(a);
                cpy.add(b);
                return cpy;
            }
        }
        List<Set<String>> groups = new ArrayList<>();
        Queue<State> queue = new ArrayDeque<>(List.of(new State(Set.of(), network.keySet(), Set.of())));
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (state.isFinalClique()) {
                groups.add(state.clique);
            } else {
                Set<String> remainingCandidates = new HashSet<>(state.candidates);
                Set<String> extendedExclude = new HashSet<>(state.excludes);
                for (String c : state.candidates) {
                    queue.add(state.extend(c, network.get(c), remainingCandidates, extendedExclude));
                    remainingCandidates.remove(c);
                    extendedExclude.add(c);
                }
            }
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
