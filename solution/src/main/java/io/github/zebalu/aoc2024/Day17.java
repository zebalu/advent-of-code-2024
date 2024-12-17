package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day17 extends AbstractDay {
    private int regA;
    private int regB;
    private int regC;
    private List<Integer> instr;
    private String originalInstrString;
    public Day17() {
        this(IOUtil.readInput(17));
    }
    public Day17(String input) {
        super(input, "Chronospatial Computer", 17);
        var lines = INPUT.lines().toList();
        regA = Integer.parseInt(lines.get(0).split("Register A: ")[1]);
        regB = Integer.parseInt(lines.get(1).split("Register B: ")[1]);
        regC = Integer.parseInt(lines.get(2).split("Register C: ")[1]);
        originalInstrString = lines.get(4).split("Program: ")[1];
        instr = IOUtil.readIntLines(originalInstrString,",").getFirst();
    }

    @Override
    public String part1() {
        Computer computer = new Computer(regA, regB, regC, instr);
        //computer = new Computer(0, 2024, 43690, List.of(4,0));
        computer.execute();
        return ""+computer.print();
    }

    @Override
    public String part2() {
        /*
        List<Integer> remainingProgram = new ArrayList<>(instr);
        List<Integer> program = new ArrayList<>();
        long A = 0;
        while (!remainingProgram.isEmpty()) {
            --A;
            program.addFirst(remainingProgram.removeLast());
            String pString = program.stream().map(Long::toString).collect(Collectors.joining(","));
            Computer computer;
            do {
                ++A;
                computer = new Computer(A, regB, regC, instr);
                computer.execute();
            } while (!computer.print().equals(pString));
            if(!remainingProgram.isEmpty()) {
                A=A<<3;
            }
        }
        /*
        Map<Integer, Integer> values = new HashMap<>();
        for(int i=0; i<8; ++i) {
            int AA = -1;
            Computer computer;
            do {
                ++AA;
                computer = new Computer(AA, regB, regC, instr);
                computer.execute();
                if(AA%1_000_000 == 0) {
                    System.out.println(":( "+AA);
                }
            }while(!computer.print().equals(Integer.toString(i)));
            System.out.println("found: "+i+" --> "+AA);
            values.put(i,AA);
        }
        System.out.println(values);
         */
        return ""+part2_2();
    }

    //@Override
    public String part2_2() {
        List<Integer> remainingProgram = new ArrayList<>(instr);
        List<Integer> program = new ArrayList<>();
        long prevA = 0L;
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
            prevA = A;
            if(!remainingProgram.isEmpty()) {
                A=A<<3;
            }
        }
        /*
        Map<Integer, Integer> values = new HashMap<>();
        for(int i=0; i<8; ++i) {
            int AA = -1;
            Computer computer;
            do {
                ++AA;
                computer = new Computer(AA, regB, regC, instr);
                computer.execute();
                if(AA%1_000_000 == 0) {
                    System.out.println(":( "+AA);
                }
            }while(!computer.print().equals(Integer.toString(i)));
            System.out.println("found: "+i+" --> "+AA);
            values.put(i,AA);
        }
        System.out.println(values);
         */
        return ""+A;
    }

    private class Computer {
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
                long pA = A, pB = B, pC = C;
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
                        if(den == 0) {
                            System.out.println("lit : "+litOp);
                            System.out.println("cop : "+combOp);
                            System.out.println("B: "+B);
                        }
                        C = A / den;
                    }
                }
                /*
                if(B < 0) {
                    System.out.println(pA+" "+pB+" "+pC+" "+ip+" "+litOp+" "+combOp);
                    System.out.println(A+" "+B+" "+C+" "+ip+" "+litOp+" "+combOp);
                }*/
                if(!skipIncrease) {
                    ip += 2;
                }
            }
        }

        boolean outPutMatches(List<Integer> expected) {
            /*
            System.out.println("compare : ");
            System.out.println("exp: "+expected);
            System.out.println("out: "+out);

             */
            if(out.size()>expected.size()) {
                return false;
            }
            for(int i=0; i<out.size(); i++) {
                if(!out.get(i).equals(expected.get(i))) {
                    //System.out.println("false");
                    return false;
                }
            }
            //System.out.println("true");
            return true;
        }

        boolean matches(int output, long expectedA) {
            return out.size()==1 && out.getFirst() == output && A == expectedA;
        }

        long getCombo(int value) {
            return switch (value%8) {
                case 0,1,2,3 -> value;
                case 4 -> A;
                case 5 -> B;
                case 6 -> C;
                case 7 -> 7;//throw new IllegalStateException("The value 7 should not appear in valid programs");
                default -> throw new IllegalStateException("Impossible to reach");
            };
        }

        String print() {
            return String.join(",", out.stream().map(i->Long.toString(i)).toList());
        }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(17);
        Day17 day17 = new Day17(input);
        System.out.println(day17.part1());
        System.out.println(day17.part2());
        System.out.println(day17.part2_2());
    }
    /*
2,4,1,1,7,5,1,5,0,3,4,3,5,5,3,0

2,4 B = A % 8
1,1 B = B ^ 1
7,5 C = A / B
1,5 B = B ^ 5
0,3 A = A / 3
4,3 B = B ^ C
5,5 out << B%8
3,0 if (A != 0) restart
     */
}
