package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {
    @Test
    void sinks_replay() {
        // Creating a Sink
        // this sink will emit or publish multiple events
        // anytime a subscriber is connected into it will go to replay all the events
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        // emitting a signal
        // Think a producer manually sending onNext
        // think of this are data being published
        // we need to assign a failure handler
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // subscribe, gives you access to actual data that were published
        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        // another subscriber
        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });

        replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        // this new subscriber will get all the events because we used the replay() operator
        Flux<Integer> integerFlux3 = replaySink.asFlux();
        integerFlux3.subscribe((i) -> {
            System.out.println("Subscriber 3 : " + i);
        });
    }

    @Test
    // transmits only the newly published data
    void sinks_multicast() {
        // you need to add onBackpressureBuffer - newly pushed as in "after the subscriber's subscription"
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();

        // subscriber 1
        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        // emitting events
        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // another subscriber
        Flux<Integer> integerFlux2 = multicast.asFlux();
            integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });
        multicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
