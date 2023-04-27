package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

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
                .expectNext("Alex", "Joli", "Popoy")
                .verifyComplete();
    }

    @Test
    void namesFlux_filter() {
        int stringLength = 4;
        var namesFlux_map = fluxAndMonoGeneratorService.namesFlux_filter(stringLength);

        StepVerifier.create(namesFlux_map)
                .expectNext("POPOY")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatMap() {
        var namesFluxFlatMap = fluxAndMonoGeneratorService.namesFlux_flatMap(3);

        StepVerifier.create(namesFluxFlatMap)
                .expectNext("A", "L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMapAsync() {
        var namesFluxFlatMap = fluxAndMonoGeneratorService.namesFlux_flatMapAsync(3);

        StepVerifier.create(namesFluxFlatMap)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {
        var namesFluxConcatFlatMap = fluxAndMonoGeneratorService.namesFlux_concatMap(3);

        StepVerifier.create(namesFluxConcatFlatMap)
                .expectNext("A", "L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void nameMono_flatMap() {
        int stringLength = 3;

        var value = fluxAndMonoGeneratorService.nameMono_flatMap(stringLength);

        StepVerifier.create(value)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void nameMono_flatMapMany() {
        int stringLength = 3;

        var value = fluxAndMonoGeneratorService.nameMono_flatMapMany(stringLength);

        StepVerifier.create(value)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void nameFlux_transform() {
        var namesFluxFlatMap = fluxAndMonoGeneratorService.nameFlux_transform(3);

        StepVerifier.create(namesFluxFlatMap)
                .expectNext("A", "L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void nameFlux_assigningDefaultValues() {
        var namesFluxFlatMap = fluxAndMonoGeneratorService.nameFlux_assigningDefaultValues(6);

        StepVerifier.create(namesFluxFlatMap)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void nameFlux_switchIfEmpty() {
        var namesFluxFlatMap = fluxAndMonoGeneratorService.nameFlux_switchIfEmpty(6);

        StepVerifier.create(namesFluxFlatMap)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }
}