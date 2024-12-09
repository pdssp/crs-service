package com.geomatys.crsservice;

import org.apache.sis.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.opengis.util.FactoryException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StartupTest extends AbstractIntegrationTest {

    /**
     * This empty test ensures that Spring Boot application context loads properly.
     * i.e. it forces Spring to load and start the application.
     */
    @Test
    public void contextLoads() {}

    @Test
    public void testIAUFactoryLoads() throws FactoryException {
        assertNotNull(CRS.getAuthorityFactory("IAU"));
        var crs = CRS.forCode("IAU:2015:1055");
        assertNotNull(crs);
        assertEquals("Sun (2015) - Sphere / Robinson, clon = 180", crs.getName().getCode());
    }
}