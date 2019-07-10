package com.github.davidmoten.rtree2;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Rectangle;
import com.github.davidmoten.rtree2.internal.Util;

public class UtilTest {

    @Test
    public void coverPrivateConstructor() {
        Asserts.assertIsUtilityClass(Util.class);
    }

    @Test
    public void testMbrWithNegativeValues() {
        Rectangle r = Geometries.rectangle(-2D, -2, -1, -1);
        Rectangle mbr = Util.mbr(Collections.singleton(r));
        assertEquals(r, mbr);
        System.out.println(r);
    }

}
