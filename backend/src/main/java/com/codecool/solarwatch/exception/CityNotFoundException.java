package com.codecool.solarwatch.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("Could not find coordinates for city: '%s'. Check the spelling.".formatted(city));
    }
}
