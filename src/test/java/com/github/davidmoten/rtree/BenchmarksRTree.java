package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Utilities.entries1000;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

@State(Scope.Benchmark)
public class BenchmarksRTree {

    private final static Precision precision = Precision.DOUBLE;

    private final List<Entry<Object, Point>> entries = GreekEarthquakes.entriesList(precision);

    private final List<Entry<Object, Rectangle>> some = entries1000(precision);

    private final RTree<Object, Point> defaultTreeM4 = RTree.maxChildren(4).<Object, Point>create().add(entries);

    private final RTree<Object, Point> defaultTreeM10 = RTree.maxChildren(10).<Object, Point>create().add(entries);

    private final RTree<Object, Point> starTreeM4 = RTree.maxChildren(4).star().<Object, Point>create().add(entries);

    private final RTree<Object, Point> starTreeM10 = RTree.maxChildren(10).star().<Object, Point>create().add(entries);

    private final RTree<Object, Point> defaultTreeM32 = RTree.maxChildren(32).<Object, Point>create().add(entries);

    private final RTree<Object, Point> starTreeM32 = RTree.maxChildren(32).star().<Object, Point>create().add(entries);

    private final RTree<Object, Point> defaultTreeM128 = RTree.maxChildren(128).<Object, Point>create().add(entries);

    private final RTree<Object, Point> starTreeM128 = RTree.maxChildren(128).star().<Object, Point>create()
            .add(entries);

    private final RTree<Object, Rectangle> smallDefaultTreeM4 = RTree.maxChildren(4).<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallDefaultTreeM10 = RTree.maxChildren(10).<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallStarTreeM4 = RTree.maxChildren(4).star().<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallStarTreeM10 = RTree.maxChildren(10).star().<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallDefaultTreeM32 = RTree.maxChildren(32).<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallStarTreeM32 = RTree.maxChildren(32).star().<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallDefaultTreeM128 = RTree.maxChildren(128).<Object, Rectangle>create()
            .add(some);

    private final RTree<Object, Rectangle> smallStarTreeM128 = RTree.maxChildren(128).star().<Object, Rectangle>create()
            .add(some);

    @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        return insertPoint(defaultTreeM4);
    }

    @Benchmark
    public RTree<Object, Point> defaultRTreeCreation010() {
        return RTree.maxChildren(10).<Object, Point>create().add(entries);
    }

    @Benchmark
    public RTree<Object, Point> starRTreeCreation010() {
        return RTree.maxChildren(10).star().<Object, Point>create().add(entries);
    }

    @Benchmark
    public RTree<Object, Point> bulkLoadingRTreeCreation010() {
        return RTree.maxChildren(10).<Object, Point>create(entries);
    }

    @Benchmark
    public RTree<Object, Point> bulkLoadingFullRTreeCreation010() {
        return RTree.maxChildren(10).loadingFactor(1.0).<Object, Point>create(entries);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren004(Blackhole bh) {
        searchGreek(defaultTreeM4, bh);
    }

    @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        return insertPoint(defaultTreeM10);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren010(Blackhole bh) {
        searchGreek(defaultTreeM10, bh);
    }

    @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        return insertPoint(starTreeM4);
    }

    @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        return insertPoint(starTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren004(Blackhole bh) {
        searchGreek(starTreeM4, bh);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren010(Blackhole bh) {
        searchGreek(starTreeM10, bh);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren010WithBackpressure(Blackhole bh) {
        searchGreekWithBackpressure(starTreeM10, bh);
    }

    @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        return insertPoint(defaultTreeM32);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren032(Blackhole bh) {
        searchGreek(defaultTreeM32, bh);
    }

    @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        return insertPoint(starTreeM32);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren032(Blackhole bh) {
        searchGreek(starTreeM32, bh);
    }

    @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        return insertPoint(defaultTreeM128);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren128(Blackhole bh) {
        searchGreek(defaultTreeM128, bh);
    }

    @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        return insertPoint(starTreeM128);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren128(Blackhole bh) {
        searchGreek(starTreeM128, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        return insertRectangle(smallDefaultTreeM4);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren004(Blackhole bh) {
        search(smallDefaultTreeM4, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        return insertRectangle(smallDefaultTreeM10);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren010(Blackhole bh) {
        search(smallDefaultTreeM10, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        return insertRectangle(smallStarTreeM4);
    }

    @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        return insertRectangle(smallStarTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren004(Blackhole bh) {
        search(smallStarTreeM4, bh);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren010(Blackhole bh) {
        search(smallStarTreeM10, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        return insertRectangle(smallDefaultTreeM32);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren032(Blackhole bh) {
        search(smallDefaultTreeM32, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        return insertRectangle(smallStarTreeM32);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren032(Blackhole bh) {
        search(smallStarTreeM32, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        return insertRectangle(smallDefaultTreeM128);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren128(Blackhole bh) {
        search(smallDefaultTreeM128, bh);
    }

    @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        return insertRectangle(smallStarTreeM128);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren128(Blackhole bh) {
        search(smallStarTreeM128, bh);
    }

    @Benchmark
    public void rStarTreeDeleteOneEveryOccurrenceFromGreekDataChildren010() {
        deleteAll(starTreeM10);
    }

    @Benchmark
    public void searchNearestGreek(Blackhole bh) {
        searchNearestGreek(starTreeM4, bh);
    }

    private RTree<Object, Point> deleteAll(RTree<Object, Point> tree) {
        return tree.delete(entries.get(1000), true);
    }

    private void search(RTree<Object, Rectangle> tree, Blackhole bh) {
        // returns 10 results
        consume(tree.search(Geometries.rectangle(500, 500, 630, 630)), bh);
    }

    private void searchGreek(RTree<Object, Point> tree, Blackhole bh) {
        // should return 22 results
        consume(tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)), bh);
    }

    private void consume(Iterable<?> iterable, Blackhole bh) {
        for (Object o : iterable) {
            bh.consume(o);
        }
    }

    private static Rectangle searchRectangle() {
        final Rectangle r;
        if (precision == Precision.DOUBLE) {
            r = Geometries.rectangle(40, 27.0, 40.5, 27.5);
        } else {
            r = Geometries.rectangle(40f, 27.0f, 40.5f, 27.5f);
        }
        return r;
    }

    private void searchNearestGreek(RTree<Object, Point> tree, Blackhole bh) {
        final Point p;
        if (precision == Precision.DOUBLE) {
            p = Geometries.point(40.0, 27.0);
        } else {
            p = Geometries.point(40.0f, 27.0f);
        }
        consume(tree.nearest(p, 1, 300), bh);
    }

    private void searchGreekWithBackpressure(RTree<Object, Point> tree, final Blackhole bh) {
        // should return 22 results
        consume(tree.search(searchRectangle()), bh);
    }

    private RTree<Object, Rectangle> insertRectangle(RTree<Object, Rectangle> tree) {
        return tree.add(new Object(), RTreeTest.random(precision));
    }

    private RTree<Object, Point> insertPoint(RTree<Object, Point> tree) {
        if (precision == Precision.DOUBLE) {
            return tree.add(new Object(), Geometries.point(Math.random() * 1000, Math.random() * 1000));
        } else {
            return tree.add(new Object(), Geometries.point((float) Math.random() * 1000, (float) Math.random() * 1000));
        }
    }

}
