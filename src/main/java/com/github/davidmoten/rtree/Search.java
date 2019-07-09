package com.github.davidmoten.rtree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.util.ImmutableStack;

class Search {

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
        private ImmutableStack<NodePosition<T, S>> stack;
        private Entry<T, S> next;

        public SearchIterator(Node<T, S> node, Predicate<? super Geometry> condition) {
            this.condition = condition;
            this.stack = ImmutableStack.create(new NodePosition<T, S>(node, 0));
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
                @SuppressWarnings("unchecked")
                Entry<T, S>[] result = new Entry[1];
                stack = search(condition, result, stack);
                if (stack.isEmpty()) {
                    stack = null;
                    return;
                }
                next = result[0];
            }
        }

    }

    static <T, S extends Geometry> ImmutableStack<NodePosition<T, S>> search(
            final Predicate<? super Geometry> condition, Entry<T, S>[] result,
            final ImmutableStack<NodePosition<T, S>> stack) {
        return searchAndReturnStack(condition, result, stack);
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchAndReturnStack(
            final Predicate<? super Geometry> condition, Entry<T, S>[] result,
            ImmutableStack<NodePosition<T, S>> stack) {

        while (!stack.isEmpty()) {
            NodePosition<T, S> np = stack.peek();
            if (result[0] != null)
                return stack;
            else if (np.position() == np.node().count()) {
                // handle after last in node
                stack = searchAfterLastInNode(stack);
            } else if (np.node() instanceof NonLeaf) {
                // handle non-leaf
                stack = searchNonLeaf(condition, stack, np);
            } else {
                // handle leaf
                stack = searchLeaf(condition, result, stack, np);
            }
        }
        return stack;
    }

    private static <T, S extends Geometry> ImmutableStack<NodePosition<T, S>> searchLeaf(
            final Predicate<? super Geometry> condition, Entry<T, S>[] result, ImmutableStack<NodePosition<T, S>> stack,
            NodePosition<T, S> np) {
        Entry<T, S> entry = ((Leaf<T, S>) np.node()).entry(np.position());
        if (condition.test(entry.geometry())) {
            result[0] = entry;
        }
        return stack.pop().push(np.nextPosition());
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchNonLeaf(
            final Predicate<? super Geometry> condition, ImmutableStack<NodePosition<T, S>> stack,
            NodePosition<T, S> np) {
        Node<T, S> child = ((NonLeaf<T, S>) np.node()).child(np.position());
        if (condition.test(child.geometry())) {
            stack = stack.push(new NodePosition<T, S>(child, 0));
        } else {
            stack = stack.pop().push(np.nextPosition());
        }
        return stack;
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchAfterLastInNode(
            ImmutableStack<NodePosition<T, S>> stack) {
        ImmutableStack<NodePosition<T, S>> stack2 = stack.pop();
        if (stack2.isEmpty())
            stack = stack2;
        else {
            NodePosition<T, S> previous = stack2.peek();
            stack = stack2.pop().push(previous.nextPosition());
        }
        return stack;
    }

}
