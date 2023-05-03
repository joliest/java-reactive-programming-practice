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

    public Flux<String> explore_concat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWith() {
        // also available in FLux
        var abcMono = Mono.just("A");
        var defMono = Mono.just("B");

        // creates a Flux
        return abcMono.concatWith(defMono).log();
    }

    public Flux<String> explore_merge() {
        //
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100)); // A
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith() {
        //
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100)); // A
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeWith_mono() {
        //
        var aMono = Mono.just("A"); // A
        var dMono = Mono.just("D");

        return aMono.mergeWith(dMono).log();
    }

    public Flux<String> explore_mergeSequential() {
        // scenario when you have two data sources and you want them in order
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100)); // A
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }


    public Flux<String> explore_zip() {
        // scenario when you have two data sources and you want them in order
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log(); // AD, BE, CF
    }

    public Flux<String> explore_zip4() {
        // scenario when you have two data sources and you want them in order
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        // "AD14", "BE25", "CF36
        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(tupple -> tupple.getT1() + tupple.getT2() + tupple.getT3() + tupple.getT4())
                .log(); // AD, BE, CF
    }

    public Flux<String> explore_zipWith() {
        // scenario when you have two data sources and you want them in order
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log(); // AD, BE, CF
    }

    public Mono<String> explore_zipWith_mono() {
        // scenario when you have two data sources and you want them in order
        var abcMono = Mono.just("A");
        var defMono = Mono.just("D");

        return abcMono.zipWith(defMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log(); // AD, BE, CF
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
