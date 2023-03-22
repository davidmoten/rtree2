package com.github.davidmoten.rtree2.geometry.internal;

import com.github.davidmoten.rtree2.geometry.*;
import com.github.davidmoten.rtree2.internal.util.ObjectsHelper;

import java.util.ArrayList;

public final class PolygonDouble implements Polygon {

    private final ArrayList<PointDouble> points;
    private final Rectangle mbr;

    private static final double PRECISION = 0.00000001;

    private PolygonDouble(double[] coordinates) {
        if (coordinates.length % 2 != 0 || coordinates.length < 6)
            throw new IllegalArgumentException("expecting an even number of coordinate points of at least 6");

        points = new ArrayList<>();
        double minX = coordinates[0];
        double maxX = coordinates[0];
        double minY = coordinates[1];
        double maxY = coordinates[1];
        for (int i = 0; i < coordinates.length - 1; i += 2) {
            minX = Math.min(minX, coordinates[i]);
            maxX = Math.max(maxX, coordinates[i]);
            minY = Math.min(minY, coordinates[i + 1]);
            maxY = Math.max(maxY, coordinates[i + 1]);
            points.add(PointDouble.create(coordinates[i], coordinates[i + 1]));
        }
        mbr = RectangleDouble.create(minX, minY, maxX, maxY);
    }

    public static PolygonDouble create(double[] coordinates) {
        return new PolygonDouble(coordinates);
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
        if (intersects(PointDouble.create(line.x1(), line.y1())))
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
