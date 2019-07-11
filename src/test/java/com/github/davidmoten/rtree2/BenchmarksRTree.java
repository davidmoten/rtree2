package com.github.davidmoten.rtree2;

import static com.github.davidmoten.rtree2.Utilities.entries1000;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Point;
import com.github.davidmoten.rtree2.geometry.Rectangle;

@State(Scope.Benchmark)
public class BenchmarksRTree {

    private static final Rectangle DEFAULT_1000_RECTANGLE = Geometries.rectangle(500, 500, 630, 630);

    private static final Rectangle DEFAULT_GREEK_RECTANGLE = Geometries.rectangle(40, 27.0, 40.5, 27.5);

    private static final Precision precision = Precision.DOUBLE;

    private static final List<Entry<Object, Point>> entries = GreekEarthquakes.entriesList(precision);

    private static final List<Entry<Object, Rectangle>> some = entries1000(precision);

    private static final RTree<Object, Point> defaultTreeM4 = RTree.maxChildren(4).<Object, Point>create().add(entries);

    private static final RTree<Object, Point> defaultTreeM10 = RTree.maxChildren(10).<Object, Point>create().add(entries);

    private static final RTree<Object, Point> starTreeM4 = RTree.maxChildren(4).star().<Object, Point>create().add(entries);

    private static final RTree<Object, Point> starTreeM10 = RTree.maxChildren(10).star().<Object, Point>create().add(entries);

    private static final RTree<Object, Point> defaultTreeM32 = RTree.maxChildren(32).<Object, Point>create().add(entries);

    private static final RTree<Object, Point> starTreeM32 = RTree.maxChildren(32).star().<Object, Point>create().add(entries);

    private static final RTree<Object, Point> defaultTreeM128 = RTree.maxChildren(128).<Object, Point>create().add(entries);

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

