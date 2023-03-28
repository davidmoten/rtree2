package com.github.davidmoten.rtree2.geometry.internal;

import com.github.davidmoten.rtree2.geometry.*;
import com.github.davidmoten.rtree2.internal.util.ObjectsHelper;

import java.util.ArrayList;

/**
 * A polygon shell.
 */
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
        PointDouble lastPoint = PointDouble.create(coordinates[0], coordinates[1]);
        points.add(lastPoint);
        for (int i = 2; i < coordinates.length - 1; i += 2) {
            // be nice to users and just ignore duplicate points
            if (coordinates[i] == lastPoint.x() && coordinates[i + 1] == lastPoint.y()) continue;
            minX = Math.min(minX, coordinates[i]);
            maxX = Math.max(maxX, coordinates[i]);
            minY = Math.min(minY, coordinates[i + 1]);
            maxY = Math.max(maxY, coordinates[i + 1]);
            lastPoint = PointDouble.create(coordinates[i], coordinates[i + 1]);
            points.add(lastPoint);
        }
        // don't break if the polygon was closed
        if (points.get(0).equals(lastPoint))
            points.remove(points.size() - 1);
        // final check to make sure the polygon remains valid after removing duplicates
        if (points.size() < 3) throw new IllegalArgumentException("fewer than 3 unique polygon points provided");
        // currently the implementation is only for convex polygons
        if (!convexPoints()) throw new UnsupportedOperationException("only implemented for convex polygons");
        mbr = RectangleDouble.create(minX, minY, maxX, maxY);
    }

    /**
     * Constructor for a polygon shell.
     * @param coordinates An array of x1, y1, x2, y2, ... xn, yn that make the un-closed shell of a convex polygon.
     *                    As it is a polygon, at least 3 pairs of points are required (6 values).
     * @return convex polygon shell
     * @throws IllegalArgumentException if there are not enough unique points or there is an odd amount of points
     * @throws UnsupportedOperationException if the resulting polygon is not convex
     */
    public static PolygonDouble create(double[] coordinates) {
        return new PolygonDouble(coordinates);
    }

    @Override
    public Rectangle mbr() {
        return mbr;
    }

    @Override
    public double distance(Rectangle r) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean intersects(Rectangle r) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean intersects(Circle c) {
        throw new UnsupportedOperationException("not implemented");
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

        int firstDirection = pointsDirection(points.get(n - 1), point, points.get(0));
        if (firstDirection == 0)
            return true;

        for (int i = 0; i < n - 1; i++) {
            int direction = pointsDirection(points.get(i), point, points.get(i + 1));
            if (direction == 0) return true;
            if (direction != firstDirection) return false;
        }

        return true;
    }

    private boolean convexPoints() {
        int n = points.size();
        int overallDirection = 0;

        for (int i = 0; i < n - 1; i++) {
            int direction = pointsDirection(points.get(i), points.get((i + 2) % n), points.get(i + 1));
            if (direction != 0) {
                if (overallDirection == 0) overallDirection = direction;
                else if (direction != overallDirection) return false;
            }
        }
        return true;
    }

    @Override
    public boolean intersects(Line line) {
        // handle the case when the line is fully inside the polygon
        if (intersects(PointDouble.create(line.x1(), line.y1())))
            return true;
        // check if the line intersects any of the edges of this polygon
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

    /**
     * Return the orientation of point b relative to the path a -> c.
     * @param a origin point
     * @param b point to determine if it is to the left, right or along the path
     * @param c point we are facing from a
     * @return 0 if b is on the line a->c, -1 if b is to the left of a facing c or 1 if to the right.
     */
    private static int pointsDirection(Point a, Point b, Point c) {
        double crossProduct = (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
        if (Math.abs(crossProduct) < PRECISION) return 0;
        return crossProduct > 0.0 ? 1 : -1;
    }
}
