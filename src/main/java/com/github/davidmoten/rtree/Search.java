package com.github.davidmoten.rtree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.github.davidmoten.rtree.geometry.Geometry;

final class Search {

    static <T, S extends Geometry> Iterable<Entry<T, S>> search(Node<T, S> node,
            Predicate<? super Geometry> condition) {
        return new SearchIterable<T, S>(node, condition);
    }

    static final class SearchIterable<T, S extends Geometry> implements Iterable<Entry<T, S>> {

        private final Node<T, S> node;
        private final Predicate<? super Geometry> condition;

        SearchIterable(Node<T, S> node, Predicate<? super Geometry> condition) {
            this.node = node;
            this.condition = condition;
        }

        @Override
        public Iterator<Entry<T, S>> iterator() {
            return new SearchIterator<T, S>(node, condition);
        }

    }

    static final class SearchIterator<T, S extends Geometry> implements Iterator<Entry<T, S>> {

        private final Predicate<? super Geometry> condition;
        private final Deque<NodePositionMutable<T, S>> stack;
        private Entry<T, S> next;

        SearchIterator(Node<T, S> node, Predicate<? super Geometry> condition) {
            this.condition = condition;
            this.stack = new ArrayDeque<NodePositionMutable<T, S>>();
            stack.push(new NodePositionMutable<T, S>(node, 0));
        }

        @Override
        public boolean hasNext() {
            load();
            return next != null;
        }

        @Override
        public Entry<T, S> next() {
            load();
            if (next == null) {
                throw new NoSuchElementException();
            } else {
                Entry<T, S> v = next;
                next = null;
                return v;
            }
        }

        private void load() {
            if (next == null && stack != null) {
                next = search(condition, stack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

    }

    private static <S extends Geometry, T> Entry<T, S> search(final Predicate<? super Geometry> condition,
            Deque<NodePositionMutable<T, S>> stack) {
        Entry<T, S> v = null;
        while (!stack.isEmpty()) {
            NodePositionMutable<T, S> np = stack.peek();
            if (v != null) {
                return v;
            } else if (np.position() == np.node().count()) {
                // handle after last in node
                searchAfterLastInNode(stack);
            } else if (np.node() instanceof NonLeaf) {
                // handle non-leaf
                searchNonLeaf(condition, stack, np);
            } else {
                // handle leaf
                v = searchLeaf(condition, np);
            }
        }
        return v;
    }

    private static <T, S extends Geometry> Entry<T, S> searchLeaf(final Predicate<? super Geometry> condition,
            NodePositionMutable<T, S> np) {
        int i = np.position();
        do {
            Entry<T, S> entry = ((Leaf<T, S>) np.node()).entry(i);
            if (condition.test(entry.geometry())) {
                np.setPosition(i + 1);
                return entry;
            }
            i++;
        } while (i < np.node().count());
        np.setPosition(i);
        return null;
    }

    private static <S extends Geometry, T> void searchNonLeaf(final Predicate<? super Geometry> condition,
            Deque<NodePositionMutable<T, S>> stack, NodePositionMutable<T, S> np) {
        Node<T, S> child = ((NonLeaf<T, S>) np.node()).child(np.position());
        if (condition.test(child.geometry())) {
            stack.push(new NodePositionMutable<T, S>(child, 0));
        } else {
            stack.peek().setPosition(np.position() + 1);
        }
    }

    private static <S extends Geometry, T> void searchAfterLastInNode(Deque<NodePositionMutable<T, S>> stack) {
        stack.pop();
        if (!stack.isEmpty()) {
            NodePositionMutable<T, S> previous = stack.peek();
            previous.setPosition(previous.position() + 1);
        }
    }

}
