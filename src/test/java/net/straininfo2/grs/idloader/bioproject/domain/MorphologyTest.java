package net.straininfo2.grs.idloader.bioproject.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the enums in OrganismMorphology.
 */
public class MorphologyTest {

    @Test
    public void testShapeToString() {
        assertEquals("eBacilli", OrganismMorphology.Shape.BACILLI.toString());
        assertEquals("eCurvedShaped", OrganismMorphology.Shape.CURVED_SHAPED.toString());
    }

    @Test
    public void testStringToShape() {
        assertEquals(OrganismMorphology.Shape.FUSOBACTERIA, OrganismMorphology.Shape.fromString("eFusobacteria"));
    }
}
