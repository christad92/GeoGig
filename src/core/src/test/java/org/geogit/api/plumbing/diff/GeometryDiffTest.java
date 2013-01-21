package org.geogit.api.plumbing.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryDiffTest {

    @Test
    public void testModifiedMultiPolygon() throws Exception {
        int NUM_COORDS = 10;
        Random rand = new Random();
        List<Coordinate> list = Lists.newArrayList();
        for (int i = 0; i < NUM_COORDS; i++) {
            list.add(new Coordinate(rand.nextInt(), rand.nextInt()));
        }
        Geometry oldGeom = new WKTReader()
                .read("MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 45 10, 30 5, 10 30, 20 35),(30 20, 20 25, 20 15, 30 20)))");
        Geometry newGeom = new WKTReader()
                .read("MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 45 20, 30 5, 10 10, 10 30, 20 35)))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.of(oldGeom),
                Optional.of(newGeom));
        LCSGeometryDiffImpl deserializedDiff = new LCSGeometryDiffImpl(diff.asText());
        assertEquals(diff, deserializedDiff);
        assertEquals("4 point(s) deleted, 1 new point(s) added, 1 point(s) moved", diff.toString());
        Optional<Geometry> resultingGeom = diff.applyOn(Optional.of(oldGeom));
        assertEquals(newGeom, resultingGeom.get());
    }

    @Test
    public void testModifiedMultiLineString() throws Exception {
        Geometry oldGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 40),(20 35, 45 10, 30 5, 10 30, 20 35))");
        Geometry newGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 35, 45 30, 40 40),(20 35, 45 20, 30 15, 10 10, 10 30, 20 35),(10 10, 20 20, 35 30))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.of(oldGeom),
                Optional.of(newGeom));
        LCSGeometryDiffImpl deserializedDiff = new LCSGeometryDiffImpl(diff.asText());
        assertEquals(diff, deserializedDiff);
        assertEquals("0 point(s) deleted, 4 new point(s) added, 3 point(s) moved", diff.toString());
        Optional<Geometry> resultingGeom = diff.applyOn(Optional.of(oldGeom));
        assertEquals(newGeom, resultingGeom.get());
    }

    @Test
    public void testNoOldGeometry() throws Exception {
        Geometry oldGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 40),(20 35, 45 10, 30 5, 10 30, 20 35))");
        Geometry newGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 35, 45 30, 40 40),(20 35, 45 20, 30 15, 10 10, 10 30, 20 35),(10 10, 20 20, 35 30))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.fromNullable((Geometry) null),
                Optional.of(newGeom));
        LCSGeometryDiffImpl deserializedDiff = new LCSGeometryDiffImpl(diff.asText());
        assertEquals(diff, deserializedDiff);
        assertEquals("0 point(s) deleted, 13 new point(s) added, 0 point(s) moved", diff.toString());
        Optional<Geometry> resultingGeom = diff.applyOn(Optional.of(oldGeom));
        assertEquals(newGeom, resultingGeom.get());
    }

    @Test
    public void testNoNewGeometry() throws Exception {
        Geometry oldGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 40),(20 35, 45 10, 30 5, 10 30, 20 35))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.of(oldGeom),
                Optional.fromNullable((Geometry) null));
        LCSGeometryDiffImpl deserializedDiff = new LCSGeometryDiffImpl(diff.asText());
        assertEquals(diff, deserializedDiff);
        assertEquals("9 point(s) deleted, 0 new point(s) added, 0 point(s) moved", diff.toString());
        Optional<Geometry> resultingGeom = diff.applyOn(Optional.of(oldGeom));
        assertFalse(resultingGeom.isPresent());
    }

    @Test
    public void testDoubleReverseEquality() throws Exception {
        Geometry oldGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 40),(20 35, 45 10, 30 5, 10 30, 20 35))");
        Geometry newGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 35, 45 30, 40 40),(20 35, 45 20, 30 15, 10 10, 10 30, 20 35),(10 10, 20 20, 35 30))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.of(oldGeom),
                Optional.of(newGeom));
        LCSGeometryDiffImpl diff2 = diff.reversed().reversed();
        assertEquals(diff, diff2);
    }

    @Test
    public void testCanApply() throws Exception {
        Geometry oldGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 40),(20 35, 45 10, 30 5, 10 30, 20 35))");
        Geometry newGeom = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 35, 45 30, 40 40),(20 35, 45 20, 30 15, 10 10, 10 30, 20 35),(10 10, 20 20, 35 30))");
        LCSGeometryDiffImpl diff = new LCSGeometryDiffImpl(Optional.of(oldGeom),
                Optional.of(newGeom));
        Geometry oldGeomModified = new WKTReader()
                .read("MULTILINESTRING ((40 40, 20 45, 45 30, 40 41),(20 35, 45 10, 30 5, 10 30, 20 35))");
        assertTrue(diff.canBeAppliedOn(Optional.of(oldGeomModified)));
        Geometry oldGeomModified2 = new WKTReader().read("MULTILINESTRING ((40 40, 10 10))");
        assertFalse(diff.canBeAppliedOn(Optional.of(oldGeomModified2)));

    }

}
