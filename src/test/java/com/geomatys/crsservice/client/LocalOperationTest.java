package com.geomatys.crsservice.client;

import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test CRS conversion business layer outside Spring environment.
 * Validates usage of {@link LocalCoordinateOperationFactory} test fixture.
 */
public class LocalOperationTest {

    private final ScriptingCoordinateOperationFactory factory = new LocalCoordinateOperationFactory();

    @Test
    public void testAxisInversion() throws FactoryException, TransformException {
        ClientTest.testTransform(factory, "EPSG:4326", "CRS:84", new double[]{10, 20}, true, 1e-12, 1e-12);
    }
}
