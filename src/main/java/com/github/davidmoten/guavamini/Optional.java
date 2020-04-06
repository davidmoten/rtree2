package com.github.davidmoten.guavamini;

public final class Optional<T> {

    private final T value;
    private final boolean present;

    private Optional(T value, boolean present) {
        this.value = value;
        this.present = present;
    }

    private Optional() {
        //no-arg constructor to enable kryo (a bit yukky but not a big deal)
        this(null, false);
    }

    public boolean isPresent() {
        return present;
    }

    public T get() {
        if (present)
            return value;
        else
            throw new NotPresentException();
    }

    public T or(T alternative) {
        if (present)
            return value;
        else
            return alternative;
    }

    public static <T> Optional<T> fromNullable(T t) {
        if (t == null)
            return Optional.absent();
        else
            return Optional.of(t);
    }

    public static <T> Optional<T> of(T t) {
        return new Optional<T>(t, true);
    }

    public static <T> Optional<T> absent() {
        return new Optional<T>();
    }

    public static class NotPresentException extends RuntimeException {

        private static final long serialVersionUID = -4444814681271790328L;

    }

    @Override
    public String toString() {
        return present ? String.format("Optional.of(%s)", value) : "Optional.absent";
    }
}
