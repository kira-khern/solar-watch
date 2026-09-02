# SolarWatch

A Spring Boot Web API that returns sunrise and sunset times for a given city
and date, in UTC and (optionally) in a local timezone.

## How it works

1. The client calls `GET /api/solarwatch?city={city}&date={date}`.
2. `GeocodingService` calls the **OpenWeather Geocoding API** to turn the city
   name into latitude/longitude coordinates.
3. `SunriseSunsetService` calls the **sunrise-sunset.org API** with those
   coordinates and the date, and gets back sunrise/sunset in UTC.
4. `SolarWatchService` combines both results into the final response, and
   optionally converts to a local timezone if one was supplied.

## Endpoint

```
GET /api/solarwatch?city=Budapest&date=2026-08-09
GET /api/solarwatch?city=Budapest&date=2026-08-09&timezone=Europe/Budapest
```

Example response:

```json
{
  "city": "Budapest",
  "country": "HU",
  "date": "2026-08-09",
  "sunriseUtc": "04:12:00",
  "sunsetUtc": "18:45:00",
  "sunriseLocal": "06:12:00",
  "sunsetLocal": "20:45:00"
}
```

`timezone` is optional and must be a valid IANA zone id (e.g. `Europe/Budapest`,
`America/New_York`). If omitted or invalid, `sunriseLocal`/`sunsetLocal`
mirror the UTC values.

### Error responses

| Situation                      | Status |
|---------------------------------|--------|
| City not found                  | 404    |
| Missing `city` or `date` param  | 400    |
| Invalid date format              | 400    |
| Upstream API unavailable/failing | 502    |

## Setup

1. Get a free API key from [OpenWeather](https://home.openweathermap.org/api_keys)
   (the Geocoding API is included in the free tier).
2. Copy `.env.example` to `.env` and fill in the values (or set them as
   environment variables / IntelliJ Run Configuration → Environment variables):

   ```
   OPENWEATHER_API_KEY=your-key-here
   JWT_SECRET=any-long-random-string
   ADMIN_PASSWORD=choose-a-password
   ```

   `DB_USERNAME` / `DB_PASSWORD` default to `postgres` / `postgres` for local
   development and only need to be set if your local Postgres differs.
3. Make sure a local PostgreSQL instance is running with a `solarwatch`
   database.

3. Run the app:

   ```
   ./mvnw spring-boot:run
   ```

## Tests

- `GeocodingServiceTest` / `SunriseSunsetServiceTest` — `@RestClientTest` +
  `MockRestServiceServer`, verifying request URLs and response parsing
  (including error/edge cases: empty results, non-OK status, server errors).
- `SolarWatchControllerTest` — `@WebMvcTest` with a mocked `SolarWatchService`,
  verifying HTTP status codes and JSON shape.

Run with:

```
./mvnw test
```

## Next steps (post-MVP)

- Dockerize the app and add a CI/CD pipeline.
- Persist lookups with Spring Data (e.g. cache coordinates per city).
- Connect the React frontend.
