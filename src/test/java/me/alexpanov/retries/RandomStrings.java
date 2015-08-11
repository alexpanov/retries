package me.alexpanov.retries;

import java.util.Random;

final class RandomStrings {

    private Random random = new Random();

    public String createOne() {
        return String.valueOf(random.nextInt(Integer.MAX_VALUE));
    }
}
