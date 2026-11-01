package com.codecool.solarwatch.integration;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.dto.auth.AuthResponse;
import com.codecool.solarwatch.dto.auth.RegisterRequest;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunsetTimes;
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
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolarWatchCacheHitIntegrationTest {

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
    void getSolarWatch_returnsCachedData_andNeverCallsExternalApis() {
        geocodingServer.enqueue(new MockResponse()
                .setBody("""
                        {"weather":[{"description":"clear sky","icon":"01d"}],"main":{"temp":21.5,"feels_like":20.8,"humidity":55},"wind":{"speed":3.1}}
                        """)
                .addHeader("Content-Type", "application/json"));

        City Budapest = cityRepository.save(new City(null, "Budapest", "HU", 47.4979, 19.0402, null));

        LocalDate date = LocalDate.of(2026, 6, 21);
        OffsetDateTime sunrise = OffsetDateTime.parse("2026-06-21T04:12:00Z");
        OffsetDateTime sunset = OffsetDateTime.parse("2026-06-21T18:45:00Z");
        sunTimesRepository.save(new SunriseSunsetTimes(Budapest, date, sunrise, sunset));

        String token = registerAndGetToken("cacheuser", "secretPassword123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<SolarWatchResponse> response = restTemplate.exchange(
                "/api/solarwatch?city=Budapest&date=2026-06-21",
                HttpMethod.GET,
                request,
                SolarWatchResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Budapest", response.getBody().city());
        assertEquals("04:12:00", response.getBody().sunriseUtc());
        assertEquals("18:45:00", response.getBody().sunsetUtc());
        assertNotNull(response.getBody().weather());
        assertEquals("clear sky", response.getBody().weather().description());
        assertEquals(21.5, response.getBody().weather().temperatureCelsius());
        assertEquals(1, geocodingServer.getRequestCount());
        assertEquals(0, sunriseSunsetServer.getRequestCount());
    }

    private String registerAndGetToken(String username, String password) {
        RegisterRequest registerRequest = new RegisterRequest(username, password);
        ResponseEntity<AuthResponse> authResponse =
                restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
        return authResponse.getBody().token();
    }
}