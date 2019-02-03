package com.nvlad.tinypng.services;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinyPNGErrorInfo {
    private static Pattern errorPattern = Pattern.compile("(.+)\\s\\(HTTP (\\d+)/(.+)\\)");

    public String message;
    public int code;
    public String httpMessage;

    @Nullable
    public static TinyPNGErrorInfo parse(String errorMessage) {
        final Matcher matcher = errorPattern.matcher(errorMessage);
        TinyPNGErrorInfo error = null;
        if (matcher.matches()) {
            error = new TinyPNGErrorInfo();
            error.message = matcher.group(1);
            error.httpMessage = matcher.group(3);
            error.code = Integer.parseInt(matcher.group(2));
        }

        return error;
    }

    @Override
    public String toString() {
        return String.format("%s (HTTP %d/%s)", message, code, message);
    }
}
