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

    @Test
    void explore_concat() {
        var concatFlux = fluxAndMonoGeneratorService.explore_concat();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
    @Test
    void explore_concatWith() {
        var concatMono = fluxAndMonoGeneratorService.explore_concatWith();
        StepVerifier.create(concatMono)
                .expectNext("A", "B")
                .verifyComplete();
    }
    @Test
    void explore_merge() {
        var value = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }
    @Test
    void explore_mergeWith() {
        var value = fluxAndMonoGeneratorService.explore_mergeWith();
        StepVerifier.create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }
    @Test
    void explore_mergeSequential() {
        var value = fluxAndMonoGeneratorService.explore_mergeSequential();
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
    @Test
    void explore_zip() {
        var value = fluxAndMonoGeneratorService.explore_zip();
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
    @Test
    void explore_zip4() {
        var value = fluxAndMonoGeneratorService.explore_zip4();
        StepVerifier.create(value)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }
}