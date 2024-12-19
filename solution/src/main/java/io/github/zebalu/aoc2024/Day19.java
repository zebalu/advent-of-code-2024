package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day19 extends AbstractDay {
    private final List<String> towels;
    private final List<String> designs;

    public Day19() {
        this(IOUtil.readInput(19));
    }
    public Day19(String input) {
        super(input, "Linen Layout", 19);
        var data = IOUtil.groupByEmptyLines(INPUT);
        towels = Arrays.asList(data.getFirst().getFirst().split(", "));
        designs = data.getLast();
    }

    @Override
    public String part1() {
        long count = designs.stream().parallel().filter(this::isPossible).count();
        return ""+count;
    }

    @Override
    public String part2() {
        long count = designs.stream().parallel().filter(this::isPossible).mapToLong(this::countPossible).sum();
        return ""+count;
    }

    private boolean isPossible(String design) {
        //Queue<String> toDo = new ArrayDeque<>();
        Stack<String> toDo = new Stack<>();
        toDo.push(design);
        //toDo.add(design);
        while(!toDo.isEmpty()) {
            String str = toDo.pop();//poll();
            for (String towel : towels) {
                if(str.startsWith(towel)) {
                    if(str.length() == towel.length()) {
                        return true;
                    }
                    toDo.push(str.substring(towel.length()));
                }
            }
        }
        return false;
    }

    //private Map<String, Long> memory = new HashMap<>();

    private long countPossible(String design) {
        Map<String, Long> memory = new HashMap<>();
        long count = countPossible(design, memory);
        System.out.println(design+"\t"+count+"\t"+memory);
        return count;
    }

    private long countPossible(String design, Map<String, Long> memory) {
        if(memory.containsKey(design)) {
            return memory.get(design);
        }
        long count = 0L;


            for (String towel : towels) {
                    if (design.startsWith(towel)) {
                        if (design.length() == towel.length()) {
                            ++count;
                        } else {
                            count += countPossible(design.substring(towel.length()), memory);
                        }
                    }
                }
        memory.put(design, count);
        return count;
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(19);
        Day19 day19 = new Day19(input);//EXAMPLE); //input); //EXAMPLE);//input);
        System.out.println(day19.part1());
        System.out.println(day19.part2());
    }

    private static final String EXAMPLE = """
            r, wr, b, g, bwu, rb, gb, br
            
            brwrr
            bggr
            gbbr
            rrbgbr
            ubwu
            bwurrg
            brgr
            bbrgwb""";
}
