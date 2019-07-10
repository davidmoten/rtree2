package com.github.davidmoten.rtree2.internal;

import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.davidmoten.rtree2.Context;
import com.github.davidmoten.rtree2.Entry;
import com.github.davidmoten.rtree2.Leaf;
import com.github.davidmoten.rtree2.Node;
import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.ListPair;

public final class LeafHelper {

    private LeafHelper() {
        // prevent instantiation
    }

    public static <T, S extends Geometry> NodeAndEntries<T, S> delete(
            Entry<? extends T, ? extends S> entry, boolean all, Leaf<T, S> leaf) {
        List<Entry<T, S>> entries = leaf.entries();
        if (!entries.contains(entry)) {
            return new NodeAndEntries<T, S>(of(leaf), Collections.<Entry<T, S>> emptyList(), 0);
        } else {
            final List<Entry<T, S>> entries2 = new ArrayList<Entry<T, S>>(entries);
            entries2.remove(entry);
            int numDeleted = 1;
            // keep deleting if all specified
            while (all && entries2.remove(entry))
                numDeleted += 1;

            if (entries2.size() >= leaf.context().minChildren()) {
                Leaf<T, S> node = leaf.context().factory().createLeaf(entries2, leaf.context());
                return new NodeAndEntries<T, S>(of(node), Collections.<Entry<T, S>> emptyList(),
                        numDeleted);
            } else {
                return new NodeAndEntries<T, S>(Optional.<Node<T, S>> empty(), entries2,
                        numDeleted);
            }
        }
    }

    public static <T, S extends Geometry> List<Node<T, S>> add(
            Entry<? extends T, ? extends S> entry, Leaf<T, S> leaf) {
        List<Entry<T, S>> entries = leaf.entries();
        Context<T, S> context = leaf.context();
        @SuppressWarnings("unchecked")
        final List<Entry<T, S>> entries2 = Util.add(entries, (Entry<T, S>) entry);
        if (entries2.size() <= context.maxChildren())
            return Collections
                    .singletonList((Node<T, S>) context.factory().createLeaf(entries2, context));
        else {
            ListPair<Entry<T, S>> pair = context.splitter().split(entries2, context.minChildren());
            return makeLeaves(pair, context);
        }
    }

    private static <T, S extends Geometry> List<Node<T, S>> makeLeaves(ListPair<Entry<T, S>> pair,
            Context<T, S> context) {
        List<Node<T, S>> list = new ArrayList<Node<T, S>>(2);
        list.add(context.factory().createLeaf(pair.group1().list(), context));
        list.add(context.factory().createLeaf(pair.group2().list(), context));
        return list;
    }

}
