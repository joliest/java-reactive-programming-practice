package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    /**
     * @Bean is required
     */
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
                .GET("/v1/helloworld", request -> ServerResponse.ok().bodyValue("hello world"))
                /**
                 * 'request' needs to be passed to the handler
                 */
                .POST("/v1/reviews", request -> reviewHandler.addReview(request))
                .GET("/v1/reviews", request -> reviewHandler.getReviews(request))
                .build();
    }
}
