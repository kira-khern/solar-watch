package com.codecool.solarwatch.integration;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.dto.auth.AuthResponse;
import com.codecool.solarwatch.dto.auth.RegisterRequest;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunTimesRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolarWatchCacheMissIntegrationTest {

    static MockWebServer geocodingServer;
    static MockWebServer sunriseSunsetServer;

    @BeforeAll
    static void startMockServers() throws IOException {
        geocodingServer = new MockWebServer();
        geocodingServer.start();

        sunriseSunsetServer = new MockWebServer();
        sunriseSunsetServer.start();
    }

    @AfterAll
    static void stopMockServers() throws IOException {
        geocodingServer.shutdown();
        sunriseSunsetServer.shutdown();
    }

    @DynamicPropertySource
    static void overrideExternalApiUrls(DynamicPropertyRegistry registry) {
        registry.add("openweather.geocoding.base-url", () -> geocodingServer.url("/").toString());
        registry.add("sunrisesunset.base-url", () -> sunriseSunsetServer.url("/").toString());
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SunTimesRepository sunTimesRepository;

    @Test
    void getSolarWatch_fetchesFromExternalApis_andPersistsResult_whenCityNotCachedYet() {
        geocodingServer.enqueue(new MockResponse()
                .setBody("""
                        [{"name":"Tokyo","lat":35.6897,"lon":139.6922,"country":"JP","state":null}]
                        """)
                .addHeader("Content-Type", "application/json"));

        sunriseSunsetServer.enqueue(new MockResponse()
                .setBody("""
                        {"results":{"sunrise":"2026-06-21T20:12:00+00:00","sunset":"2026-06-21T10:45:00+00:00"},"status":"OK"}
                        """)
                .addHeader("Content-Type", "application/json"));
        geocodingServer.enqueue(new MockResponse()
                .setBody("""
                        {"weather":[{"description":"light rain","icon":"10d"}],"main":{"temp":15.2,"feels_like":14.6,"humidity":80},"wind":{"speed":5.4}}
                        """)
                .addHeader("Content-Type", "application/json"));

        String token = registerAndGetToken("cachemissuser", "secretPassword123");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<SolarWatchResponse> response = restTemplate.exchange(
                "/api/solarwatch?city=Tokyo&date=2026-06-21",
                HttpMethod.GET,
                request,
                SolarWatchResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tokyo", response.getBody().city());
        assertEquals("20:12:00", response.getBody().sunriseUtc());
        assertEquals("10:45:00", response.getBody().sunsetUtc());
        assertNotNull(response.getBody().weather());
        assertEquals("light rain", response.getBody().weather().description());

        assertEquals(2, geocodingServer.getRequestCount());
        assertEquals(1, sunriseSunsetServer.getRequestCount());

        City savedCity = cityRepository.findByName("Tokyo").orElseThrow();
        assertEquals("JP", savedCity.getCountry());
        assertTrue(sunTimesRepository.findByCityAndDate(savedCity, LocalDate.of(2026, 6, 21)).isPresent());
    }

    private String registerAndGetToken(String username, String password) {
        RegisterRequest registerRequest = new RegisterRequest(username, password);
        ResponseEntity<AuthResponse> authResponse =
                restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
        return authResponse.getBody().token();
    }
}