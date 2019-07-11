package com.github.davidmoten.rtree2;

import java.util.Collections;

import org.junit.Test;

import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.internal.NonLeafDefault;

public class NonLeafTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNonLeafPrecondition() {
        new NonLeafDefault<Object,Geometry>(Collections.<Node<Object,Geometry>>emptyList(), null);
    }
    
}
