package com.geomatys.crsservice.service;

import java.time.OffsetDateTime;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public interface CrsOperationService {

    /**
     * Create a source code to convert coordinates from a given <em>source CRS</em> to a <em>target CRS</em>.
     *
     * @param specification Wanted operation specification.
     * @return The source code to realize the operation, as a resource (i.e. a file or in-memory text)
     *
     * @throws IllegalArgumentException If the set of provided parameters is invalid (no source provided, etc.).
     * @throws UnsupportedOperationException If the set of input parameters is correct, but the service cannot handle it.
     *
     * @see <a href="https://docs.spring.io/spring-framework/reference/core/resources.html#resources-implementations">Spring resource interface documentation</a>
     */
    SourceCode getOperation(Specification specification) throws IllegalArgumentException, UnsupportedOperationException;

    record Specification(
            String source,
            String target,
            String format,
            @Nullable double[] aoi,
            @Nullable OffsetDateTime time
    ) {}

    record SourceCode(MediaType contentType, Resource sourceCode) {}
}
