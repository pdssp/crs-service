
package com.geomatys.crsservice.client;

import java.net.URI;
import java.util.Arrays;
import org.apache.sis.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author jsorel
 */
public class ClientTest {

    @Test
    public void operationTest() throws Exception {

        CoordinateOperationFactory factory = new CRSServiceCoordinateOperationFactory(new URI("http://localhost:8080/crs/operation"));

        CoordinateOperation op = factory.createOperation(CRS.forCode("EPSG:4326"), CRS.forCode("EPSG:3395"));

        final double[] coords = new double[]{10,20};
        MathTransform trs = op.getMathTransform();
        trs.transform(coords, 0, coords, 0, 1);

        System.out.println(Arrays.toString(coords));
    }

}
