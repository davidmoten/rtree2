package com.github.davidmoten.rtree2;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree2.internal.Comparators;

public class ComparatorsTest {

    @Test
    public void testConstructorIsPrivate() {
        Asserts.assertIsUtilityClass(Comparators.class);
    }

}
