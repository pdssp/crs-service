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
package com.geomatys.crsservice.client;

import com.geomatys.crsservice.service.CrsOperationService;
import com.geomatys.crsservice.service.DefaultCrsOperationService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CoordinateOperationFactory backed by local (in-process/jvm) service.
 * This component is useful when we want to test crs service business layer without the application deployment burden.
 * It helps to test in environments where spring-boot test cannot be configured, as with GIGS tests.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class LocalCoordinateOperationFactory extends AbstractScriptingOperationFactory {

    private final CrsOperationService service;

    public LocalCoordinateOperationFactory() {
        this(new DefaultCrsOperationService());
    }

    public LocalCoordinateOperationFactory(CrsOperationService service) {
        this.service = Objects.requireNonNull(service);
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
    }

    @Override
    public Citation getVendor() {
        return new DefaultCitation("crs-service:local");
    }

    @Override
    String getOperationCode(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, String format) {
        final var parameters = new CrsOperationService.OperationParameters(crs1.toWKT(), false, crs2.toWKT(), false, format, null, null);
        final var operationText = service.getOperation(parameters);
        try {
            return operationText.sourceCode().getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load operation code in memory", e);
        }
    }
}
