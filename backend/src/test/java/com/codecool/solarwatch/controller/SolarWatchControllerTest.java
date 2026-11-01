package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.dto.weather.WeatherInfo;
import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.security.JwtService;
import com.codecool.solarwatch.service.SolarWatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolarWatchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SolarWatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolarWatchService solarWatchService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @Test
    void getSolarWatch_returns200AndBody_onValidRequest() throws Exception {
        WeatherInfo weather = new WeatherInfo(21.5, 20.8, "clear sky", "01d", 55, 3.1);
        SolarWatchResponse response = new SolarWatchResponse(
                "Budapest", "HU", "2026-08-09",
                "04:12:00", "18:45:00", "06:12:00", "20:45:00", weather);

        when(solarWatchService.getSolarWatch(eq("Budapest"), eq(LocalDate.of(2026, 8, 9)), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/solarwatch")
                        .param("city", "Budapest")
                        .param("date", "2026-08-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Budapest"))
                .andExpect(jsonPath("$.sunriseUtc").value("04:12:00"))
                .andExpect(jsonPath("$.sunsetUtc").value("18:45:00"));
    }

    @Test
    void getSolarWatch_returns404_whenCityNotFound() throws Exception {
        when(solarWatchService.getSolarWatch(eq("Atlantis"), any(), any()))
                .thenThrow(new CityNotFoundException("Atlantis"));

        mockMvc.perform(get("/api/solarwatch")
                        .param("city", "Atlantis")
                        .param("date", "2026-08-09"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Atlantis")));
    }

    @Test
    void getSolarWatch_returns400_whenDateParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/solarwatch")
                        .param("city", "Budapest"))
                .andExpect(status().isBadRequest());
    }
}