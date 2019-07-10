package com.github.davidmoten.rtree;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.geometry.Geometry;

final class NodePositionMutable<T, S extends Geometry> {

    private Node<T, S> node;
    private int position;

    NodePositionMutable(Node<T, S> node, int position) {
        Preconditions.checkNotNull(node);
        this.node = node;
        this.position = position;
    }

    Node<T, S> node() {
        return node;
    }

    int position() {
        return position;
    }
    
    void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        String builder = "NodePosition [node=" +
                node +
                ", position=" +
                position +
                "]";
        return builder;
    }

}
