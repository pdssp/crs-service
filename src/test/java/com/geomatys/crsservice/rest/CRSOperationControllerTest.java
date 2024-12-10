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
}
