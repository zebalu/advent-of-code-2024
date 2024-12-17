package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Day17 extends AbstractDay {
    private final  int regA;
    private final int regB;
    private final int regC;
    private final List<Integer> instr;
    public Day17() {
        this(IOUtil.readInput(17));
    }
    public Day17(String input) {
        super(input, "Chronospatial Computer", 17);
        var lines = INPUT.lines().toList();
        regA = Integer.parseInt(lines.get(0).split("Register A: ")[1]);
        regB = Integer.parseInt(lines.get(1).split("Register B: ")[1]);
        regC = Integer.parseInt(lines.get(2).split("Register C: ")[1]);
        instr = Collections.unmodifiableList(IOUtil.readIntLines(lines.get(4).split("Program: ")[1],",").getFirst());
    }

    @Override
    public String part1() {
        Computer computer = new Computer(regA, regB, regC, instr);
        computer.execute();
        return computer.print();
    }

    @Override
    public String part2() {
        List<Integer> remainingProgram = new ArrayList<>(instr);
        List<Integer> program = new ArrayList<>();
        long A = 0L;
        while (!remainingProgram.isEmpty()) {
            --A;
            program.addFirst(remainingProgram.removeLast());
            String pString = program.stream().map(Long::toString).collect(Collectors.joining(","));
            Computer computer;
            do {
                ++A;
                computer = new Computer(A, regB, regC, instr);
                computer.execute(true, program);
            } while (!computer.print().equals(pString));
            if(!remainingProgram.isEmpty()) {
                A=A<<3;
            }
        }
        return Long.toString(A);
    }

    private static class Computer {
        long A;
        long B;
        long C;
        List<Integer> opcodes;
        List<Integer> out = new ArrayList<>();
        int ip;
        Computer (long registerA, int registerB, int registerC, List<Integer> opcodes) {
            A=registerA;
            B=registerB;
            C=registerC;
            this.opcodes=opcodes;
            ip=0;
        }

        void execute() {
            execute(false, List.of());
        }

        void execute(boolean earlyStop, List<Integer> expected) {
            while (ip < opcodes.size()) {
                int litOp = opcodes.get(ip+1);
                long combOp = getCombo(opcodes.get(ip+1));
                boolean skipIncrease = false;
                switch (opcodes.get(ip)) {
                    case 0 -> {
                        long den = (long)Math.pow(2, combOp);
                        A = A / den;
                    }
                    case 1 -> B = B ^ ((long)litOp);
                    case 2 -> B = combOp % 8;
                    case 3 -> {
                        if(A != 0) {
                            ip = litOp;
                            skipIncrease = true;
                            if(earlyStop && !outPutMatches(expected)) {
                                return;
                            }
                        }
                    }
                    case 4 -> B = B^C;
                    case 5 -> out.add((int) (combOp % 8L));
                    case 6 -> {
                        long den = (long)Math.pow(2, combOp);
                        B = A / den;
                    }
                    case 7 -> {
                        long den = (long)Math.pow(2, combOp);
                        C = A / den;
                    }
                }
                if(!skipIncrease) {
                    ip += 2;
                }
            }
        }

        boolean outPutMatches(List<Integer> expected) {
            if(out.size()>expected.size()) {
                return false;
            }
            for(int i=0; i<out.size(); i++) {
                if(!out.get(i).equals(expected.get(i))) {
                    return false;
                }
            }
            return true;
        }

        long getCombo(int value) {
            return switch (value%8) {
                case 0,1,2,3 -> value;
                case 4 -> A;
                case 5 -> B;
                case 6 -> C;
                case 7 -> Long.MIN_VALUE;//throw new IllegalStateException("The value 7 should not appear in valid programs");
                default -> throw new IllegalStateException("Impossible to reach");
            };
        }

        String print() {
            return String.join(",", out.stream().map(Long::toString).toList());
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(17);
        Day17 day17 = new Day17(input);
        System.out.println(day17.part1());
        System.out.println(day17.part2());
    }
}
