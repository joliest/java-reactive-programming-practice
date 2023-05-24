package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    static String MOVIES_INFO_URL = "/v1/movieinfos";

    @Test
    void getAllMoviesInfo() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));
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
    void getMovieInfoById() {
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        when(movieInfoServiceMock.getMovieInfoById(movieInfoId)).thenReturn(Mono.just(movieInfo));
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class);

    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo("mockId", "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));
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
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());
                });

    }

    @Test
    void addMovieInfo_validation() {
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(new MovieInfo(null, null,
                        -2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                   var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println("responsBody ::: " + responseBody);
                   assert responseBody != null;

                    var expectedErrorMsg = "movieInfo.name must be present,movieInfo.year must be a positive value";

                    assertEquals(expectedErrorMsg, responseBody);
                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        when(movieInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(Mono.just(
                new MovieInfo(movieInfoId, "Dark Knight Rises (Updated)",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));
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
    void deleteMovieInfo() {
        var movieInfoId = "abc";
        when(movieInfoServiceMock.deleteMovieInfo(movieInfoId)).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

}