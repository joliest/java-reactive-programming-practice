package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        // creating a Flux
        return Flux.fromIterable(List.of("Alex", "Joli", "Popoy")) // imagine we retrieved this in a db
                .log();
    }

    public Mono<String> nameMono() {
        // creating a Mono
        return Mono.just("Alex").log();
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
