package com.github.davidmoten.rtree2.internal;

import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree2.Context;
import com.github.davidmoten.rtree2.Entry;
import com.github.davidmoten.rtree2.Node;
import com.github.davidmoten.rtree2.NonLeaf;
import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.Rectangle;

public final class NonLeafDefault<T, S extends Geometry> implements NonLeaf<T, S> {

    private final List<? extends Node<T, S>> children;
    private final Rectangle mbr;
    private final Context<T, S> context;

    public NonLeafDefault(List<? extends Node<T, S>> children, Context<T, S> context) {
        Preconditions.checkArgument(!children.isEmpty());
        this.context = context;
        this.children = children;
        this.mbr = Util.mbr(children);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public int count() {
        return children.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return NonLeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafHelper.delete(entry, all, this);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Node<T, S> child(int i) {
        return children.get(i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Node<T, S>> children() {
        return (List<Node<T, S>>) children;
    }
}