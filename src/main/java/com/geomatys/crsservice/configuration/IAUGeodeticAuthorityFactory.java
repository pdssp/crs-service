
package com.geomatys.crsservice.configuration;

import java.util.Set;
import org.apache.sis.io.wkt.WKTDictionary;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.referencing.factory.GeodeticAuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public final class IAUGeodeticAuthorityFactory extends GeodeticAuthorityFactory implements CRSAuthorityFactory {

    static final WKTDictionary INSTANCE = new WKTDictionary(new DefaultCitation("IAU"));

    public IAUGeodeticAuthorityFactory() {}

    @Override
    public CoordinateReferenceSystem createCoordinateReferenceSystem(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createCoordinateReferenceSystem(s);
    }

    @Override
    public CompoundCRS createCompoundCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createCompoundCRS(s);
    }

    @Override
    public DerivedCRS createDerivedCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createDerivedCRS(s);
    }

    @Override
    public EngineeringCRS createEngineeringCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createEngineeringCRS(s);
    }

    @Override
    public GeographicCRS createGeographicCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createGeographicCRS(s);
    }

    @Override
    public GeocentricCRS createGeocentricCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createGeocentricCRS(s);
    }

    @Override
    public ImageCRS createImageCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createImageCRS(s);
    }

    @Override
    public ProjectedCRS createProjectedCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createProjectedCRS(s);
    }

    @Override
    public TemporalCRS createTemporalCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createTemporalCRS(s);
    }

    @Override
    public VerticalCRS createVerticalCRS(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createVerticalCRS(s);
    }

    @Override
    public Citation getAuthority() {
        return INSTANCE.getAuthority();
    }

    @Override
    public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> aClass) throws FactoryException {
        return INSTANCE.getAuthorityCodes(aClass);
    }

    @Override
    public InternationalString getDescriptionText(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.getDescriptionText(s);
    }

    @Override
    public IdentifiedObject createObject(String s) throws NoSuchAuthorityCodeException, FactoryException {
        return INSTANCE.createObject(s);
    }

    @Override
    public Citation getVendor() {
        return INSTANCE.getVendor();
    }
}
