package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        // creating a Flux
        return Flux.fromIterable(List.of("Alex", "Joli", "Popoy", "Khaye")); // imagine we retrieved this in a db
    }
    public Flux<String> namesFlux_map() {
        // creating a Flux
        return Flux.fromIterable(List.of("Alex", "Joli", "Popoy"))
                .map(String::toUpperCase)
                .log();
    }
    public Flux<String> namesFlux_immutability() {
        var nameFlux = Flux.fromIterable(List.of("Alex", "Joli", "Popoy"));
        // this will cause error. It needs to be attached via method chaining
        // map always returns a new FluxMap instance, so this below will not work.
        nameFlux.map(String::toUpperCase);
        return nameFlux;
    }

    public Flux<String> namesFlux_filter(int stringLength) {
        // creating a Flux
        return Flux.fromIterable(List.of("Alex", "Joli", "Popoy"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }
    public Flux<String> namesFlux_flatMap(int stringLength) {
        // creating a Flux
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString) // "A", "L","E","X","C","H","L","O","E"
                .log();
    }
    // ALEX -> Flux(A,L,E,X)
    public Flux<String> splitString(String name) {
        var charArray = name.split(""); // returns String[]
        return Flux.fromArray(charArray);
    }
    // demonstrating Asynchronous nature of flatMap()
    public Flux<String> namesFlux_flatMapAsync(int stringLength) {
        // creating a Flux
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                /**
                 * make the call asynchronous, wait for the response of all the results
                 */
                .flatMap(this::splitString_withDelayForAsyncDemo) // async
                .log();
    }
    public Flux<String> splitString_withDelayForAsyncDemo(String name) {
        var charArray = name.split(""); // returns String[]
        var delay = new Random().nextInt(1000); // random value between 0 - 1000
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay)); // make this async
    }

    public Mono<String> nameMono() {
        // creating a Mono
        return Mono.just("Alex");
    }
    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        // Only way to access the value, "subscribe" to the Flux
        // If you dont subscribe, there will be no data to access
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
//                    System.out.println("Name is: " + name);
                }); // will be sent in a string one by one

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Name is: " + name);
                });
    }
}
