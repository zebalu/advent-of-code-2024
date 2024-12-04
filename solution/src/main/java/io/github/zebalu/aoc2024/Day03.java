package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends AbstractDay {
    private final Pattern COMPLEX_MUL_PATTERN = Pattern.compile("(do\\(\\)|mul\\(\\d{1,3},\\d{1,3}\\)|don't\\(\\))");

    private final List<Instruction> instructions = new ArrayList<>();

    public Day03() {
        this(IOUtil.readInput(3));
    }

    public Day03(String input) {
        super(input, "Mull It Over", 3);
        Matcher matcher = COMPLEX_MUL_PATTERN.matcher(INPUT);
        while (matcher.find()) {
            instructions.add(Instruction.fromString(matcher.group(1)));
        }
    }

    @Override
    public String part1() {
        return Integer.toString(calculate(false));
    }

    @Override
    public String part2() {
        return Integer.toString(calculate(true));
    }

    private int calculate(boolean processDosAndDonts) {
        int result = 0;
        boolean shouldInclude = true;
        for(Instruction instruction : instructions) {
            if(!instruction.isCalculatable()) {
                if(processDosAndDonts) {
                    if (instruction.isInclusive()) {
                        shouldInclude = true;
                    } else {
                        shouldInclude = false;
                    }
                }
            } else if (shouldInclude) {
                result += instruction.calc();
            }
        }
        return result;
    }

    private sealed interface Instruction permits MulInstruction, DoInstruction, DontInstruction {
        default boolean isCalculatable() {
            return switch (this) {
                case MulInstruction _ -> true;
                default -> false;
            };
        }

        default boolean isInclusive() {
            return switch (this) {
                case DoInstruction _ -> true;
                case DontInstruction _ -> false;
                case MulInstruction _ -> throw new IllegalStateException();
            };
        }

        default int calc() {
            return switch (this) {
                case MulInstruction mi -> mi.a*mi.b;
                default -> throw new IllegalStateException();
            };
        }

        static Instruction fromString(String string) {
            return switch (string) {
                case "do()" -> new DoInstruction();
                case "don't()" -> new DontInstruction();
                default -> new MulInstruction(string);
            };
        }

    }

    private static final class MulInstruction implements Instruction {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d{1,3}),(\\d{1,3})");
        private final int a;
        private final int b;
        MulInstruction(String mulString) {
            Matcher numMatcher = NUMBER_PATTERN.matcher(mulString);
            if (numMatcher.find()) {
                a = Integer.parseInt(numMatcher.group(1));
                b = Integer.parseInt(numMatcher.group(2));
            } else {
                throw new IllegalArgumentException("Invalid mul string: " + mulString);
            }
        }
    }

    private static final class DoInstruction implements Instruction {

    }

    private static final class DontInstruction implements Instruction {

    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(3);
        Day03 day03 = new Day03(input);
        System.out.println(day03.part1());
        System.out.println(day03.part2());
    }
}
