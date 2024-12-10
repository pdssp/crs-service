package com.geomatys.crsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder clientBuilder;

    public String getServerUrl() { return "http://localhost:"+port; }
    public RestClient createClient() { return clientBuilder.baseUrl(getServerUrl()).build(); }
}
