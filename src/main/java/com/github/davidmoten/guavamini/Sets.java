package com.github.davidmoten.guavamini;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public final class Sets {

    private Sets() {
        // prevent instantiation
    }

    static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    public static <E> HashSet<E> newHashSet(E... elements) {
        Preconditions.checkNotNull(elements);
        HashSet<E> set = newHashSetWithExpectedSize(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<E>(capacity(expectedSize));
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized
     * as long as it grows no larger than expectedSize and the load factor is >=
     * its default (0.75).
     */
    @VisibleForTesting
    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            checkNonnegative(expectedSize, "expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE; // any large value
    }

    @VisibleForTesting
    static int checkNonnegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
        return (elements instanceof Collection) ? new HashSet<E>(Collections2.cast(elements))
                : newHashSet(elements.iterator());
    }

    public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
        HashSet<E> set = newHashSet();
        Iterators.addAll(set, elements);
        return set;
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

}
