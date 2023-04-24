package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        // given

        // when
        // Flux is a 'reactor' type, not a normal object that you can test
        // by assert statements.
        var nameFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(nameFlux)
                // expect these elements once it invokes
                .expectNext("Alex", "Joli", "Popoy", "Khaye")
//                .expectNextCount(3)
                // one event is already consumed, so we count the remaining one
                // if "expectNext" is invoked and "expectNextCount" is present, it should be "3"
//                .expectNext("Alex")
//                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {
        var namesFlux_map = fluxAndMonoGeneratorService.namesFlux_map();

        StepVerifier.create(namesFlux_map)
                .expectNext("ALEX", "JOLI", "POPOY")
                .verifyComplete();
    }

    // this will fail as flux is immutable, it should only be chained
    @Test
    void namesFlux_immutability() {
        var namesFlux_map = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux_map)
                .expectNext("ALEX", "JOLI", "POPOY")
                .verifyComplete();
    }
}