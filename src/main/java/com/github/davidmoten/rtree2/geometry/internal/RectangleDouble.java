package com.github.davidmoten.rtree2.geometry.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.Rectangle;
import com.github.davidmoten.rtree2.internal.util.ObjectsHelper;

public final class RectangleDouble implements Rectangle {
    private final double x1, y1, x2, y2;

    private RectangleDouble(double x1, double y1, double x2, double y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static RectangleDouble create(double x1, double y1, double x2, double y2) {
        return new RectangleDouble(x1, y1, x2, y2);
    }

    @Override
    public double x1() {
        return x1;
    }

    @Override
    public double y1() {
        return y1;
    }

    @Override
    public double x2() {
        return x2;
    }

    @Override
    public double y2() {
        return  y2;
    }

    @Override
    public Rectangle add(Rectangle r) {
        return new RectangleDouble(min(x1, r.x1()), min(y1, r.y1()), max(x2, r.x2()),
                max(y2, r.y2()));
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return GeometryUtil.intersects(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public double distance(Rectangle r) {
        return GeometryUtil.distance(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public String toString() {
        return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        RectangleDouble other = ObjectsHelper.asClass(obj, RectangleDouble.class);
        if (other != null) {
            return Objects.equal(x1, other.x1) && Objects.equal(x2, other.x2)
                    && Objects.equal(y1, other.y1) && Objects.equal(y2, other.y2);
        } else
            return false;
    }

    @Override
    public double intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else {
            return create(max(x1, r.x1()), max(y1, r.y1()), min(x2, r.x2()), min(y2, r.y2()))
                    .area();
        }
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    private static double max(double a, double b) {
        if (a < b)
            return b;
        else
            return a;
    }

    private static double min(double a, double b) {
        if (a < b)
            return a;
        else
            return b;
    }

    @Override
    public double perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    @Override
    public double area() {
        return (x2 - x1) * (y2 - y1);
    }

    @Override
    public boolean isDoublePrecision() {
        return true;
    }

}
