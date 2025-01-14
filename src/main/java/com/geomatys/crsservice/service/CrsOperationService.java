/*
 * Planet CRS Registry - The coordinates reference system registry for solar bodies
 * Copyright (C) 2025 - CNES (for PDSSP)
 *
 * This file is part of CRS Service.
 *
 * CRS Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License v3  as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CRS Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License v3  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License v3
 * along with CRS Service.  If not, see <https://www.gnu.org/licenses/>.
 */
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
