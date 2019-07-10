package com.github.davidmoten.rtree2;

import java.util.List;

import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.HasGeometry;
import com.github.davidmoten.rtree2.internal.NodeAndEntries;

public interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    int count();

    Context<T, S> context();

}
