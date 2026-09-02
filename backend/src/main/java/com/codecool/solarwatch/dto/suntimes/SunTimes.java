package com.codecool.solarwatch.dto.suntimes;

import java.time.OffsetDateTime;

public record SunTimes(OffsetDateTime sunriseUtc, OffsetDateTime sunsetUtc) {
}
