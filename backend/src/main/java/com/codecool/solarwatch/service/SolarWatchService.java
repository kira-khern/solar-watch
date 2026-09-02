package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.dto.suntimes.SunTimes;
import com.codecool.solarwatch.model.City;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class SolarWatchService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final GeocodingService geocodingService;
    private final SunriseSunsetService sunriseSunsetService;

    public SolarWatchService(GeocodingService geocodingService, SunriseSunsetService sunriseSunsetService) {
        this.geocodingService = geocodingService;
        this.sunriseSunsetService = sunriseSunsetService;
    }

    public SolarWatchResponse getSolarWatch(String cityName, LocalDate date, String timezone) {
        City city = geocodingService.getCity(cityName);
        SunTimes sunTimes = sunriseSunsetService.getSunTimes(city, date);

        String sunriseUtc = sunTimes.sunriseUtc().format(TIME_FORMAT);
        String sunsetUtc = sunTimes.sunsetUtc().format(TIME_FORMAT);

        String sunriseLocal = sunriseUtc;
        String sunsetLocal = sunsetUtc;

        if (timezone != null && !timezone.isBlank()) {
            try {
                ZoneId zoneId = ZoneId.of(timezone);
                sunriseLocal = toZone(sunTimes.sunriseUtc(), zoneId);
                sunsetLocal = toZone(sunTimes.sunsetUtc(), zoneId);
            } catch (Exception ex) {
                // invalid timezone - fall back to UTC values
            }
        }

        return new SolarWatchResponse(
                city.getName(),
                city.getCountry(),
                date.toString(),
                sunriseUtc,
                sunsetUtc,
                sunriseLocal,
                sunsetLocal
        );
    }

    private String toZone(OffsetDateTime utcTime, ZoneId zoneId) {
        return utcTime.atZoneSameInstant(zoneId).format(TIME_FORMAT);
    }
}