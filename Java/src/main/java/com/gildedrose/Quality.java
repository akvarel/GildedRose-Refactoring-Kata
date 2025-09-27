package com.gildedrose;

public final class Quality {
    static final int MIN = 0;
    static final int MAX = 50;
    static int clamp(int q) {
        return Math.max(MIN, Math.min(MAX, q));
    }
}
