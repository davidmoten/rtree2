package com.github.davidmoten.guavamini;

import java.util.Collection;
import java.util.Iterator;

public final class Iterators {

    private Iterators() {
        // prevent instantiation
    }

    /**
     * Adds all elements in {@code iterator} to {@code collection}. The iterator
     * will be left exhausted: its {@code hasNext()} method will return
     * {@code false}.
     *
     * @param addTo
     *            collection to add to
     * @param iterator
     *            iterator whose elements will be added to the collection
     * @param <T>
     *            generic type of collection
     * @return {@code true} if {@code collection} was modified as a result of
     *         this operation
     */
    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        com.github.davidmoten.guavamini.Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

}
