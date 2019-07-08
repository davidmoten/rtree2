package com.github.davidmoten.rtree;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.util.ImmutableStack;

import rx.functions.Func1;

public final class Iterables {

    public static <T, S extends Geometry> Iterable<Entry<T, S>> search(Node<T, S> node,
            Func1<? super Geometry, Boolean> condition) {
        return new SearchIterable<T, S>(node, condition);
    }

    public static final class SearchIterable<T, S extends Geometry> implements Iterable<Entry<T, S>> {

        private final Node<T, S> node;
        private final Func1<? super Geometry, Boolean> condition;

        public SearchIterable(Node<T, S> node, Func1<? super Geometry, Boolean> condition) {
            this.node = node;
            this.condition = condition;
        }

        @Override
        public Iterator<Entry<T, S>> iterator() {
            return new SearchIterator<T, S>(node, condition);
        }

    }

    public static final class SearchIterator<T, S extends Geometry> implements Iterator<Entry<T, S>> {

        private final Func1<? super Geometry, Boolean> condition;
        private ImmutableStack<NodePosition<T, S>> stack;
        private Entry<T, S> next;

        public SearchIterator(Node<T, S> node, Func1<? super Geometry, Boolean> condition) {
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
            final Func1<? super Geometry, Boolean> condition, Entry<T, S>[] result,
            final ImmutableStack<NodePosition<T, S>> stack) {
        StackAndRequest<NodePosition<T, S>> state = StackAndRequest.create(stack, 1);
        return searchAndReturnStack(condition, result, state);
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchAndReturnStack(
            final Func1<? super Geometry, Boolean> condition, Entry<T, S>[] result,
            StackAndRequest<NodePosition<T, S>> state) {

        while (!state.stack.isEmpty()) {
            NodePosition<T, S> np = state.stack.peek();
            if (result[0]!= null)
                return state.stack;
            else if (np.position() == np.node().count()) {
                // handle after last in node
                state = StackAndRequest.create(searchAfterLastInNode(state.stack), state.request);
            } else if (np.node() instanceof NonLeaf) {
                // handle non-leaf
                state = StackAndRequest.create(searchNonLeaf(condition, state.stack, np), state.request);
            } else {
                // handle leaf
                state = searchLeaf(condition, result, state, np);
            }
        }
        return state.stack;
    }

    private static class StackAndRequest<T> {
        private final ImmutableStack<T> stack;
        private final long request;

        StackAndRequest(ImmutableStack<T> stack, long request) {
            this.stack = stack;
            this.request = request;
        }

        static <T> StackAndRequest<T> create(ImmutableStack<T> stack, long request) {
            return new StackAndRequest<T>(stack, request);
        }

    }

    private static <T, S extends Geometry> StackAndRequest<NodePosition<T, S>> searchLeaf(
            final Func1<? super Geometry, Boolean> condition, Entry<T, S>[] result,
            StackAndRequest<NodePosition<T, S>> state, NodePosition<T, S> np) {
        final long nextRequest;
        Entry<T, S> entry = ((Leaf<T, S>) np.node()).entry(np.position());
        if (condition.call(entry.geometry())) {
            result[0] = entry;
            nextRequest = state.request - 1;
        } else
            nextRequest = state.request;
        return StackAndRequest.create(state.stack.pop().push(np.nextPosition()), nextRequest);
    }

    private static <S extends Geometry, T> ImmutableStack<NodePosition<T, S>> searchNonLeaf(
            final Func1<? super Geometry, Boolean> condition, ImmutableStack<NodePosition<T, S>> stack,
            NodePosition<T, S> np) {
        Node<T, S> child = ((NonLeaf<T, S>) np.node()).child(np.position());
        if (condition.call(child.geometry())) {
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
