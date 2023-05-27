package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Need @Component to be injected into RouterFunction<ServerResponse> reviewsRoute(...)
 */
@Component
public class ReviewHandler {

    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                /**
                 * We will perform save operation and return the value
                 * Everytime you're going to perform a reactive operation
                 * and return the value of that reactive operation
                 * you need to use a flat map operator
                 */
                .flatMap(reviewReactiveRepository::save)
                .flatMap(saveReview -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(saveReview));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var reviewsFlux = reviewReactiveRepository.findAll();

        // other way in sending body response
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var reviewFromDb = reviewReactiveRepository.findById(reviewId);
        return reviewFromDb
                .flatMap(existingReview ->
                        // accessing the request body
                        serverRequest.bodyToMono(Review.class)
                                .map(requestReview -> {
                                    existingReview.setComment(requestReview.getComment());
                                    existingReview.setRating(requestReview.getRating());
                                    return requestReview;
                                })
                                .flatMap(reviewReactiveRepository::save)
                                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }
}
