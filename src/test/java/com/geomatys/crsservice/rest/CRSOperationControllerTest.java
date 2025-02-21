package com.geomatys.crsservice.rest;

import com.geomatys.crsservice.AbstractIntegrationTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.Assertions.assertThat;

public class CRSOperationControllerTest extends AbstractIntegrationTest {

    @Test
    public void getIAUCrsDefinition() {
        var client = createClient();
        var body = client.get().uri("/crs/define?source=IAU:1055&format=application/json")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        assertThat(body).containsAllEntriesOf(Map.of(
                "code", "IAU:1055",
                "units", "m",
                "axisDirection", List.of("east", "north")
        ));
    }
    @Test
    public void getPythonDefinition() {
//        CoordinateOperationFactory factory = new CRSServiceCoordinateOperationFactory(new URI(getServerUrl()+"/crs/operation"));
        var client = createClient();
//        "http://localhost:8080/crs/operation?source=EPSG:4326&sourceLongitudeFirst=false&target=EPSG:3395&targetLongitudeFirst=false&format=text/javascript"
//        var body = client.get().uri("crs/operation?source=EPSG:4813&sourceLongitudeFirst=false&target=EPSG:5330&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4978&sourceLongitudeFirst=false&target=EPSG:4979&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4326&sourceLongitudeFirst=false&target=EPSG:32631&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4283&sourceLongitudeFirst=false&target=EPSG:28354&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4190&sourceLongitudeFirst=false&target=EPSG:22175&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4807&sourceLongitudeFirst=false&target=EPSG:27572&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4313&sourceLongitudeFirst=false&target=EPSG:31370&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4152&sourceLongitudeFirst=false&target=EPSG:2921&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4152&sourceLongitudeFirst=false&target=EPSG:3568&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4211&sourceLongitudeFirst=false&target=EPSG:3001&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4813&sourceLongitudeFirst=false&target=EPSG:5330&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:2049&sourceLongitudeFirst=false&target=EPSG:4148&targetLongitudeFirst=false&format=text/x-python")
//        var body = client.get().uri("crs/operation?source=EPSG:4283&sourceLongitudeFirst=false&target=EPSG:3577&targetLongitudeFirst=false&format=text/x-python")
        var body = client.get().uri("crs/operation?source=EPSG:4326&sourceLongitudeFirst=false&target=EPSG:4978&targetLongitudeFirst=false&format=text/x-python")
                .retrieve()
                .body(new ParameterizedTypeReference<String>() {});
        System.out.println(body);
//        assertThat(body).containsAllEntriesOf(Map.of(
//                "code", "IAU:1055",
//                "units", "m",
//                "axisDirection", List.of("east", "north")
//        ));
    }
}
