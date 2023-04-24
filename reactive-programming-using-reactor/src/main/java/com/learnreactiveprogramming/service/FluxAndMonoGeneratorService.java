package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
