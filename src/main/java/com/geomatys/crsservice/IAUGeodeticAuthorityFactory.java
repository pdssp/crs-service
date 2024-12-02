
package com.geomatys.crsservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.io.wkt.WKTDictionary;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class IAUGeodeticAuthorityFactory extends WKTDictionary implements CRSAuthorityFactory {

    public IAUGeodeticAuthorityFactory() throws IOException, FactoryException {
        super(new DefaultCitation("IAU"));
        try (BufferedReader source = Files.newBufferedReader(Path.of("IAU.txt"))) {
            load(source);
        }
    }
}
