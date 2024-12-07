package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day07 extends AbstractDay {
    private List<Calibration> calibrations;
    public Day07() {
        this(IOUtil.readInput(7));
    }
    public Day07(String input) {
        super(input, "???", 7);
        var lines = INPUT.lines().toList();
        calibrations = lines.stream().map(Calibration::from).toList();
    }

    @Override
    public String part1() {
        long res = calibrations.stream().filter(Calibration::isValid).mapToLong(Calibration::testValue).sum();
        return ""+res;
    }

    @Override
    public String part2() {
        //var valids = calibrations.stream().filter(Calibration::isValid).collect(Collectors.toSet());
        //long res1 =  valids.stream().mapToLong(Calibration::testValue).sum();
        //long res = calibrations.stream().filter(Predicate.not(valids::contains)).filter(Calibration::isExtendedValid).mapToLong(Calibration::testValue).sum();
        return ""+calibrations.stream().filter(Calibration::isBackwardValid).mapToLong(Calibration::testValue).sum();
        //return ""+(res+res1);
    }

    private record Calibration (long testValue, List<Long> parts) {
        static Calibration from(String line) {
            String[] parting1 = line.split(": ");
            long testValue = Long.parseLong(parting1[0]);
            List<Long> parts = new ArrayList<>();
            String[] parting2 = parting1[1].split(" ");
            for (int i = 0; i < parting2.length; i++) {
                parts.add(Long.parseLong(parting2[i]));
            }
            return new Calibration(testValue, parts);
        }
        boolean isValid() {
            if(parts.isEmpty()) {
                return testValue == 0;
            }
            Queue<Long> toConsume = new ArrayDeque<>(parts);
            List<OperationResult>  current = new ArrayList<>();
            long v = toConsume.poll();
            current.add(new OperationResult(v, v));
            while (!toConsume.isEmpty() && !current.isEmpty()){
                long num = toConsume.poll();
                current = current.stream().<OperationResult>mapMulti(
                        (or, ds)->{
                        ds.accept(new OperationResult(or.withPlus+num, or.withMul*num));
                ds.accept(new OperationResult(or.withPlus*num, or.withMul+num));
                        }).filter(or->or.isPossible(testValue)).toList();
            }
            return toConsume.isEmpty() && current.stream().anyMatch(or->or.isSolution(testValue));
        }
        boolean isExtendedValid() {
            if(parts.isEmpty()) {
                return testValue == 0;
            }
            Queue<Long> toConsume = new ArrayDeque<>(parts);
            List<ExtendedOperationResult>  current = new ArrayList<>();
            long v = toConsume.poll();
            current.add(new ExtendedOperationResult(v, v, v));
            while (!toConsume.isEmpty() && !current.isEmpty()){
                long num = toConsume.poll();
                current = current.stream().<ExtendedOperationResult>mapMulti(
                        (or, ds)->{
                            long concP = concatenate(or.withPlus,num);
                            long concM = concatenate(or.withMul,num);
                            long concC = concatenate(or.withConc, num);
                            if(concC <= testValue) {
                                ds.accept(new ExtendedOperationResult(or.withPlus + num, or.withMul * num, concC));
                                ds.accept(new ExtendedOperationResult(or.withPlus * num, or.withMul + num, concC));
                            }
                            if(concP <= testValue) {
                                ds.accept(new ExtendedOperationResult(concP, or.withMul + num, or.withConc * num));
                                ds.accept(new ExtendedOperationResult(concP, or.withMul * num, or.withConc + num));
                            }
                            if(concM <= testValue) {
                                ds.accept(new ExtendedOperationResult(or.withPlus + num, concM, or.withConc * num));
                                ds.accept(new ExtendedOperationResult(or.withPlus * num, concM, or.withConc + num));
                            }
                        }).filter(or->or.isPossible(testValue)).toList();
            }
            return toConsume.isEmpty() && current.stream().anyMatch(or->or.isSolution(testValue));
        }

        boolean isBackwardValid() {
            if(parts.isEmpty()) {
                return testValue == 0;
            }
            boolean[] result = new boolean[]{false};
            Queue<Long> toConsume = new ArrayDeque<>(parts.reversed());
            List<Long>  current = new ArrayList<>();
            current.add(testValue);
            while (!toConsume.isEmpty() && !current.isEmpty()){
                long num = toConsume.poll();
                List<Long> next = new ArrayList<>();
                for(var v : current){
                    if(v == num && toConsume.isEmpty()) {
                        return true;
                    }
                    if(v > num) {
                        long changed = v - num;
                        next.add(changed);
                    }
                    if(v%num==0) {
                        long changed = v / num;
                        next.add(changed);
                    }
                    long retracted = retract(v, num);
                    if(retract(v, num) != v) {
                        //System.out.println(v+"\t"+num+"\t"+retracted);
                        next.add(retracted);
                    }
                }
                current = next;
            }
            return false;
        }
        private long concatenate(long left, long right) {
            String helper = left+""+right;
            return Long.parseLong(helper);
        }
        private long retract(long left, long right) {
            if(left == right) {
                return 0;
            }
            String leftStr = Long.toString(left);
            String rightStr = Long.toString(right);
            if(leftStr.endsWith(rightStr)) {
                return Long.parseLong(leftStr.substring(0, leftStr.length() - rightStr.length()));
            }
            return left;
        }
    }

    private record OperationResult (long withPlus, long withMul) {
        public boolean isPossible(long testValue) {return withPlus <= testValue || withMul <= testValue;}
        public boolean isSolution(long testValue) { return testValue == withPlus || testValue == withMul; }
    }
    private record ExtendedOperationResult (long withPlus, long withMul, long withConc) {
        public boolean isPossible(long testValue) {return withPlus <= testValue || withMul <= testValue || withConc <= testValue;}
        public boolean isSolution(long testValue) { return testValue == withPlus || testValue == withMul || withConc == testValue; }
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(7);
        Day07 day07 = new Day07(input); //EXAMPLE);
        System.out.println(day07.part1());
        System.out.println(day07.part2());
    }

    private static final String EXAMPLE = """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
            """;
}
