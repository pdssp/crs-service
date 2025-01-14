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
package com.geomatys.crsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

/**
 * Extraction des WKT de projections IAU venant de http://voparis-vespa-crs.obspm.fr:8080/docs#/Browse%20by%20WKT/get_wkts_ws_wkts_get
 *
 * @author Johann Sorel (Geomatys)
 */
public final class IAUWKTExtractTool {

    public static void main(String[] args) throws Exception {

        final HttpClient client = HttpClient.newBuilder().build();
        final HttpRequest requestCount = HttpRequest.newBuilder()
            .uri(new URI("http://voparis-vespa-crs.obspm.fr:8080/ws/wkts/count"))
            .GET()
            .build();
        final HttpResponse<String> responseCount = client.send(requestCount, HttpResponse.BodyHandlers.ofString());
        final long count = Long.parseLong(responseCount.body());

        final StringBuilder sb = new StringBuilder();
        final ObjectMapper jsonMapper = new ObjectMapper();
        for (long i = 0; i < count; i+= 100) {
            final HttpRequest requestWkt = HttpRequest.newBuilder()
                .uri(new URI("http://voparis-vespa-crs.obspm.fr:8080/ws/wkts?limit=100&offset=" + i + "&is_clean_formatting=true"))
                .GET()
                .build();
            final HttpResponse<String> responseWkts = client.send(requestWkt, HttpResponse.BodyHandlers.ofString());
            final List<Map> list = jsonMapper.readValue(responseWkts.body(), List.class);

            for (Map<String,Object> entry : list) {
                sb.append(entry.get("wkt"));
                sb.append("\n\n");
            }
        }

        Files.writeString(Paths.get("IAU.txt"), sb.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

}
