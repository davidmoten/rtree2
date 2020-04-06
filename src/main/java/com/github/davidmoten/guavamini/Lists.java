package com.github.davidmoten.guavamini;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public final class Lists {

    private Lists() {
        // cannot instantiate
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        com.github.davidmoten.guavamini.Preconditions.checkNotNull(elements);
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    @VisibleForTesting
    static int computeArrayListCapacity(int arraySize) {
        com.github.davidmoten.guavamini.Preconditions.checkArgument(arraySize >= 0, "arraySize must be non-negative");

        // TODO(kevinb): Figure out the right behavior, and document it
        return saturatedCast(5L + arraySize + (arraySize / 10));
    }

    /**
     * Returns the {@code int} nearest in value to {@code value}.
     *
     * @param value
     *            any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the
     *         {@code int} type, {@link Integer#MAX_VALUE} if it is too large,
     *         or {@link Integer#MIN_VALUE} if it is too small
     */
    @VisibleForTesting
    static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        Preconditions.checkNotNull(elements); // for GWT
        // Let ArrayList's sizing logic work, if possible
        return (elements instanceof Collection) ? new ArrayList<E>(Collections2.cast(elements))
                : newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        Iterators.addAll(list, elements);
        return list;
    }
}
