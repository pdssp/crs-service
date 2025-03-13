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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CoordinateOperationFactory backed by a distance server.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CRSServiceCoordinateOperationFactory extends AbstractScriptingOperationFactory {

    private final URI serviceURL;

    public CRSServiceCoordinateOperationFactory(URI serviceURL) {
        this.serviceURL = serviceURL;
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
    }

    @Override
    String getOperationCode(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, String format) {
        final String crs1Txt = URLEncoder.encode(crs1.toWKT(), StandardCharsets.UTF_8);
        final String crs2Txt = URLEncoder.encode(crs2.toWKT(), StandardCharsets.UTF_8);
        final String query = serviceURL.toString() + "?source=" + crs1Txt + "&target=" + crs2Txt + "&format=" + format;

        return getText(query);
    }

    @Override
    public Citation getVendor() {
        return new DefaultCitation(serviceURL.toString());
    }

    private static String getText(String url) {
        try (final HttpClient client = HttpClient.newBuilder().build()) {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }
}
