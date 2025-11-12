package com.rasberry.rasberry_api_management.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class LineHelper {

    private static final Pattern percentPattern = Pattern.compile("(\\d+)%");
    private static final Pattern etaPattern = Pattern.compile("ETA\\s+([0-9hms]+)");

    public static String getProgressRclone(String line) {
        Matcher percentMatcher = percentPattern.matcher(line);
        Matcher etaMatcher = etaPattern.matcher(line);

        String percent = percentMatcher.find() ? percentMatcher.group(1) : "N/A";
        String eta = etaMatcher.find() ? etaMatcher.group(1) : "N/A";

        return format("Progress: %s%% | ETA: %s", percent, eta);

    }
}
