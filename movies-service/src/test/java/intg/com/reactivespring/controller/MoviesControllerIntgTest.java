package com.reactivespring.controller;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // avoid conflicting to :8080 port
@ActiveProfiles("test") // this is important, avoid embedded mongo statup issues. should be different with your environments
@AutoConfigureWebTestClient // we need the webtest client to interact with the endpoint
@AutoConfigureWireMock(port = 8084) // for integration test only, spin up http server in port 8084 for the integration test to interact
@TestPropertySource(
        properties = {
                // we need to override the domain (8080 to 8084) in application.yml:5
                "restClient.movieInfoUrl=http://localhost:8084/v1/movieinfos",
                "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntgTest {
}
