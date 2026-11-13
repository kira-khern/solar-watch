package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.SolarWatchResponse;
import com.codecool.solarwatch.service.SolarWatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SolarWatchController {

    private static final Logger logger = LoggerFactory.getLogger(SolarWatchController.class);

    private final SolarWatchService solarWatchService;

    public SolarWatchController(SolarWatchService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    @GetMapping("/api/solarwatch")
    public SolarWatchResponse getSolarWatch(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String timezone) {
        logger.info("Received request for city='{}', date='{}', timezone='{}'", city, date, timezone);
        return solarWatchService.getSolarWatch(city, date, timezone);
    }

    @GetMapping("/api/solarwatch/by-coordinates")
    public SolarWatchResponse getSolarWatchByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String timezone){
        logger.info("Received request for lat={}, lon={}, date='{}'", lat, lon, date);
        return solarWatchService.getSolarWatchByCoordinates(lat,lon,date,timezone);
    }
}
