package com.github.davidmoten.rtree.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ImmutableStack<T> implements Iterable<T> {

    private final T head;
    private final ImmutableStack<T> tail;

    private static ImmutableStack<?> EMPTY = new ImmutableStack<Object>();

    private ImmutableStack(T head, ImmutableStack<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static <T> ImmutableStack<T> create(T t) {
        return new ImmutableStack<T>(t, empty());
    }

    public ImmutableStack() {
        this(null, null);
    }

    @SuppressWarnings("unchecked")
    public static <S> ImmutableStack<S> empty() {
        return (ImmutableStack<S>) EMPTY;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public T peek() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        return head;
    }

    public ImmutableStack<T> pop() {
        if (tail == null) {
            throw new NoSuchElementException();
        }
        return tail;
    }

    public ImmutableStack<T> push(T value) {
        return new ImmutableStack<T>(value, this);
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator<T>(this);
    }

    private static class StackIterator<U> implements Iterator<U> {
        private ImmutableStack<U> stack;

        public StackIterator(final ImmutableStack<U> stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override
        public U next() {
            final U result = this.stack.peek();
            this.stack = this.stack.pop();
            return result;
        }

        @Override
        public void remove() {
            throw new RuntimeException("not supported");
        }
    }

}