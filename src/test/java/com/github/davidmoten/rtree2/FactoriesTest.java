package com.github.davidmoten.rtree2;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree2.Factories;

public class FactoriesTest {

    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Factories.class);
    }

}
