package com.codecool.solarwatch.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeocodingWebClientConfig {

    @Bean
    public WebClient geocodingWebClient(
            WebClient.Builder builder,
            @Value("${openweather.geocoding.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}