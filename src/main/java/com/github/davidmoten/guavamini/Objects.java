package com.github.davidmoten.guavamini;

import java.util.Arrays;

public final class Objects {

    private Objects() {
        // prevent instantiation
    }

    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
