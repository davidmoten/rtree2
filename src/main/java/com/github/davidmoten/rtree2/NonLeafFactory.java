package com.github.davidmoten.rtree2;

import java.util.List;

import com.github.davidmoten.rtree2.geometry.Geometry;

public interface NonLeafFactory<T, S extends Geometry> {

    NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context);
}
