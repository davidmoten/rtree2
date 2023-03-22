package com.github.davidmoten.rtree2.geometry.internal;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.rtree2.geometry.*;
import com.github.davidmoten.rtree2.geometry.internal.PointDouble;
import com.github.davidmoten.rtree2.internal.util.ObjectsHelper;

import java.util.ArrayList;

public final class PolygonDouble implements Polygon {

    private final ArrayList<PointDouble> points;
    private final Rectangle mbr;

    private static final double PRECISION = 0.00000001;

    private PolygonDouble(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        points = new ArrayList<>();
        points.add(PointDouble.create(x1, y1));
        points.add(PointDouble.create(x2, y2));
        points.add(PointDouble.create(x3, y3));
        points.add(PointDouble.create(x4, y4));
        mbr = RectangleDouble.create(Math.min(Math.min(x1, x2), Math.min(x3, x4)),
                                     Math.min(Math.min(y1, y2), Math.min(y3, y4)),
                                     Math.max(Math.max(x1, x2), Math.max(x3, x4)),
                                     Math.max(Math.max(y1, y2), Math.max(y3, y4)));
    }

    public static PolygonDouble create(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return new PolygonDouble(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean intersects(Rectangle r) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean intersects(Circle c) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int hashCode() {
        return points.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PolygonDouble other = ObjectsHelper.asClass(obj, PolygonDouble.class);
        if (other!=null) {
            return points.equals(other.points);
        } else
            return false;
    }

    @Override
    public boolean intersects(Point point) {
        int n = points.size();
        if (n < 3)
            return false;

        int firstSign = cosineSign(points.get(n - 1), point, points.get(0));
        if (firstSign == 0)
            return false;

        for (int i = 0; i < n - 1; i++) {
            int sign = cosineSign(points.get(i), point, points.get(i + 1));
            if (sign == 0 || sign != firstSign)
                return false;
        }

        return true;
    }

    @Override
    public boolean intersects(Line line) {
        if (intersects(Geometries.point(line.x1(), line.y1())))
            return true;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            PointDouble cur = points.get(i);
            PointDouble next = points.get((i + 1) % n);
            LineDouble edge = LineDouble.create(cur.x(), cur.y(), next.x(), next.y());
            if (line.intersects(edge))
                return true;
        }
        return false;
    }

    @Override
    public boolean isDoublePrecision() {
        return true;
    }

    private static int cosineSign(Point a, Point b, Point c) {
        double cosine = (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
        if (Math.abs(cosine) < PRECISION) return 0;
        return cosine > 0.0 ? 1 : -1;
    }
}
