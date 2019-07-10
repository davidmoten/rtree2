package com.github.davidmoten.rtree2.internal;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree2.internal.LeafHelper;

public class LeafHelperTest {

    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(LeafHelper.class);
    }

}
