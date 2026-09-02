package com.codecool.solarwatch.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SunriseSunsetWebClientConfig {

    @Bean
    public WebClient sunriseSunsetWebClient(
            WebClient.Builder builder,
            @Value("${sunrisesunset.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}