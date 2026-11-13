package com.codecool.solarwatch.exception;

public class NoCityNearPointException extends RuntimeException {
    public NoCityNearPointException(double lat, double lon) {
        super("No city found near this point (%.2f, %.2f). Try clicking closer to a populated area.".formatted(lat,lon));
    }
}
