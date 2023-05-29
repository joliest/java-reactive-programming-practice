package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

/**
 * Difference with the non functional web is we dont specify the controller attribute value
 */
@WebFluxTest
/**
 * Specify the two beans this unit test requires
 */
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";

    @Test
    void addReview() {
        // Given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        /**
         * Careful of importing 'when()' as it auto imports the wrong one
         */
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        // When
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange() // make the call to the endpoint
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    // Then
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert  savedReview.getReviewId() != null;
                });
    }
    @Test
    void getReviews() {
        // Given
        List<Review> listToReturn = Lists.newArrayList(
                new Review("abc", 1L, "Awesome Movie", 9.0),
                new Review("def", 1L, "Awesome Movie1", 9.0),
                new Review("hij", 2L, "Excellent Movie", 8.0));
        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(listToReturn));
        // When
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void getReviewsByMovieInfoId() {
        // Given
        var movieInfoId = "1";
        var uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toUri();


        List<Review> listToReturn = Lists.newArrayList(
                new Review("abc", 1L, "Awesome Movie", 9.0),
                new Review("def", 1L, "Awesome Movie1", 9.0));
        when(reviewReactiveRepository.findReviewByMovieInfoId(isA(Long.class)))
                .thenReturn(Flux.fromIterable(listToReturn));

        // When
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void updateReviewById() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(review));
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));

        var id = "abc";
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", id)
                .bodyValue(Review.builder()
                        .comment("New Comment")
                        .rating(2D)
                        .build())
                .exchange()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var responseBody = reviewEntityExchangeResult.getResponseBody();
                    assert "New Comment".equals(responseBody.getComment());
                    assert 2D == responseBody.getRating();
                });
    }

    @Test
    void deleteReviewById() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(review));
        when(reviewReactiveRepository.deleteById(isA(String.class)))
                .thenReturn(Mono.empty());
        var id = "abc";
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
