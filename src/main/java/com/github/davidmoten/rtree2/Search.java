package com.github.davidmoten.rtree2;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.github.davidmoten.rtree2.geometry.Geometry;

final class Search {

    private Search() {
        // prevent instantiation
    }

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
        private final Deque<NodePosition<T, S>> stack;
        private Entry<T, S> next;

        SearchIterator(Node<T, S> node, Predicate<? super Geometry> condition) {
            this.condition = condition;
            this.stack = new LinkedList<NodePosition<T, S>>();
            stack.push(new NodePosition<T, S>(node, 0));
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
            if (next == null) {
                next = search();
            }
        }

        private Entry<T, S> search() {
            while (!stack.isEmpty()) {
                NodePosition<T, S> np = stack.peek();
                if (!np.hasRemaining()) {
                    // handle after last in node
                    searchAfterLastInNode();
                } else if (np.node() instanceof NonLeaf) {
                    // handle non-leaf
                    searchNonLeaf(np);
                } else {
                    // handle leaf
                    Entry<T, S> v = searchLeaf(np);
                    if (v != null) {
                        return v;
                    }
                }
            }
            return null;
        }

        private Entry<T, S> searchLeaf(NodePosition<T, S> np) {
            int i = np.position();
            Leaf<T, S> leaf = (Leaf<T, S>) np.node();
            do {
                Entry<T, S> entry = leaf.entry(i);
                if (condition.test(entry.geometry())) {
                    np.setPosition(i + 1);
                    return entry;
                }
                i++;
            } while (i < leaf.count());
            np.setPosition(i);
            return null;
        }

        private void searchNonLeaf(NodePosition<T, S> np) {
            Node<T, S> child = ((NonLeaf<T, S>) np.node()).child(np.position());
            if (condition.test(child.geometry())) {
                stack.push(new NodePosition<T, S>(child, 0));
            } else {
                np.setPosition(np.position() + 1);
            }
        }

        private void searchAfterLastInNode() {
            stack.pop();
            if (!stack.isEmpty()) {
                NodePosition<T, S> previous = stack.peek();
                previous.setPosition(previous.position() + 1);
            }
        }

    }

}
