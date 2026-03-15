package com.example.tinyurl.util;

public final class Base62Codec {
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int BASE = ALPHABET.length;

    private Base62Codec() {
    }

    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be non-negative");
        }
        if (value == 0) {
            return String.valueOf(ALPHABET[0]);
        }

        StringBuilder builder = new StringBuilder();
        long current = value;
        while (current > 0) {
            int index = (int) (current % BASE);
            builder.append(ALPHABET[index]);
            current = current / BASE;
        }
        return builder.reverse().toString();
    }
}
