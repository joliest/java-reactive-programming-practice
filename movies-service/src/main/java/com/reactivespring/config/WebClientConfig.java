package com.reactivespring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * hold the bean of WebClient
 */
@Configuration
public class WebClientConfig {

    /**
     * Injected as a bean to the whole spring context
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        /**
         * automatically create the webclient instance for our application
         */
        return builder.build();
    }
}
