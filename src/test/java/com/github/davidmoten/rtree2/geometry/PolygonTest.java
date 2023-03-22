package com.github.davidmoten.rtree2.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class PolygonTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDoesIntersectHorizontalLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(-2, 0, 2, 0);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectVerticalLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(0.5, -5.0, 0.5, 10.1);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectArbitraryLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(-1.2, 5.0, 0.5, -2.5);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectHorizontalLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(-0.5, 5, 0.5, 5);
        assertTrue(!a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectVerticalLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(-4, 0, -4, 5);
        assertTrue(!a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectArbitraryLine() {
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(0.1, 2.2, 10.7, 3.1);
        assertTrue(!a.intersects(b));
    }

    @Test
    public void testLineIsNotInfinite() {
        // Check that line is treated like a segment rather than an infinite line
        Polygon a = Geometries.polygon(-1, -1, -1, 1, 1, 1, 1, -1);
        Line b = Geometries.line(0.5, 5.0, 0.5, 10.1);
        assertTrue(!a.intersects(b));
    }

    @Test
    public void testDoesIntersectPoint() {
        Polygon a = Geometries.polygon(-3, 0, 1, 4, 2, -5, -2, -10);
        Point b = Geometries.point(0.5, 1.2);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectPoint() {
        Polygon a = Geometries.polygon(-3, 0, 1, 4, 2, -5, -2, -10);
        Point b = Geometries.point(-2.5, 3.2);
        assertTrue(!a.intersects(b));
    }

    @Test
    public void testPolygonMbr() {
        Polygon a = Geometries.polygon(-3, 0, 1, 4, 2, -5, -2, -10);
        Rectangle mbr = a.mbr();
        assertEquals(-3, mbr.x1(), PRECISION);
        assertEquals(-10, mbr.y1(), PRECISION);
        assertEquals(2, mbr.x2(), PRECISION);
        assertEquals(4, mbr.y2(), PRECISION);
    }
}
