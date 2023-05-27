package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // avoid conflicting to :8080 port
@ActiveProfiles("test") // this is important, avoid embedded mongo statup issues. should be different with your environments
@AutoConfigureWebTestClient //we need the webtest client to interact with the endpoint
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setup() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();

    }

    @AfterEach
    void teardown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void addReview() {
        // Given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

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
}
