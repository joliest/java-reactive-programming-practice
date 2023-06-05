package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // avoid conflicting to :8080 port
@ActiveProfiles("test") // this is important, avoid embedded mongo statup issues. should be different with your environments
@AutoConfigureWebTestClient // we need the webtest client to interact with the endpoint
@AutoConfigureWireMock(port = 8084) // for integration test only, spin up http server in port 8084 for the integration test to interact
@TestPropertySource(
        properties = {
                // we need to override the domain (8080 to 8084) in application.yml:5
                "restClient.movieInfoUrl=http://localhost:8084/v1/movieinfos",
                "restClient.reviewsUrl=http://localhost:8084/v1/reviews",
                // prevents java.lang.IllegalStateException: No Server ALPNProcessors - WireMock
                "wiremock.server.httpsPort=-1"
        }
)
public class MoviesControllerIntgTest {

        @Autowired
        WebTestClient webTestClient;

        @Test
        void retrieveMovieById() {
                // given
                var movieId = "abc";
                stubFor(WireMock.get(urlEqualTo("/v1/movieinfos/" + movieId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                // looks for this file at resources/__files/movieinfo.json
                                .withBodyFile("movieinfo.json")));

                stubFor(WireMock.get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                // looks for this file at resources/__files/reviews.json
                                .withBodyFile("reviews.json")));

                // wehn
                webTestClient.get()
                        .uri("/v1/movies/{id}", movieId)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Movie.class)
                        .consumeWith(movieEntityExchangeResult -> {
                                var movie = movieEntityExchangeResult.getResponseBody();
                                assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                                assertEquals("Batman Begins", movie.getMovieInfo().getName());
                        });


        }
}
