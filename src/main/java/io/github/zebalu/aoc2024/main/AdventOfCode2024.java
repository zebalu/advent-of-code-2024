package io.github.zebalu.aoc2024.main;

import io.github.zebalu.aoc2024.*;
import io.github.zebalu.aoc2024.helper.Downloader;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class AdventOfCode2024 {

    private static final int PRINT_WIDTH = 80;

    public static void main(String[] args) {
        if(isDownloadRequested(args)) {
            downloadInputs();
        }
        List<Supplier<Day>> days = List.of(
                Day01::new, Day02::new, Day03::new, Day04::new, Day05::new,
                Day06::new, Day07::new, Day08::new, Day09::new, Day10::new,
                Day11::new, Day12::new, Day13::new, Day14::new, Day15::new,
                Day16::new, Day17::new, Day18::new, Day19::new, Day20::new,
                Day21::new, Day22::new);
        String bigTitle = "Advent of Code 2024";
        System.out.println();
        int spacesNeeded = (PRINT_WIDTH + bigTitle.length())/2;
        System.out.println(String.format("%"+spacesNeeded+"s", bigTitle));
        System.out.println();
        StringBuilder endSeparator = new StringBuilder();
        endSeparator.repeat("*", PRINT_WIDTH);
        List<Duration> durations = new ArrayList<>();
        Instant bigStart = Instant.now();
        days.forEach(daySupplier->{
            Instant start = Instant.now();
            Day d = daySupplier.get();
            System.out.println(getTitle(d));
            System.out.println(d.part1());
            Instant part1 = Instant.now();
            System.out.println(d.part2());
            Instant end = Instant.now();
            durations.add(Duration.between(start, end));
            System.out.println("  part 1 time: "+ Duration.between(start, part1).toMillis());
            System.out.println("  part 2 time: "+ Duration.between(part1, end).toMillis());
            System.out.println("full day time: "+ Duration.between(start, end).toMillis());
            System.out.println(endSeparator.toString());
        });
        Instant bigEnd = Instant.now();
        System.out.println("Full execution time: "+Duration.between(bigStart, bigEnd).toMillis());
        System.out.println("   Sum of all times: "+durations.stream().reduce(Duration.ZERO, Duration::plus).toMillis());

    }

    private static String getTitle(Day day) {
        return switch (day) {
            case PrettyPrintable pp -> pp.getFormattedTitle(PRINT_WIDTH);
            default -> day.toString();
        };
    }

    private static boolean isDownloadRequested(String[] args) {
        return args != null && Arrays.stream(args).filter(Objects::nonNull).anyMatch(s->s.equalsIgnoreCase("download"));
    }

    private static void downloadInputs() {
        String sessionId = System.getenv("AOC_SESSION_ID");
        if(sessionId == null || sessionId.isBlank()) {
            System.err.println("Session ID in environment variable AOC_SESSION_ID is not set correctly!");
            System.exit(1);
        }
        new Downloader(sessionId, 2024).downloadInputs();
    }
}
