package com.github.davidmoten.rtree2;

import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.internal.FactoryDefault;

public final class Factories {

    private Factories() {
        // prevent instantiation
    }

    public static <T, S extends Geometry> Factory<T, S> defaultFactory() {
        return FactoryDefault.instance();
    }
}
