package com.github.davidmoten.rtree2.geometry.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.rtree2.geometry.*;
import com.github.davidmoten.rtree2.internal.util.ObjectsHelper;

public final class CircleFloat implements Circle {

    private final float x, y, radius;
    private final Rectangle mbr;

    private CircleFloat(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = RectangleFloat.create(x - radius, y - radius, x + radius, y + radius);
    }

    public static CircleFloat create(float x, float y, float radius) {
        return new CircleFloat(x, y, radius);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double radius() {
        return radius;
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        return Math.max(0, GeometryUtil.distance(x, y, r) - radius);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return distance(r) == 0;
    }

    @Override
    public boolean intersects(Circle c) {
        double total = radius + c.radius();
        return GeometryUtil.distanceSquared(x, y, c.x(), c.y()) <= total * total;
    }

    @Override
    public boolean intersects(Polygon p) {
        return p.intersects(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, radius);
    }

    @Override
    public boolean equals(Object obj) {
        CircleFloat other = ObjectsHelper.asClass(obj, CircleFloat.class);
        if (other != null) {
            return Objects.equal(x, other.x) && Objects.equal(y, other.y)
                    && Objects.equal(radius, other.radius);
        } else
            return false;
    }

    @Override
    public boolean intersects(Point point) {
        return Math.sqrt(sqr(x - point.x()) + sqr(y - point.y())) <= radius;
    }

    private double sqr(double x) {
        return x * x;
    }

    @Override
    public boolean intersects(Line line) {
        return line.intersects(this);
    }

    @Override
    public boolean isDoublePrecision() {
        return false;
    }
}
