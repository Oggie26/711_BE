package com.example._1.util;

public class SlugUtil {

    public static String toSlug(String input) {
        return input == null ? null :
                input.toLowerCase()
                        .replaceAll("[^a-z0-9\\s-]", "")
                        .replaceAll("\\s+", "-")
                        .replaceAll("-+", "-")
                        .trim();
    }
}