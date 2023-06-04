package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * To run this, run the following first
 * 1. MongoDB
 * 2. MoviesInfoServiceApplication
 * 3. MoviesReviewServiceApplication
 * 4. Movie Service app
 * 5. Create test data. See https://github.com/joliest/java-reactive-programming-practice/blob/62fe5e66b911bf5c3af2cff47fd40f311258f87a/movies-service/src/main/resources/curl-commands.txt
 *
 */

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }
    @GetMapping("/{movieId}")
    public Mono<Movie> retrieveMovieById(@PathVariable("movieId") String movieId) {
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                /**
                 * reviewsRestClient.retrieveReviews returns a Flux<Movie>. SInce we are dealing of transformation and  returning a reactive type
                 * We will use 'flatMap' operator
                 */
                .flatMap(movieInfo -> {
                    var reviewsListMono = reviewsRestClient.retrieveReviews(movieId)
                            /**
                             * Returns a Mono of list of reviews
                             */
                            .collectList();
                    return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }
}
