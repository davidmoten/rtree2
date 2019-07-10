package com.github.davidmoten.rtree2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class Iterables {

    private Iterables() {
        // prevent instantiation
    }

    public static <T> Iterable<T> filter(Iterable<? extends T> iterable, Predicate<? super T> condition) {
        return new FilterIterable<T>(iterable, condition);
    }

    static final class FilterIterable<T> implements Iterable<T> {

        private final Iterable<? extends T> iterable;
        private final Predicate<? super T> condition;

        FilterIterable(Iterable<? extends T> iterable, Predicate<? super T> condition) {
            this.iterable = iterable;
            this.condition = condition;
        }

        @Override
        public Iterator<T> iterator() {
            return new FilterIterator<T>(iterable.iterator(), condition);
        }

    }

    static final class FilterIterator<T> implements Iterator<T> {

        private Iterator<? extends T> it;
        private final Predicate<? super T> condition;
        private T next;

        FilterIterator(Iterator<? extends T> it, Predicate<? super T> condition) {
            this.it = it;
            this.condition = condition;
        }

        @Override
        public boolean hasNext() {
            load();
            return next != null;
        }

        @Override
        public T next() {
            load();
            if (next == null) {
                throw new NoSuchElementException();
            } else {
                T v = next;
                next = null;
                return v;
            }
        }

        private void load() {
            if (next == null && it != null) {
                while (it.hasNext()) {
                    T v = it.next();
                    if (condition.test(v)) {
                        next = v;
                        return;
                    }
                }
                it = null;
            }
        }

    }
    
    public static long size(Iterable<?> iterable) {
        Iterator<?> it = iterable.iterator();
        long count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }
    
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static boolean isEmpty(Iterable<?> entries) {
        return !entries.iterator().hasNext();
    }
    
}
