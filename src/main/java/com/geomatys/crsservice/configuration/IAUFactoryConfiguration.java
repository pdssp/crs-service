package com.geomatys.crsservice.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.sis.io.wkt.WKTDictionary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableConfigurationProperties(IAUFactoryConfiguration.IAUFactoryProperties.class)
public class IAUFactoryConfiguration implements InitializingBean {

    private final System.Logger logger = System.getLogger("com.geomatys.crsservice.configuration");
    private final IAUFactoryProperties properties;

    public IAUFactoryConfiguration(IAUFactoryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws IOException, FactoryException {
        var factory = IAUGeodeticAuthorityFactory.INSTANCE;
        final int nbCrsBeforeImport = factory.getAuthorityCodes(CoordinateReferenceSystem.class).size();

        importWkts(factory, properties.wkt());

        final int nbImportedCrss = factory.getAuthorityCodes(CoordinateReferenceSystem.class).size() - nbCrsBeforeImport;
        logger.log(System.Logger.Level.INFO, "{0} CRSs have been loaded for IAU authority", nbImportedCrss);
    }

    private void importWkts(final WKTDictionary target, final WKTSourceProperties wktSource) throws FactoryException, IOException {
        logger.log(System.Logger.Level.INFO, "Loading IAU CRS definitions from {0} using encoding {1}", wktSource.file(), wktSource.charset());
        try (var rawStream = wktSource.file().getInputStream();
             var textStream = new InputStreamReader(rawStream, wktSource.charset());
             var reader =  wktSource.replaceGeog() ? new ReplaceGeogBufferedReader(textStream) : new BufferedReader(textStream)
        ) {
            target.load(reader);
        }
    }

    /**
     * IAU factory externalized configuration
     */
    @ConfigurationProperties(prefix = "geomatys.iau.factory")
    public record IAUFactoryProperties(WKTSourceProperties wkt) {}

    public record WKTSourceProperties(Resource file, Charset charset, boolean replaceGeog) {}

    /**
     * HACK: replace all <code>BASEGEOGCRS</code> terms in WKTs with <code>BASEGEODCRS</code>.
     * This is required until APache SIS update.
     */
    private final class ReplaceGeogBufferedReader extends BufferedReader {

        public ReplaceGeogBufferedReader(Reader in, int sz) {
            super(in, sz);
        }

        public ReplaceGeogBufferedReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            var line = super.readLine();
            if (line != null) line = line.replaceAll("(?i)BASEGEOGCRS", "BASEGEODCRS");
            return line;
        }
    }
}
