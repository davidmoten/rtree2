package com.github.davidmoten.rtree2.geometry;

public interface Polygon extends Geometry {

    boolean intersects(Line b);

    boolean intersects(Point point);

    boolean intersects(Circle circle);

}
