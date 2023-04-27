package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

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
    public Flux<String> namesFlux_concatMap(int stringLength) {
        // creating a Flux
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitString_withDelayForAsyncDemo) // preserves the ordering sequence, unlike the flatMap()
                .log();
    }

    public Mono<String> nameMono() {
        // creating a Mono
        return Mono.just("Alex");
    }

    public Mono<String> nameMono_map_filter(int stringLength) {
        return Mono.just("Alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
    }
    public Mono<List<String>> nameMono_flatMap(int stringLength) {
        return Mono.just("Alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono); // Mono<List<String>> A,L,E,X
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split(""); // returns String[]
        var charList = List.of(charArray); // ALEX -> A,L,E,X
        return Mono.just(charList)
                .log();
    }
    public Flux<String> nameMono_flatMapMany(int stringLength) {
        return Mono.just("Alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString_withDelayForAsyncDemo)
                .log(); // Mono<List<String>> A,L,E,X
    }

    // transform() is used to extract piece of functionality and assign it to variable.
    // (useful if you want a reusable function/logic)
    public Flux<String> nameFlux_transform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString);
    }
    public Flux<String> nameFlux_assigningDefaultValues(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }
    public Flux<String> nameFlux_switchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

       var defaultFlux =  Flux.just("default")
                .transform(filterMap); // returns D,E,F,A,U,,T
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux) // accepts a  Publisher
                .log();
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
