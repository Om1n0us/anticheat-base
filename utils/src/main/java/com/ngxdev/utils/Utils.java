package com.ngxdev.utils;

import java.util.List;
import java.util.stream.Stream;

public class Utils {
    public static String join(Stream<String> list, String separator) {
        StringBuilder builder = new StringBuilder();
        list.forEach(e -> {
            if (builder.length() != 0) builder.append(separator);
            builder.append(e);
        });
        return builder.toString();
    }
}
