package io.github.zebalu.aoc2024.helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.stream.IntStream;

public class Downloader {

    private final String sessionId;
    private final int year;

    public Downloader(String sessionId, int year) {
        this.sessionId = sessionId;
        this.year = year;
    }

    public void downloadInputs() {
        var httpClient = createHttpClient();
        IntStream.rangeClosed(1, 25)
                .filter(i -> Instant.parse(String.format("%d-12-%02dT05:00:00Z", year, i)).isBefore(Instant.now()))
                .forEach(i -> {
                    try {
                        var target = new File(String.format("input-%02d.txt", i));
                        if(!target.exists()) {
                            System.out.println("Downloading Day " + i);
                            var httpRequest = HttpRequest.newBuilder().GET()
                                    .uri(new URI(String.format("https://adventofcode.com/%d/day/", year) + i + "/input")).build();
                            var data = httpClient.send(httpRequest, BodyHandlers.ofString()).body();
                            try (var pw = new PrintWriter(target)) {
                                pw.print(data);
                            }
                            System.out.println("Saved: " + target.getAbsolutePath());
                        } else {
                            System.out.println("Input: "+target.getAbsolutePath()+" already exists.");
                        }
                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        throw new IllegalStateException("Could not download input: " + i, e);
                    }
                });
    }

    private HttpClient createHttpClient() {
        try {
            var sessionCookie = new HttpCookie("session", sessionId);
            sessionCookie.setPath("/");
            sessionCookie.setVersion(0);
            var cookieManager = new CookieManager();
            cookieManager.getCookieStore().add(new URI("https://adventofcode.com/"), sessionCookie);
            return HttpClient.newBuilder().cookieHandler(cookieManager).build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}