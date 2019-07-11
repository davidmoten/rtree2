package com.github.davidmoten.rtree2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.davidmoten.kool.Stream;

import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Point;

public class GreekEarthquakes {

    public static Stream<Entry<Object, Point>> entries(final Precision precision) {
        return Stream
                .using(() -> new GZIPInputStream(
                        GreekEarthquakes.class.getResourceAsStream("/greek-earthquakes-1964-2000.txt.gz")),
                        in -> Stream.lines(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))))
                .flatMap(line -> {
                    if (line.trim().length() > 0) {
                        String[] items = line.split(" ");
                        double lat = Double.parseDouble(items[0]);
                        double lon = Double.parseDouble(items[1]);
                        Entry<Object, Point> entry;
                        if (precision == Precision.DOUBLE)
                            entry = Entries.entry(new Object(), Geometries.point(lat, lon));
                        else
                            entry = Entries.entry(new Object(), Geometries.point((float) lat, (float) lon));
                        return Stream.of(entry);
                    } else
                        return Stream.empty();
                });
    }

    static List<Entry<Object, Point>> entriesList(Precision precision) {
        List<Entry<Object, Point>> result = entries(precision).toList().get();
        System.out.println("loaded greek earthquakes into list");
        return result;
    }

}
