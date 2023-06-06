package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var url = movieInfoUrl.concat("/" + movieId);
        // gives delay of 1 second and atempts 3 retries
        var retrySpec = Retry.fixedDelay(3, Duration.ofSeconds(1))
                // retry only for 5xx, leaves the 4xx alone
                .filter(ex -> ex instanceof MoviesInfoServerException)
                // anytime exception happens, we need to propagate the root cause of the issue so that client knows the actual error
                // Without this, you will get vague errors
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
        return webClient
                .get()
                .uri(url)
                .retrieve()
                // if status is 4xx, it will be custom handled
                .onStatus(HttpStatus::is4xxClientError,  clientResponse -> {
                    log.info("Status code is: {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                                "There is no MovieInfo available for the given Id: " + movieId,
                                clientResponse.statusCode().value()
                        ));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                    responseMessage, clientResponse.statusCode().value()
                            )));
                })
                // if status is 5xx, it will be custom handled
                .onStatus(HttpStatus::is5xxServerError,  clientResponse -> {
                    log.info("Status code is: {}", clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                    "Server error in MovieInfoService " + responseMessage
                            )));
                })
                .bodyToMono(MovieInfo.class)
                // retry the call for 3x if the call fails
//                .retry(3)
                .retryWhen(retrySpec)
                .log();
    }
}
