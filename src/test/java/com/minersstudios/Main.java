package com.minersstudios;

import com.minersstudios.mscore.annotation.Key;

public final class Main {

    public static void main(final String[] args) {
        System.out.println("Hello, PackmanDude!");

        final String statusKey = "status_key";

        System.out.println(Key.Validator.matches(statusKey));

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000000; ++i) {
            Key.Validator.matches(statusKey);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (endTime - startTime) + "ms");
    }
}
