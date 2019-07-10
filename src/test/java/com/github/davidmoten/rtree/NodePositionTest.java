package com.github.davidmoten.rtree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.github.davidmoten.rtree.geometry.Rectangle;

public class NodePositionTest {

    @Test
    public void testToString() {
        @SuppressWarnings("unchecked")
        Node<Object, Rectangle> node = Mockito.mock(Node.class);
        assertTrue(new NodePositionMutable<Object, Rectangle>(node, 1).toString()
                .startsWith("NodePosition ["));
    }

}
