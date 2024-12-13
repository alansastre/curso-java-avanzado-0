package com.certidevs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient manufacturersClient() {
        return WebClient.builder()
                .baseUrl("https://manufacturer.com")
                .build();
    }
    @Bean
    public WebClient ordersClient() {
        return WebClient.builder()
                .baseUrl("https://orders.com")
                .build();
    }
    @Bean
    public WebClient customersClient() {
        return WebClient.builder()
                .baseUrl("https://customers.com")
                .build();
    }
    @Bean
    public WebClient ratingsClient() {
        return WebClient.builder()
                .baseUrl("https://ratings.com")
                .build();
    }
}
