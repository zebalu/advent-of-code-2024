package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.*;

public class Day17 extends AbstractDay {
    private final int regA;
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
        instr = Collections.unmodifiableList(IOUtil.readIntLines(lines.get(4).split("Program: ")[1], ",").getFirst());
    }

    @Override
    public String part1() {
        Computer computer = new Computer(regA, regB, regC, instr);
        computer.execute();
        return computer.print();
    }

    @Override
    public String part2() {
        Queue<State> queue = new PriorityQueue<>(State.REGISTER_COMPARATOR);
        queue.add(new State(0, 0));
        long min = Long.MAX_VALUE;
        while (!queue.isEmpty() && min == Long.MAX_VALUE) {
            State state = queue.poll();
            List<Integer> sufix = instr.subList(instr.size()-state.outputSize-1, instr.size());
            long nextRegisterABase = state.registerA << 3;
            for(long i=0L; i<8L; ++i) {
                long nextRegisterA = nextRegisterABase | i;
                Computer computer = new Computer(nextRegisterA, regB, regC, instr);
                computer.execute();
                if(computer.outPutMatches(sufix)) {
                    if(instr.size() == sufix.size()) {
                        min = Math.min(min, nextRegisterA);
                    }
                    queue.add(new State(nextRegisterA, state.outputSize+1));
                }
            }
        }
        return Long.toString(min);
    }

    private static class Computer {
        private long A;
        private long B;
        private long C;
        private final List<Integer> opcodes;
        private final List<Integer> out = new ArrayList<>();
        private int ip;

        Computer(long registerA, int registerB, int registerC, List<Integer> opcodes) {
            A = registerA;
            B = registerB;
            C = registerC;
            this.opcodes = opcodes;
            ip = 0;
        }

        void execute() {
            while (ip < opcodes.size()) {
                int litOp = opcodes.get(ip + 1);
                long combOp = getCombo(opcodes.get(ip + 1));
                boolean skipIncrease = false;
                switch (opcodes.get(ip)) {
                    case 0 -> {
                        long den = (long) Math.pow(2, combOp);
                        A = A / den;
                    }
                    case 1 -> B = B ^ ((long) litOp);
                    case 2 -> B = combOp % 8;
                    case 3 -> {
                        if (A != 0) {
                            ip = litOp;
                            skipIncrease = true;
                        }
                    }
                    case 4 -> B = B ^ C;
                    case 5 -> out.add((int) (combOp % 8L));
                    case 6 -> {
                        long den = (long) Math.pow(2, combOp);
                        B = A / den;
                    }
                    case 7 -> {
                        long den = (long) Math.pow(2, combOp);
                        C = A / den;
                    }
                }
                if (!skipIncrease) {
                    ip += 2;
                }
            }
        }

        boolean outPutMatches(List<Integer> expected) {
            return expected.equals(out);
        }

        long getCombo(int value) {
            return switch (value % 8) {
                case 0, 1, 2, 3 -> value;
                case 4 -> A;
                case 5 -> B;
                case 6 -> C;
                case 7 -> Long.MIN_VALUE;
                default -> throw new IllegalStateException("Impossible to reach");
            };
        }

        String print() {
            return String.join(",", out.stream().map(Long::toString).toList());
        }
    }

    private record State(long registerA, int outputSize) {
        static final Comparator<State> REGISTER_COMPARATOR = Comparator.comparingLong(State::registerA);
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(17);
        Day17 day17 = new Day17(input);
        System.out.println(day17.part1());
        System.out.println(day17.part2());
    }
}
