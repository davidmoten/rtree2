package com.github.davidmoten.rtree2;

import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    T value();

    @Override
    S geometry();

}