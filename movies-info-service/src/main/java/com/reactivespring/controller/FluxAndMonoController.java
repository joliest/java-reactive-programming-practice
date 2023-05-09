package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {
    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.just(1, 2, 3)
                .log();
    }
    @GetMapping("/mono")
    public Mono<String> mono() {
        return Mono.just("Hello").log();
    }
    // produces = MediaType.TEXT_EVENT_STREAM_VALUE instruct endpoint to produce a stream of data to the client
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream() {
        // continously send some events with an interval of x
        // for every second, it will send update to the client
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
