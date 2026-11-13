package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.dto.suntimes.SunTimes;
import com.codecool.solarwatch.dto.weather.WeatherInfo;
import com.codecool.solarwatch.model.City;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class SolarWatchService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final GeocodingService geocodingService;
    private final SunriseSunsetService sunriseSunsetService;
    private final WeatherService weatherService;

    public SolarWatchService(GeocodingService geocodingService, SunriseSunsetService sunriseSunsetService, WeatherService weatherService) {
        this.geocodingService = geocodingService;
        this.sunriseSunsetService = sunriseSunsetService;
        this.weatherService = weatherService;
    }

    public SolarWatchResponse getSolarWatch(String cityName, LocalDate date, String timezone) {
        return buildResponse(geocodingService.getCity(cityName), date, timezone);
    }

    public SolarWatchResponse getSolarWatchByCoordinates(double latitude, double longitude, LocalDate date, String timezone) {
        return buildResponse(geocodingService.getCityByCoordinates(latitude, longitude), date, timezone);
    }

    public SolarWatchResponse buildResponse(City city, LocalDate date, String timezone) {
        SunTimes sunTimes = sunriseSunsetService.getSunTimes(city, date);
        WeatherInfo weather = weatherService.getCurrentWeatherInfo(city);

        String sunriseUtc = sunTimes.sunriseUtc().format(TIME_FORMAT);
        String sunsetUtc = sunTimes.sunsetUtc().format(TIME_FORMAT);

        String sunriseLocal = sunriseUtc;
        String sunsetLocal = sunsetUtc;

        ZoneId zoneId = null;
        if (timezone != null && !timezone.isBlank()) {
            try {
                zoneId = ZoneId.of(timezone);
            } catch (Exception ex) {
            }
        }
            if (zoneId == null) {
                zoneId = ZoneOffset.ofTotalSeconds(weather.utcOffsetSeconds());
            }
            sunriseLocal = toZone(sunTimes.sunriseUtc(), zoneId);
            sunsetLocal = toZone(sunTimes.sunsetUtc(), zoneId);


        return new SolarWatchResponse(
                city.getName(),
                city.getCountry(),
                date.toString(),
                sunriseUtc,
                sunsetUtc,
                sunriseLocal,
                sunsetLocal,
                weather,
                city.getLatitude(),
                city.getLongitude()
        );
    }

    private String toZone(OffsetDateTime utcTime, ZoneId zoneId) {
        return utcTime.atZoneSameInstant(zoneId).format(TIME_FORMAT);
    }
}