package com.geomatys.crsservice.service;

import java.time.OffsetDateTime;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public interface CrsOperationService {

    /**
     * Create a source code to convert coordinates from a given <em>source CRS</em> to a <em>target CRS</em>.
     *
     * @param parameters Wanted crs parameters.
     * @return The description of the CRS
     *
     * @throws IllegalArgumentException If the set of provided parameters is invalid (no source provided, etc.).
     * @throws UnsupportedOperationException If the set of input parameters is correct, but the service cannot handle it.
     *
     * @see <a href="https://docs.spring.io/spring-framework/reference/core/resources.html#resources-implementations">Spring resource interface documentation</a>
     */
    SourceCode getCRS(CRSParameters parameters) throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * Create a source code to convert coordinates from a given <em>source CRS</em> to a <em>target CRS</em>.
     *
     * @param parameters Wanted operation specification.
     * @return The source code to realize the operation, as a resource (i.e. a file or in-memory text)
     *
     * @throws IllegalArgumentException If the set of provided parameters is invalid (no source provided, etc.).
     * @throws UnsupportedOperationException If the set of input parameters is correct, but the service cannot handle it.
     *
     * @see <a href="https://docs.spring.io/spring-framework/reference/core/resources.html#resources-implementations">Spring resource interface documentation</a>
     */
    SourceCode getOperation(OperationParameters parameters) throws IllegalArgumentException, UnsupportedOperationException;

    record CRSParameters(
            /**
             * EPSG/CRS/IAU/IGNF code, WKT, or URN.
             */
            String source,
            /**
             * Set to true to force longitude first.
             */
            boolean longitudeFirst,
            /**
             * Output format, only application/json is supported.
             */
            String format
    ) {}

    record OperationParameters(
            String source,
            boolean sourceLongFirst,
            String target,
            boolean targetLongFirst,
            String format,
            @Nullable double[] aoi,
            @Nullable OffsetDateTime time
    ) {}

    record SourceCode(MediaType contentType, Resource sourceCode) {}
}
