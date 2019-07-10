package com.github.davidmoten.rtree2.geometry;

public interface Circle extends Geometry {

    double x();

    double y();

    double radius();

    boolean intersects(Circle c);

    boolean intersects(Point point);

    boolean intersects(Line line);

}