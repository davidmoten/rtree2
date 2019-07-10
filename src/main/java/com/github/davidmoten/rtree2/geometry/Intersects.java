package com.github.davidmoten.rtree2.geometry;

import java.util.function.BiPredicate;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final BiPredicate<Rectangle, Circle> rectangleIntersectsCircle = new BiPredicate<Rectangle, Circle>() {
        @Override
        public boolean test(Rectangle rectangle, Circle circle) {
            return circleIntersectsRectangle.test(circle, rectangle);
        }
    };

    public static final BiPredicate<Circle, Rectangle> circleIntersectsRectangle = new BiPredicate<Circle, Rectangle>() {
        @Override
        public boolean test(Circle circle, Rectangle rectangle) {
            return circle.intersects(rectangle);
        }
    };

    public static final BiPredicate<Point, Circle> pointIntersectsCircle = new BiPredicate<Point, Circle>() {
        @Override
        public boolean test(Point point, Circle circle) {
            return circleIntersectsPoint.test(circle, point);
        }
    };

    public static final BiPredicate<Circle, Point> circleIntersectsPoint = new BiPredicate<Circle, Point>() {
        @Override
        public boolean test(Circle circle, Point point) {
            return circle.intersects(point);
        }
    };

    public static final BiPredicate<Circle, Circle> circleIntersectsCircle = new BiPredicate<Circle, Circle>() {
        @Override
        public boolean test(Circle a, Circle b) {
            return a.intersects(b);
        }
    };

    public static final BiPredicate<Line, Line> lineIntersectsLine = new BiPredicate<Line, Line>() {
        @Override
        public boolean test(Line a, Line b) {
            return a.intersects(b);
        }
    };

    public static final BiPredicate<Line, Rectangle> lineIntersectsRectangle = new BiPredicate<Line, Rectangle>() {
        @Override
        public boolean test(Line a, Rectangle r) {
            return rectangleIntersectsLine.test(r, a);
        }
    };

    public static final BiPredicate<Rectangle, Line> rectangleIntersectsLine = new BiPredicate<Rectangle, Line>() {
        @Override
        public boolean test(Rectangle r, Line a) {
            return a.intersects(r);
        }
    };

    public static final BiPredicate<Line, Circle> lineIntersectsCircle = new BiPredicate<Line, Circle>() {
        @Override
        public boolean test(Line a, Circle c) {
            return circleIntersectsLine.test(c, a);
        }
    };

    public static final BiPredicate<Circle, Line> circleIntersectsLine = new BiPredicate<Circle, Line>() {
        @Override
        public boolean test(Circle c, Line a) {
            return a.intersects(c);
        }
    };

    public static final BiPredicate<Line, Point> lineIntersectsPoint = new BiPredicate<Line, Point>() {

        @Override
        public boolean test(Line line, Point point) {
            return pointIntersectsLine.test(point, line);
        }
    };

    public static final BiPredicate<Point, Line> pointIntersectsLine = new BiPredicate<Point, Line>() {

        @Override
        public boolean test(Point point, Line line) {
            return line.intersects(point);
        }
    };

    public static final BiPredicate<Geometry, Line> geometryIntersectsLine = new BiPredicate<Geometry, Line>() {

        @Override
        public boolean test(Geometry geometry, Line line) {
            if (geometry instanceof Line)
                return line.intersects((Line) geometry);
            else if (geometry instanceof Circle)
                return line.intersects((Circle) geometry);
            else if (geometry instanceof Point)
                return line.intersects((Point) geometry);
            else if (geometry instanceof Rectangle)
                return line.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final BiPredicate<Geometry, Circle> geometryIntersectsCircle = new BiPredicate<Geometry, Circle>() {

        @Override
        public boolean test(Geometry geometry, Circle circle) {
            if (geometry instanceof Line)
                return circle.intersects((Line) geometry);
            else if (geometry instanceof Circle)
                return circle.intersects((Circle) geometry);
            else if (geometry instanceof Point)
                return circle.intersects((Point) geometry);
            else if (geometry instanceof Rectangle)
                return circle.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final BiPredicate<Circle, Geometry> circleIntersectsGeometry = new BiPredicate<Circle, Geometry>() {

        @Override
        public boolean test(Circle circle, Geometry geometry) {
            return geometryIntersectsCircle.test(geometry, circle);
        }
    };

    public static final BiPredicate<Geometry, Rectangle> geometryIntersectsRectangle = new BiPredicate<Geometry, Rectangle>() {

        @Override
        public boolean test(Geometry geometry, Rectangle r) {
            if (geometry instanceof Line)
                return geometry.intersects(r);
            else if (geometry instanceof Circle)
                return geometry.intersects(r);
            else if (geometry instanceof Point)
                return geometry.intersects(r);
            else if (geometry instanceof Rectangle)
                return r.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final BiPredicate<Rectangle, Geometry> rectangleIntersectsGeometry = new BiPredicate<Rectangle, Geometry>() {

        @Override
        public boolean test(Rectangle r, Geometry geometry) {
            return geometryIntersectsRectangle.test(geometry, r);
        }
    };

    public static final BiPredicate<Geometry, Point> geometryIntersectsPoint = new BiPredicate<Geometry, Point>() {

        @Override
        public boolean test(Geometry geometry, Point point) {
            return geometryIntersectsRectangle.test(geometry, point.mbr());
        }
    };

    public static final BiPredicate<Point, Geometry> pointIntersectsGeometry = new BiPredicate<Point, Geometry>() {

        @Override
        public boolean test(Point point, Geometry geometry) {
            return geometryIntersectsPoint.test(geometry, point);
        }
    };

}
