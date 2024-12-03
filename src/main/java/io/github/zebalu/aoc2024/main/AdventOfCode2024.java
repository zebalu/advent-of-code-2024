package io.github.zebalu.aoc2024.main;

import io.github.zebalu.aoc2024.Day01;
import io.github.zebalu.aoc2024.Day02;
import io.github.zebalu.aoc2024.Day03;
import io.github.zebalu.aoc2024.helper.Downloader;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdventOfCode2024 {
    public static void main(String[] args) {
        if(isDownloadRequested(args)) {
            downloadInputs();
        }
        var days = List.of(/*new Day01() , new Day02(),*/ new Day03());
        days.forEach(d->{
            System.out.println(d.getFormattedTitle(80));
            Instant start = Instant.now();
            System.out.println(d.part1());
            System.out.println(d.part2());
            Instant end = Instant.now();
            System.out.println("time: "+ Duration.between(start, end).toMillis());
        });
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
