
package com.geomatys.crsservice.client;

import java.net.URI;
import java.util.Arrays;
import org.apache.sis.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author jsorel
 */
public class ClientTest {

    @Test
    public void operationTest() throws Exception {
        
        CoordinateOperationFactory factory = new CRSServiceCoordinateOperationFactory(new URI("http://localhost:8080/crs/operation"));

        testTransform(factory, "EPSG:4326", "EPSG:3395", new double[]{10, 20});

    }

    private void testTransform(CoordinateOperationFactory factory, String source, String target, double[] coords) throws FactoryException, TransformException {
        final CoordinateReferenceSystem crsSource = CRS.forCode(source);
        final CoordinateReferenceSystem crsTarget = CRS.forCode(target);
        final CoordinateOperation distop = factory.createOperation(crsSource, crsTarget);
        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = coords.clone();
        distTrs.transform(distRes, 0, distRes, 0, 1);

        //compare with SIS
        final CoordinateOperation localOp = factory.createOperation(crsSource, crsTarget);
        final MathTransform localTrs = localOp.getMathTransform();
        final double[] localRes = coords.clone();
        localTrs.transform(localRes, 0, localRes, 0, 1);

        System.out.println(source +" => " + target);
        System.out.println(Arrays.toString(localRes) + "\n" + Arrays.toString(distRes));

    }

}
