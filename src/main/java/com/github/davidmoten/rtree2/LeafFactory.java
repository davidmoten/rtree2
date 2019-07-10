package com.github.davidmoten.rtree2;

import java.util.List;

import com.github.davidmoten.rtree2.geometry.Geometry;

public interface LeafFactory<T, S extends Geometry> {
    Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context);
}
