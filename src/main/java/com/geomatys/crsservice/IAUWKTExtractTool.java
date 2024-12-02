
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