    // @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        return insertPoint(defaultTreeM4);
    }

    // @Benchmark
    public RTree<Object, Point> defaultRTreeCreation010() {
        return RTree.maxChildren(10).<Object, Point>create().add(entries);
    }

    // @Benchmark
    public RTree<Object, Point> starRTreeCreation010() {
        return RTree.maxChildren(10).star().<Object, Point>create().add(entries);
    }

    // @Benchmark
    public RTree<Object, Point> bulkLoadingRTreeCreation010() {
        return RTree.maxChildren(10).<Object, Point>create(entries);
    }

    // @Benchmark
    public RTree<Object, Point> bulkLoadingFullRTreeCreation010() {
        return RTree.maxChildren(10).loadingFactor(1.0).<Object, Point>create(entries);
    }

    // @Benchmark
    public long defaultRTreeSearchOfGreekDataPointsMaxChildren004() {
        return searchGreek(defaultTreeM4);
    }

    // @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        return insertPoint(defaultTreeM10);
    }

    // @Benchmark
    public long defaultRTreeSearchOfGreekDataPointsMaxChildren010() {
        return searchGreek(defaultTreeM10);
    }

    // @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        return insertPoint(starTreeM4);
    }

    // @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        return insertPoint(starTreeM10);
    }

     @Benchmark
    public long rStarTreeSearchOfGreekDataPointsMaxChildren004() {
        return searchGreek(starTreeM4);
    }

     @Benchmark
    public long rStarTreeSearchOfGreekDataPointsMaxChildren010() {
        return searchGreek(starTreeM10);
    }

    // @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren010WithBackpressure(Blackhole bh) {
        searchGreekWithBackpressure(starTreeM10, bh);
    }

    // @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        return insertPoint(defaultTreeM32);
    }

    // @Benchmark
    public long defaultRTreeSearchOfGreekDataPointsMaxChildren032() {
        return searchGreek(defaultTreeM32);
    }

    // @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        return insertPoint(starTreeM32);
    }

     @Benchmark
    public long rStarTreeSearchOfGreekDataPointsMaxChildren032() {
        return searchGreek(starTreeM32);
    }

    // @Benchmark
    public RTree<Object, Point> defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        return insertPoint(defaultTreeM128);
    }

    // @Benchmark
    public long defaultRTreeSearchOfGreekDataPointsMaxChildren128() {
        return searchGreek(defaultTreeM128);
    }

    // @Benchmark
    public RTree<Object, Point> rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        return insertPoint(starTreeM128);
    }

    @Benchmark
    public long rStarTreeSearchOfGreekDataPointsMaxChildren128() {
        return searchGreek(starTreeM128);
    }

    // @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        return insertRectangle(smallDefaultTreeM4);
    }

    // @Benchmark
    public long defaultRTreeSearchOf1000PointsMaxChildren004() {
        return search(smallDefaultTreeM4);
    }

    // @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        return insertRectangle(smallDefaultTreeM10);
    }

    // @Benchmark
    public long defaultRTreeSearchOf1000PointsMaxChildren010() {
        return search(smallDefaultTreeM10);
    }

    // @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        return insertRectangle(smallStarTreeM4);
    }

    // @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        return insertRectangle(smallStarTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren004() {
        search(smallStarTreeM4);
    }

    @Benchmark
    public long rStarTreeSearchOf1000PointsMaxChildren010() {
        return search(smallStarTreeM10);
    }

    // @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        return insertRectangle(smallDefaultTreeM32);
    }

    // @Benchmark
    public long defaultRTreeSearchOf1000PointsMaxChildren032() {
        return search(smallDefaultTreeM32);
    }

    // @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        return insertRectangle(smallStarTreeM32);
    }

    @Benchmark
    public long rStarTreeSearchOf1000PointsMaxChildren032() {
        return search(smallStarTreeM32);
    }

    // @Benchmark
    public RTree<Object, Rectangle> defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        return insertRectangle(smallDefaultTreeM128);
    }

    // @Benchmark
    public long defaultRTreeSearchOf1000PointsMaxChildren128() {
        return search(smallDefaultTreeM128);
    }

    // @Benchmark
    public RTree<Object, Rectangle> rStarTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        return insertRectangle(smallStarTreeM128);
    }

    @Benchmark
    public long rStarTreeSearchOf1000PointsMaxChildren128() {
        return search(smallStarTreeM128);
    }

    // @Benchmark
    public void rStarTreeDeleteOneEveryOccurrenceFromGreekDataChildren010() {
        deleteAll(starTreeM10);
    }

    @Benchmark
    public long searchNearestGreek() {
        return searchNearestGreek(starTreeM4);
    }

    private RTree<Object, Point> deleteAll(RTree<Object, Point> tree) {
        return tree.delete(entries.get(1000), true);
    }

    private long search(RTree<Object, Rectangle> tree) {
        return search(tree, DEFAULT_1000_RECTANGLE);
    }

    private long search(RTree<?, ?> tree, Rectangle r) {
        return Iterables.size(tree.search(r));
    }

    private long searchGreek(RTree<Object, Point> tree) {
        // should return 22 results
        return search(tree, DEFAULT_GREEK_RECTANGLE);
    }

    private static Rectangle searchRectangle() {
        final Rectangle r;
        if (precision == Precision.DOUBLE) {
            r = DEFAULT_GREEK_RECTANGLE;
        } else {
            r = Geometries.rectangle(40f, 27.0f, 40.5f, 27.5f);
        }
        return r;
    }

    private long searchNearestGreek(RTree<Object, Point> tree) {
        final Point p;
        if (precision == Precision.DOUBLE) {
            p = Geometries.point(40.0, 27.0);
        } else {
            p = Geometries.point(40.0f, 27.0f);
        }
        return Iterables.size(tree.nearest(p, 1, 300));
    }

    private long searchGreekWithBackpressure(RTree<Object, Point> tree, final Blackhole bh) {
        // should return 22 results
        return search(tree, searchRectangle());
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

    static boolean longRun = false;
    
    public static void main(String[] args) {
        RTree<Object, Point> tree = RTree.maxChildren(28).star().<Object, Point>create(entries);
        Rectangle r = searchRectangle();
        if (longRun) {
            while (true) {
                if (Iterables.size(tree.search(r)) == 0) {
                    System.out.println("unexpected");
                }
            }
        } else {
            
            long t = System.currentTimeMillis();
            long warmupTimeSeconds = 10;
            long benchmarkTimeSeconds = 10;
            long t2 = -1;
            long count = 0;
            while (true) {
                if (Iterables.size(tree.search(r))== 0) {
                    System.out.println("zero!!");
                };
                count++;
                if (count % 10000 == 0) {
                    if (t2 == -1) {
                        if (System.currentTimeMillis() - t > TimeUnit.SECONDS.toMillis(warmupTimeSeconds)) {
                            t2 = System.currentTimeMillis();
                        }
                    } else if (System.currentTimeMillis() - t2 > TimeUnit.SECONDS.toMillis(benchmarkTimeSeconds)) {
                        break;
                    }
                }
            }
            double ratePerSecond = count * 1000.0 / (System.currentTimeMillis() - t2);
            DecimalFormat df = new DecimalFormat("0.000");
            System.out.println("ratePerSecond=" + df.format(ratePerSecond / 1000000.0) + "m");
        }
    }

}
