package com.github.davidmoten.rtree2;

import java.util.Arrays;
import java.util.List;

import org.davidmoten.kool.Stream;

import com.github.davidmoten.rtree2.geometry.Point;

public class GalleryMain {

    public static void main(String[] args) {
        Stream<Entry<Object, Point>> entries = GreekEarthquakes.entries(Precision.DOUBLE)
                .cache();

        List<Integer> sizes = Arrays.asList(100, 1000, 10000, 1000000);
        List<Integer> maxChildrenValues = Arrays.asList(4, 8, 16, 32, 64, 128);
        for (int size : sizes)
            for (int maxChildren : maxChildrenValues) {
                if (size > maxChildren) {
                    System.out.println("saving " + size + " m=" + maxChildren);
                    RTree<Object, Point> tree = RTree.maxChildren(maxChildren)
                            .<Object, Point>create().add(entries.take(size));
                    tree.visualize(600, 600)
                            .save("target/greek-" + size + "-" + maxChildren + "-quad.png");
                    RTree<Object, Point> tree2 = RTree.star().maxChildren(maxChildren)
                            .<Object, Point>create().add(entries.take(size));
                    tree2.visualize(600, 600)
                            .save("target/greek-" + size + "-" + maxChildren + "-star.png");
                }
            }
    }
}
