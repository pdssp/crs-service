
package com.geomatys.crsservice.client;

import com.geomatys.crsservice.AbstractIntegrationTest;
import java.net.URI;
import java.util.Arrays;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
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

        //Linear transform test
        testTransform(factory, "EPSG:4326", "CRS:84", new double[]{10, 20}, true);
        //Mercator
        testTransform(factory, "EPSG:4326", "EPSG:3395", new double[]{10, 20}, true);
        //EquidistantCylindrical
        testTransform(factory, "EPSG:4326", "EPSG:4087", new double[]{10, 20}, true);
        //PolarStereographic
        testTransform(factory, "EPSG:4326", "EPSG:3031", new double[]{60, 20}, true);
        //TransverseMercator
        testTransform(factory, "EPSG:4326", "EPSG:32231", new double[]{48, 2}, true);
        //Lambert Conic
        testTransform(factory, "EPSG:4326", "EPSG:2154", new double[]{48, 2}, true);
        //Geocentric
        testTransform(factory, "EPSG:4326", "EPSG:4978", new double[]{48, 2}, true);

        //test PassThroughTransform : Not working yet
        CoordinateReferenceSystem crs1 = CRS.compound(CRS.forCode("CRS:84"));
        CoordinateReferenceSystem crs2 = CRS.compound(CRS.forCode("CRS:84"), CRS.forCode("EPSG:5714"));
        CoordinateReferenceSystem crs3 = CRS.compound(CRS.forCode("EPSG:3395"), CRS.forCode("EPSG:5714"));

        testTransform(factory, crs2.toWKT(), crs1.toWKT(), new double[]{40,50,100}, false);
        testTransform(factory, crs2.toWKT(), crs3.toWKT(), new double[]{40,50,100}, true);
    }

    private void testTransform(CoordinateOperationFactory factory, String source, String target, double[] coords, boolean testInverse) throws FactoryException, TransformException {
        final CoordinateReferenceSystem crsSource = parseCRS(source, false);
        final CoordinateReferenceSystem crsTarget = parseCRS(target, false);
        final CoordinateOperation distop = factory.createOperation(crsSource, crsTarget);
        final MathTransform distTrs = distop.getMathTransform();
        final double[] distRes = new double[distTrs.getTargetDimensions()];
        distTrs.transform(coords, 0, distRes, 0, 1);

        //compare with SIS
        final CoordinateOperation localOp = CRS.findOperation(crsSource, crsTarget, null);
        final MathTransform localTrs = localOp.getMathTransform();
        final double[] localRes = new double[localTrs.getTargetDimensions()];
        localTrs.transform(coords, 0, localRes, 0, 1);

        System.out.println("FROM : " + source + "\n  TO : " + target);
        System.out.println("SRC : " + Arrays.toString(coords));
        System.out.println("SIS : " + Arrays.toString(localRes));
        System.out.println("JS  : " + Arrays.toString(distRes));
        System.out.println("");

        if (testInverse) {//test inverse
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

            System.out.println("FROM : " + target + "\n  TO : " + source);
            System.out.println("SRC : " + Arrays.toString(coords));
            System.out.println("SIS : " + Arrays.toString(rlocalRes));
            System.out.println("JS  : " + Arrays.toString(rdistRes));
            System.out.println("");
        }

    }


    private static CoordinateReferenceSystem parseCRS(String text, boolean longFirst) throws FactoryException {
        CoordinateReferenceSystem crs;
        try {
            crs = CRS.fromWKT(text);
        } catch (FactoryException ex) {
            try {
                return CRS.forCode(text);
            } catch (FactoryException ex1) {
                ex.addSuppressed(ex1);
                throw ex;
            }
        }
        if (longFirst) {
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.DISPLAY_ORIENTED);
        }
        return crs;
    }

}
