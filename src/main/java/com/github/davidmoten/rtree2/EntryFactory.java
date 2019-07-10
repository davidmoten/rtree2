package com.github.davidmoten.rtree2;

import com.github.davidmoten.rtree2.geometry.Geometry;

public interface EntryFactory<T,S extends Geometry> {
    Entry<T,S> createEntry(T value, S geometry);
}
