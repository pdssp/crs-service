
package com.geomatys.crsservice.client;

import com.geomatys.crsservice.AbstractIntegrationTest;
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
public class ClientTest extends AbstractIntegrationTest {

    @Test
    public void operationTest() throws Exception {

        CoordinateOperationFactory factory = new CRSServiceCoordinateOperationFactory(new URI(getServerUrl()+"/crs/operation"));

        //Mercator
        testTransform(factory, "EPSG:4326", "EPSG:3395", new double[]{10, 20});
        //EquidistantCylindrical
        testTransform(factory, "EPSG:4326", "EPSG:4087", new double[]{10, 20});
        //PolarStereographic
        testTransform(factory, "EPSG:4326", "EPSG:3031", new double[]{60, 20});
        //TransverseMercator
        testTransform(factory, "EPSG:4326", "EPSG:32231", new double[]{48, 2});
        //Geocentric
        testTransform(factory, "EPSG:4326", "EPSG:4978", new double[]{48, 2});

    }

    private void testTransform(CoordinateOperationFactory factory, String source, String target, double[] coords) throws FactoryException, TransformException {
        final CoordinateReferenceSystem crsSource = CRS.forCode(source);
        final CoordinateReferenceSystem crsTarget = CRS.forCode(target);
        final CoordinateOperation distop = factory.createOperation(crsSource, crsTarget);
        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(coords, 0, distRes, 0, 1);

        //compare with SIS
        final CoordinateOperation localOp = CRS.findOperation(crsSource, crsTarget, null);
        final MathTransform localTrs = localOp.getMathTransform();
        final double[] localRes = new double[localTrs.getTargetDimensions()];
        localTrs.transform(coords, 0, localRes, 0, 1);

        System.out.println(source + " " + Arrays.toString(coords) + " => " + target );
        System.out.println("SRC : " + Arrays.toString(coords));
        System.out.println("SIS : " + Arrays.toString(localRes));
        System.out.println("JS  : " + Arrays.toString(distRes));
        System.out.println("");

        {//test inverse
            coords = localRes;

            final CoordinateOperation rdistop = factory.createOperation(crsTarget, crsSource);
            final MathTransform rdistTrs = rdistop.getMathTransform();
            final double[] rdistRes = new double[rdistTrs.getTargetDimensions()];
            rdistTrs.transform(coords, 0, rdistRes, 0, 1);

            //compare with SIS
            final CoordinateOperation rlocalOp = CRS.findOperation(crsTarget, crsSource, null);
            final MathTransform rlocalTrs = rlocalOp.getMathTransform();
            final double[] rlocalRes = new double[rlocalTrs.getTargetDimensions()];
            rlocalTrs.transform(coords, 0, rlocalRes, 0, 1);

            System.out.println(target + " => " + source);
            System.out.println("SRC : " + Arrays.toString(coords));
            System.out.println("SIS : " + Arrays.toString(rlocalRes));
            System.out.println("JS  : " + Arrays.toString(rdistRes));
            System.out.println("");
        }

    }

}
