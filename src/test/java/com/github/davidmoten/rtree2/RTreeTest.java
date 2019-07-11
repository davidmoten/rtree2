package com.github.davidmoten.rtree2;

import static com.github.davidmoten.rtree2.Entries.entry;
import static com.github.davidmoten.rtree2.geometry.Geometries.circle;
import static com.github.davidmoten.rtree2.geometry.Geometries.line;
import static com.github.davidmoten.rtree2.geometry.Geometries.point;
import static com.github.davidmoten.rtree2.geometry.Geometries.rectangle;
import static com.github.davidmoten.rtree2.geometry.Intersects.pointIntersectsCircle;
import static com.github.davidmoten.rtree2.geometry.Intersects.rectangleIntersectsCircle;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Sets;
import com.github.davidmoten.rtree2.geometry.Circle;
import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.HasGeometry;
import com.github.davidmoten.rtree2.geometry.Intersects;
import com.github.davidmoten.rtree2.geometry.Point;
import com.github.davidmoten.rtree2.geometry.Rectangle;
import com.github.davidmoten.rtree2.internal.EntryDefault;

import rx.Observable;
import rx.functions.Func1;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RTreeTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testInstantiation() {
        RTree<Object, Geometry> tree = RTree.create();
        assertTrue(Observable.from(tree.entries()).isEmpty().toBlocking().single());
    }

    @Test
    public void testSearchEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        assertTrue(Observable.from(tree.search(r(1))).isEmpty().toBlocking().single());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testSearchEmptyTreeIteratorNextThrows() {
        RTree<Object, Geometry> tree = RTree.create();
        tree.search(r(1)).iterator().next();
    }

    @Test
    public void testSearchOnOneItem() {
        RTree<Object, Rectangle> tree = RTree.create();
        Entry<Object, Rectangle> entry = e(1);
        tree = tree.add(entry);
        assertEquals(Arrays.asList(entry), Observable.from(tree.search(r(1))).toList().toBlocking().single());
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTree<Object, Geometry> tree = RTree.create().add(e(1));
        assertFalse(tree.isEmpty());
    }

    // @Test(expected = IOException.class)
    public void testSaveFileException() throws IOException {
        FileLock lock = null;
        RandomAccessFile file = null;
        try {
            String filename = "target/locked.png";
            File f = new File(filename);
            f.createNewFile();
            file = new RandomAccessFile(f, "rw");
            lock = file.getChannel().lock();
            RTree.create().visualize(600, 600).save(filename, "PNG");
        } finally {
            try {
                lock.release();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testVisualizerWithEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        tree.visualize(600, 600).save("target/tree.png", "PNG");
    }

    @Test
    public void testBulkLoadingEmpty() {
        RTree<Object, Point> tree = RTree.create(new ArrayList<Entry<Object, Point>>());
        assertTrue(Observable.from(tree.entries()).isEmpty().toBlocking().single());
    }

    @Test
    public void testBulkLoadingWithOneItemIsNotEmpty() {
        RTree<Object, Rectangle> tree = RTree.create(Arrays.asList(e(1)));
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testBulkLoadingEntryCount() {
        List<Entry<Integer, Geometry>> entries = new ArrayList<Entry<Integer, Geometry>>(10000);
        for (int i = 1; i <= 10000; i++) {
            Point point = nextPoint();
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            entries.add(new EntryDefault<Integer, Geometry>(i, point));
        }
        RTree<Integer, Geometry> tree = RTree.create(entries);
        int entrySize = Observable.from(tree.entries()).count().toBlocking().single();
        System.out.println("entry count: " + entrySize);
        assertEquals(entrySize, entries.size());
    }

    @Test
    public void testSearchOnOneItemOnBulkLoadingRTree() {
        Entry<Object, Rectangle> entry = e(1);
        RTree<Object, Rectangle> tree = RTree.create(Arrays.asList(entry));
        assertEquals(Arrays.asList(entry), Observable.from(tree.search(r(1))).toList().toBlocking().single());
    }

    @Test
    public void testPerformanceAndEntriesCount() {

        long repeats = Long.parseLong(System.getProperty("r", "1"));
        long n = Long.parseLong(System.getProperty("n", "10000"));
        RTree<Object, Geometry> tree = null;
        while (--repeats >= 0) {
            long t = System.currentTimeMillis();
            tree = createRandomRTree(n);
            long diff = System.currentTimeMillis() - t;
            System.out.println("inserts/second = " + ((double) n / diff * 1000));
        }
        assertEquals(n, Iterables.size(tree.entries()));

        long t = System.currentTimeMillis();
        Entry<Object, Geometry> entry = tree.search(rectangle(0, 0, 500, 500)).iterator().next();
        long diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " entries=" + diff);

    }

    @Test
    public void testSearchOfPoint() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = Observable.from(tree.search(point(1, 1))).toList().toBlocking().single();
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    @Test
    public void testSearchOfPointWithinDistance() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = Observable.from(tree.search(point(1, 1), 2)).toList().toBlocking()
                .single();
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    static List<Entry<Object, Geometry>> createRandomEntries(long n) {
        List<Entry<Object, Geometry>> list = new ArrayList<Entry<Object, Geometry>>();
        for (long i = 0; i < n; i++)
            list.add(randomEntry());
        return list;
    }

    static RTree<Object, Geometry> createRandomRTree(long n) {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create();
        for (long i = 0; i < n; i++) {
            Entry<Object, Geometry> entry = randomEntry();
            tree = tree.add(entry);
        }
        return tree;
    }

    static Entry<Object, Geometry> randomEntry() {
        return entry(new Object(), (Geometry) random(Precision.SINGLE));
    }

    @Test
    public void testDeleteWithGeometry() {
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).create();
        Entry<Object, Rectangle> entry = e(1);
        Entry<Object, Rectangle> entry2 = e2(1);
        tree = tree.add(entry).add(entry2);

        tree = tree.delete(entry.value(), entry.geometry(), true);
        List<Entry<Object, Rectangle>> entries = Observable.from(tree.entries()).toList().toBlocking().single();
        assertTrue(entries.contains(entry2) && !entries.contains(entry));
    }

    @Test
    public void testDeleteIssue81() {
        RTree<Object, Point> t = RTree.create();
        t = t.add(1, Geometries.pointGeographic(123, 23));
        t = t.delete(1, Geometries.pointGeographic(123, 23));
        assertEquals(0, t.size());
    }

    @Test
    public void testDepthWith0() {
        RTree<Object, Geometry> tree = RTree.create();
        tree = tree.add(createRandomEntries(5));
        List<Entry<Object, Geometry>> entries = Iterables.toList(tree.entries());
        RTree<Object, Geometry> deletedTree = tree.delete(entries, true);
        assertTrue(deletedTree.isEmpty());
    }

    @Test
    public void testContext() {
        RTree<Object, Geometry> tree = RTree.create();
        assertNotNull(tree.context());
    }

    @Test
    public void testIterableDeletion() {
        RTree<Object, Rectangle> tree = RTree.create();
        Entry<Object, Rectangle> entry1 = e(1);
        Entry<Object, Rectangle> entry2 = e(2);
        Entry<Object, Rectangle> entry3 = e(3);
        tree = tree.add(entry1).add(entry2).add(entry3);

        List<Entry<Object, Rectangle>> list = new ArrayList<Entry<Object, Rectangle>>();
        list.add(entry1);
        list.add(entry3);
        tree = tree.delete(list);
        List<Entry<Object, Rectangle>> entries = Iterables.toList(tree.entries());
        assertTrue(entries.contains(entry2));
        assertFalse(entries.contains(entry1));
        assertFalse(entries.contains(entry3));
    }

    @Test
    public void testFullDeletion() {
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).create();
        Entry<Object, Rectangle> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, true);
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testPartialDeletion() {
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).create();
        Entry<Object, Rectangle> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, false);
        List<Entry<Object, Rectangle>> entries = Iterables.toList(tree.entries());
        long countEntries = Iterables.size(tree.entries());
        assertTrue(countEntries == 1);
        assertTrue(entries.get(0).equals(entry));
    }

    @Test
    public void testDepthWithMaxChildren3Entries1() {
        RTree<Object, Rectangle> tree = create(3, 1);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries2() {
        RTree<Object, Rectangle> tree = create(3, 2);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries3() {
        RTree<Object, Rectangle> tree = create(3, 3);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries4() {
        RTree<Object, Rectangle> tree = create(3, 4);
        assertEquals(2, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries8() {
        RTree<Object, Rectangle> tree = create(3, 8);
        tree.visualize(800, 800).save(new File("target/treeLittle.png"), "PNG");
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries10() {
        RTree<Object, Rectangle> tree = create(3, 10);
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testSizeIsZeroIfTreeEmpty() {
        assertEquals(0, create(3, 0).size());
    }

    @Test
    public void testSizeIsOneIfTreeHasOneEntry() {
        assertEquals(1, create(3, 1).size());
    }

    @Test
    public void testSizeIsFiveIfTreeHasFiveEntries() {
        assertEquals(5, create(3, 5).size());
    }

    @Test
    public void testSizeAfterDelete() {
        Entry<Object, Rectangle> entry = e(1);
        RTree<Object, Rectangle> tree = create(3, 0).add(entry).add(entry).add(entry).delete(entry);
        assertEquals(2, tree.size());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        RTree<Object, Rectangle> tree = create(3, 8);
        tree = tree.add(e(10));
        // node children are now 1,2 and 3,4
        assertEquals(3, tree.calculateDepth());
        tree = tree.delete(e(10));
        // node children are now 1,2 and 3
        assertEquals(3, tree.calculateDepth());
        assertEquals(Sets.newHashSet(e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8)), Sets.newHashSet(tree.entries()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteOfEntryThatDoesNotExistFromTreeOfOneEntry() {
        RTree<Object, Geometry> tree = RTree.create().add(e(1));
        tree = tree.delete(e(2));
        assertEquals(Lists.newArrayList(e(1)), Lists.newArrayList(tree.entries()));
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        tree = tree.delete(e(2));
        assertEquals(0, Iterables.size(tree.entries()));
    }

    @Test
    public void testBuilder1() {
        RTree<Object, Point> tree = RTree.minChildren(1).maxChildren(4).selector(new SelectorMinimalAreaIncrease())
                .splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTree<Object, Rectangle> tree = create(3, 100).delete(e(1000));
        assertEquals(100, Iterables.size(tree.entries()));
    }

    @Test
    public void testBuilder2() {
        RTree<Object, Point> tree = RTree.selector(new SelectorMinimalAreaIncrease()).minChildren(1).maxChildren(4)
                .splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTree<Object, Point> tree = RTree.maxChildren(4).selector(new SelectorMinimalAreaIncrease()).minChildren(1)
                .splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTree<Object, Point> tree = RTree.splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).minChildren(1).create();
        testBuiltTree(tree);
    }

    private void testBuiltTree(RTree<Object, Point> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(i, Geometries.point(i, i));
        }
        assertEquals(1000, Iterables.size(tree.entries()));
    }

    private static RTree<Object, Rectangle> create(int maxChildren, int n) {
        RTree<Object, Rectangle> tree = RTree.maxChildren(maxChildren).create();
        for (int i = 1; i <= n; i++)
            tree = tree.add(e(i));
        return tree;
    }

    @Test
    public void testNearestSameDirection() {
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).<Object, Rectangle>create().add(e(1)).add(e(2)).add(e(3))
                .add(e(10)).add(e(11));
        List<Entry<Object, Rectangle>> list = Iterables.toList(tree.nearest(r(9), 10, 2));
        assertEquals(2, list.size());
        System.out.println(list);
        assertEquals(10, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(11, list.get(1).geometry().mbr().x1(), PRECISION);

        List<Entry<Object, Rectangle>> list2 = Iterables.toList(tree.nearest(r(10), 8, 3));
        assertEquals(2, list2.size());
        assertEquals(10, list2.get(1).geometry().mbr().x1(), PRECISION);
        assertEquals(11, list2.get(0).geometry().mbr().x1(), PRECISION);
    }

    @Test
    public void testNearestDifferentDirections() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(3)).add(e(9)).add(e(10));
        List<Entry<Object, Geometry>> list = Iterables.toList(tree.nearest(r(6), 10, 2));
        assertEquals(2, list.size());
        assertEquals(3, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(9, list.get(1).geometry().mbr().x1(), PRECISION);
    }

    @Test
    public void testNearestToAPoint() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = Iterables.toList(tree.nearest(point(2, 2), 3, 2));
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    @Test
    public void testNearestReturnsInOrder() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1)).add(value, point(2, 2))
                .add(value, point(3, 3)).add(value, point(4, 4));
        List<Entry<Object, Geometry>> list = Iterables.toList(tree.nearest(point(0, 0), 10, 10));
        System.out.println(list);
        assertEquals(4, list.size());
        assertEquals(point(1, 1), list.get(0).geometry());
        assertEquals(point(2, 2), list.get(1).geometry());
        assertEquals(point(3, 3), list.get(2).geometry());
        assertEquals(point(4, 4), list.get(3).geometry());
    }

    @Test
    public void testVisualizer() {
        List<Entry<Object, Geometry>> entries = createRandomEntries(1000);
        int maxChildren = 8;
        RTree<Object, Geometry> tree = RTree.maxChildren(maxChildren).create().add(entries);
        tree.visualize(600, 600).save("target/tree.png");

        RTree<Object, Geometry> tree2 = RTree.star().maxChildren(maxChildren).create().add(entries);
        tree2.visualize(600, 600).save("target/tree2.png");

        RTree<Object, Geometry> tree3 = RTree.maxChildren(maxChildren).create(entries);
        tree3.visualize(600, 600).save("target/tree3.png");
    }

    @Test(expected = RuntimeException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList() {
        SplitterRStar spl = new SplitterRStar();
        spl.split(Collections.<HasGeometry>emptyList(), 4);
    }

    @Test
    public void testSearchOnGreekDataUsingFlatBuffersFactory() {

    }

    @Test
    public void testVisualizerWithGreekData() {
        List<Entry<Object, Point>> entries = GreekEarthquakes.entriesList(Precision.DOUBLE);
        int maxChildren = 8;
        RTree<Object, Point> tree = RTree.maxChildren(maxChildren).<Object, Point>create().add(entries);
        tree.visualize(2000, 2000).save("target/greek.png");

        // do search
        long found = Iterables.size(tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)));
        System.out.println("found=" + found);
        assertEquals(22, found);

        RTree<Object, Point> tree2 = RTree.maxChildren(maxChildren).star().<Object, Point>create().add(entries);
        tree2.visualize(2000, 2000).save("target/greek2.png");

        RTree<Object, Point> tree3 = RTree.maxChildren(maxChildren).create(entries);
        tree3.visualize(2000, 2000).save("target/greek3.png");
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Object, Rectangle> e1 = e(1);
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).<Object, Rectangle>create().add(e1).delete(e1);
        assertTrue(Iterables.isEmpty(tree.entries()));
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Object, Rectangle> e1 = e(1);
        RTree<Object, Rectangle> tree = RTree.maxChildren(4).<Object, Rectangle>create().add(e1).add(e(2)).add(e(3))
                .add(e(4)).add(e(5)).add(e(6)).add(e(7)).add(e(8)).add(e(9)).add(e(10)).delete(e1);
        assertEquals(9, Iterables.size(tree.entries()));
        assertFalse(Observable.from(tree.entries()).contains(e1).toBlocking().single());
    }

    @Test
    public void testDeleteOneFromLargeTreeThenDeleteAllAndEnsureEmpty() {
        int n = 10000;
        RTree<Object, Geometry> tree = createRandomRTree(n).add(e(1)).add(e(2)).delete(e(1));
        assertEquals(n + 1, Iterables.size(tree.entries()));
        assertFalse(Observable.from(tree.entries()).contains(e(1)).toBlocking().single());
        assertTrue(Observable.from(tree.entries()).contains(e(2)).toBlocking().single());
        n++;
        assertEquals(n, tree.size());

        for (Entry<Object, Geometry> entry : tree.entries()) {
            tree = tree.delete(entry);
            n--;
            assertEquals(n, tree.size());
        }
        assertTrue(Iterables.isEmpty(tree.entries()));
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testDeleteOnlyDeleteOneIfThereAreMoreThanMaxChildren() {
        Entry<Object, Rectangle> e1 = e(1);
        long count = Iterables.size(RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1).delete(e1)
                .search(e1.geometry().mbr()));
        assertEquals(4, count);
    }

    @Test
    public void testDeleteAllIfThereAreMoreThanMaxChildren() {
        Entry<Object, Rectangle> e1 = e(1);
        long count = Iterables.size(RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1, true).search(e1.geometry().mbr()));
        assertEquals(0, count);
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Object, Rectangle> e1 = e(1);
        Entry<Object, Rectangle> e2 = e(2);
        RTree<Object, Rectangle> tree = RTree.<Object, Rectangle>create().add(e1);
        assertTrue(tree == tree.delete(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTree<String, Geometry> tree = RTree.maxChildren(5).create();
        tree = tree.add(entry("DAVE", point(10, 20))).add(entry("FRED", point(12, 25)))
                .add(entry("MARY", point(97, 125)));
    }

    @Test
    public void testSearchConditionAlwaysFalse() {
        @SuppressWarnings("unchecked")
        RTree<Object, Geometry> tree = (RTree<Object, Geometry>) (RTree<?, ?>) create(3, 3);
        assertTrue(Iterables.isEmpty(tree.search(x -> false)));
    }

    @Test
    public void testAddOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object, Geometry> tree = (RTree<Object, Geometry>) (RTree<?, ?>) create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2));
        assertEquals(1, Iterables.size(tree.entries()));
    }

    @Test
    public void testDeleteOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object, Geometry> tree = (RTree<Object, Geometry>) (RTree<?, ?>) create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2)).delete(123, Geometries.point(1, 2));
        assertTrue(Iterables.isEmpty(tree.entries()));
    }

    @Test
    public void testStandardRTreeSearch() {
        Rectangle r = rectangle(13.0, 23.0, 50.0, 80.0);
        Point[] points = { point(59.0, 91.0), point(86.0, 14.0), point(36.0, 60.0), point(57.0, 36.0),
                point(14.0, 37.0) };

        RTree<Integer, Geometry> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = new HashSet<Integer>(
                Observable.from(tree.search(r)).map(RTreeTest.<Integer>toValue()).toList().toBlocking().single());
        assertEquals(new HashSet<Integer>(asList(3, 5)), set);
    }

    @Test
    public void testStandardRTreeSearch2() {
        Rectangle r = rectangle(10.0, 10.0, 50.0, 50.0);
        Point[] points = { point(28.0, 19.0), point(29.0, 4.0), point(10.0, 63.0), point(34.0, 85.0),
                point(62.0, 45.0) };

        RTree<Integer, Geometry> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = new HashSet<Integer>(
                Observable.from(tree.search(r)).map(RTreeTest.<Integer>toValue()).toList().toBlocking().single());
        assertEquals(new HashSet<Integer>(asList(1)), set);
    }

    @Test
    public void testBulkLoadingTreeAndStarTreeReturnsSameAsStandardRTree() {

        RTree<Integer, Geometry> tree1 = RTree.create();
        RTree<Integer, Geometry> tree2 = RTree.star().create();

        Rectangle[] testRects = { rectangle(0, 0, 0, 0), rectangle(0, 0, 100, 100), rectangle(0, 0, 10, 10),
                rectangle(0.12, 0.25, 50.356, 50.756), rectangle(1, 0.252, 50, 69.23),
                rectangle(13.12, 23.123, 50.45, 80.9), rectangle(10, 10, 50, 50) };

        List<Entry<Integer, Geometry>> entries = new ArrayList<Entry<Integer, Geometry>>(10000);
        for (int i = 1; i <= 10000; i++) {
            Point point = nextPoint();
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            tree1 = tree1.add(i, point);
            tree2 = tree2.add(i, point);
            entries.add(new EntryDefault<Integer, Geometry>(i, point));
        }
        RTree<Integer, Geometry> tree3 = RTree.create(entries);

        // tree2.visualize(2000, 2000).save("target/tree22.png");
        // tree3.visualize(2000, 2000).save("target/tree33.png");

        for (Rectangle r : testRects) {
            Set<Integer> res1 = new HashSet<Integer>(
                    Observable.from(tree1.search(r)).map(RTreeTest.<Integer>toValue()).toList().toBlocking().single());
            Set<Integer> res2 = new HashSet<Integer>(
                    Observable.from(tree2.search(r)).map(RTreeTest.<Integer>toValue()).toList().toBlocking().single());
            Set<Integer> res3 = new HashSet<Integer>(
                    Observable.from(tree3.search(r)).map(RTreeTest.<Integer>toValue()).toList().toBlocking().single());
            System.out.println("searchRect= rectangle(" + r.x1() + "," + r.y1() + "," + r.x2() + "," + r.y2() + ")");
            System.out.println("res1.size=" + res1.size() + ",res2.size=" + res2.size() + ",res3.size=" + res3.size());
            // System.out.println("res1=" + res1 + ",res2=" + res2 + ",res3=" + res3);
            assertEquals(res1.size(), res2.size());
            assertEquals(res1.size(), res3.size());
        }
    }

    @Test
    public void testSearchWithIntersectsRectangleFunction() {
        RTree<Integer, Rectangle> tree = RTree.create();
        tree.search(circle(0, 0, 1), rectangleIntersectsCircle);
    }

    @Test
    public void testSearchWithIntersectsPointFunctionReturnsOne() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(0, 0));
        Observable<Entry<Integer, Point>> entries = Observable
                .from(tree.search(circle(0, 0, 1), pointIntersectsCircle));
        assertEquals(1, (int) entries.count().toBlocking().single());
    }

    @Test
    public void testSearchWithIntersectsPointFunctionReturnsNone() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(10, 10));
        Observable<Entry<Integer, Point>> entries = Observable
                .from(tree.search(circle(0, 0, 1), pointIntersectsCircle));
        assertEquals(0, (int) entries.count().toBlocking().single());
    }

    @Test
    public void testSearchWithDistanceFunctionIntersectsMbrButNotActualGeometry() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(0, 0)).add(2, point(1, 1));

        Observable<Entry<Integer, Point>> entries = Observable
                .from(tree.search(circle(0, 0, 1), 0.1, distanceCircleToPoint));
        assertEquals(1, (int) entries.count().toBlocking().single());
    }

    @Test
    public void testSearchWithDistanceFunctionIntersectsMbrAndActualGeometry() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(0, 0)).add(2,
                point(1, 1));

        Observable<Entry<Integer, Point>> entries = Observable.from(tree.search(circle(0, 0, 1), 0.5,
                distanceCircleToPoint));
        assertEquals(2, (int) entries.count().toBlocking().single());
    }

    @Test
    public void testSearchWithDistanceFunctionIntersectsNothing() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(0, 0)).add(2, point(1, 1));

        Observable<Entry<Integer, Point>> entries = Observable
                .from(tree.search(circle(10, 10, 1), 0.5, distanceCircleToPoint));
        assertEquals(0, (int) entries.count().toBlocking().single());
    }

    @Test
    public void calculateDepthOfEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        assertEquals(0, tree.calculateDepth());
    }

    @Test
    public void calculateAsStringOfEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        assertEquals("", tree.asString());
    }

    @Test
    public void testForMeiZhao() {
        for (int minChildren = 1; minChildren <= 2; minChildren++) {
            RTree<Integer, Point> tree = RTree.maxChildren(3).minChildren(minChildren).<Integer, Point>create()
                    .add(1, point(1, 9)).add(2, point(2, 10)).add(3, point(4, 8)).add(4, point(6, 7))
                    .add(5, point(9, 10)).add(6, point(7, 5)).add(7, point(5, 6)).add(8, point(4, 3))
                    .add(9, point(3, 2)).add(10, point(9, 1)).add(11, point(10, 4)).add(12, point(6, 2))
                    .add(13, point(8, 3));
            System.out.println(tree.asString());
        }
    }

    @Test
    public void testSearchWithCircleFindsCentreOnly() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).add(3,
                point(3, 3));
        List<Entry<Integer, Point>> list = Iterables.toList(tree.search(Geometries.circle(2, 2, 1)));
        assertEquals(1, list.size());
        assertEquals(2, (int) list.get(0).value());
    }

    @Test
    public void testSearchWithCircleFindsAll() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).add(3,
                point(3, 3));
        List<Entry<Integer, Point>> list = Iterables.toList(tree.search(Geometries.circle(2, 2, 1.5)));
        assertEquals(3, list.size());
    }

    @Test
    public void testSearchWithLineFindsAll() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).add(3,
                point(3, 3));
        List<Entry<Integer, Point>> list =Iterables.toList(tree.search(Geometries.line(0, 0, 4, 4)));
        assertEquals(3, list.size());
    }

    @Test
    public void testSearchWithLineFindsOne() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).add(3,
                point(3, 3));
        List<Entry<Integer, Point>> list = Iterables.toList(tree.search(Geometries.line(1.5, 1.5, 2.5, 2.5)));
        assertEquals(1, list.size());
        assertEquals(2, (int) list.get(0).value());
    }

    @Test
    public void testSearchWithLineFindsNone() {
        RTree<Integer, Point> tree = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).add(3,
                point(3, 3));
        List<Entry<Integer, Point>> list =Iterables.toList( tree.search(Geometries.line(1.5, 1.5, 2.6, 2.5)));
        System.out.println(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testRTreeRootMbrWhenRTreeEmpty() {
        assertFalse(RTree.create().mbr().isPresent());
    }

    @Test
    public void testRTreeRootMbrWhenRTreeNonEmpty() {
        Optional<Rectangle> r = RTree.<Integer, Point>create().add(1, point(1, 1)).add(2, point(2, 2)).mbr();
        assertEquals(Geometries.rectangle(1, 1, 2, 2), r.get());
    }

    @Test
    public void testIntersectsPointLine() {
        assertTrue(Intersects.lineIntersectsPoint.test(line(1, 1, 2, 2), point(1, 1)));
    }

    @Test
    public void testDeleteIssue81d() {
        RTree<Object, Point> t = RTree.create();
        t = t.add(1, Geometries.pointGeographic(123.4d, 23.3d));
        t = t.delete(1, Geometries.pointGeographic(123.4d, 23.3d));
        assertEquals(0, t.size());
    }

    @Test
    public void testDeleteIssue81f() {
        RTree<Object, Point> t = RTree.create();
        t = t.add(1, Geometries.pointGeographic(123.4f, 23.3f));
        t = t.delete(1, Geometries.pointGeographic(123.4f, 23.3f));
        assertEquals(0, t.size());
    }

    private static BiFunction<Point, Circle, Double> distanceCircleToPoint = new BiFunction<Point, Circle, Double>() {
        @Override
        public Double apply(Point point, Circle circle) {
            return circle.distance(point.mbr());
        }
    };

    private static <T> Func1<Entry<T, ?>, T> toValue() {
        return new Func1<Entry<T, ?>, T>() {

            @Override
            public T call(Entry<T, ?> entry) {
                return entry.value();
            }
        };
    }

    private static Point nextPoint() {

        double randomX = Math.round(Math.random() * 100);

        double randomY = Math.round(Math.random() * 100);

        return Geometries.point(randomX, randomY);

    }

    static Entry<Object, Rectangle> e(int n) {
        return Entries.<Object, Rectangle>entry(n, r(n));
    }

    static Entry<Object, Rectangle> e2(int n) {
        return Entries.<Object, Rectangle>entry(n, r(n - 1));
    }

    private static Rectangle r(int n) {
        return rectangle(n, n, n + 1, n + 1);
    }

    private static Rectangle r(double n, double m) {
        return rectangle(n, m, n + 1, m + 1);
    }

    private static Rectangle r(float n, float m) {
        return rectangle(n, m, n + 1, m + 1);
    }

    static Rectangle random(Precision precision) {
        if (precision == Precision.SINGLE)
            return r((float) Math.random() * 1000, (float) Math.random() * 1000);
        else
            return r(Math.random() * 1000, Math.random() * 1000);
    }

    @Test
    public void testSearchGreekEarthquakesDouble() {
        Iterable<Entry<Object, Point>> entriesDouble = GreekEarthquakes.entries(Precision.DOUBLE);
        RTree<Object, Point> t = RTree.maxChildren(4).<Object, Point>create().add(entriesDouble); //
        Observable.from(t.search(Geometries.rectangle(40, 27.0, 40.5, 27.5))) //
                .test() //
                .assertValueCount(22) //
                .assertCompleted();
    }

}
