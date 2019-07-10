package com.github.davidmoten.rtree2;

import com.github.davidmoten.rtree2.geometry.Geometry;

public interface Factory<T, S extends Geometry>
        extends LeafFactory<T, S>, NonLeafFactory<T, S>, EntryFactory<T,S> {
}
