package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // avoid conflicting to :8080 port
@ActiveProfiles("test") // this is important, avoid embedded mongo statup issues. should be different with your environments
@AutoConfigureWebTestClient //we need the webtest client to interact with the endpoint
class MoviesInfoControllerTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_INFO_URL = "/v1/movieinfos";

    @BeforeEach
    void setup() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        // saves the Movie Info data above
        movieInfoRepository.saveAll(movieinfos)
                .blockLast(); // should gets completed before TC will call findAll() or else, method wont find these newly added
        // disables async for tc only
        // block last is only available in test cases
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(new MovieInfo(null, "Batman Begins 1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
                .exchange() // make the call to the endpoint
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert  savedMovieInfo.getMovieInfoId() != null;
                });

    }

    @Test
    void getAllMovieInfos() {
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);

    }

    @Test
    void getAllMovieInfosByYear() {
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("year", 2005)
                .buildAndExpand()
                .toUri();
        webTestClient
                .get()
                .uri(uri)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);

    }

    @Test
    void getAllMovieInfosByName() {
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("name", "Batman Begins")
                .buildAndExpand()
                .toUri();
        webTestClient
                .get()
                .uri(uri)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);

    }

    @Test
    void findMovieInfoById_approach_1() {
        var movieInfoId = "abc";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    // get access of the actual body
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                });

    }

    @Test
    void findMovieInfoById_approach_2() {
        var movieInfoId = "abc";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                // another way, accessing the name on the , starts with "$" dollar sign
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");

    }

    @Test
    void findMovieInfoById_not_found() {
        var movieInfoId = "invalid id";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .isNotFound();

    }

    @Test
    void updateMovieInfo() {
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Dark Knight Rises (Updated)",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updateMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updateMovieInfo != null;
                    assert  updateMovieInfo.getMovieInfoId() != null;
                    assertEquals("Dark Knight Rises (Updated)", updateMovieInfo.getName());
                });

    }

    @Test
    void updateMovieInfo_notfound() {
        var movieInfoId = "not valid id";
        var movieInfo = new MovieInfo(null, "Dark Knight Rises (Updated)",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void deleteMovieInfo() {
        var movieInfoId = "abc";
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}