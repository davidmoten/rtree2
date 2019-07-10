package com.github.davidmoten.rtree2;

import com.github.davidmoten.rtree2.geometry.Geometry;
import com.github.davidmoten.rtree2.geometry.HasGeometry;
import com.github.davidmoten.rtree2.geometry.Rectangle;

public class Mbr implements HasGeometry {

    private final Rectangle r;

    public Mbr(Rectangle r) {
        this.r = r;
    }

    @Override
    public Geometry geometry() {
        return r;
    }

}
