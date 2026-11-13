import { useState } from "react";
import { getSolarWatch, getSolarWatchByCoordinates } from "../apiClient";
import { useAuth } from "../context/AuthContext.jsx";
import CityMap from "../components/CityMap.jsx";
import SunArc from "../components/SunArc.jsx";
import WeatherIcon from "../components/WeatherIcon.jsx";

function SolarWatchPage() {
    const [city, setCity] = useState("");
    const [date, setDate] = useState(() => new Date().toISOString().split("T")[0]);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const { logout, token } = useAuth();

    async function runSearch(fetchData) {
        setError(null);
        setIsLoading(true);

        try {
            const data = await fetchData();
            setResult(data);
            setCity(data.city);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    }

    function handleSubmit(event) {
        event.preventDefault();
        runSearch(() => getSolarWatch(city, date, token));
    }

    function handleMapClick(lat, lon) {
        if (isLoading) return;
        runSearch(() => getSolarWatchByCoordinates(lat, lon, date, token));
    }

    const todayStr = new Date().toISOString().split("T")[0];
    const weather = result?.date === todayStr ? result?.weather : null;

    return (
        <div className="page-container">
            <div className="card" style={{ maxWidth: 460 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <h1>SolarWatch</h1>
                    <button onClick={logout} className="btn-ghost">
                        Log out
                    </button>
                </div>
                <p className="subtitle">
                    Find the sunrise and sunset for any city and date, plus live weather for today.
                </p>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="city">City</label>
                        <input
                            id="city"
                            type="text"
                            placeholder="e.g. Budapest"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="date">Date</label>
                        <input
                            id="date"
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn" disabled={isLoading}>
                        {isLoading ? "Searching..." : "Get sunrise & sunset"}
                    </button>
                </form>

                <hr className="divider" />

                <CityMap
                    latitude={result?.latitude}
                    longitude={result?.longitude}
                    label={result ? `${result.city}, ${result.country}` : ""}
                    onMapClick={handleMapClick}
                />
                <p className="map-hint">Tip: click anywhere on the map to look up the nearest city.</p>

                {error && <p className="error-text">{error}</p>}

                {result && (
                    <>
                        <div className="result-header">
                            <div className="result-location">
                                <h2>{result.city}, {result.country}</h2>
                                <p className="subtitle">{result.date}</p>
                            </div>
                        </div>

                        {weather && (
                            <>
                                <p className="result-label">Right now</p>
                                <div className="result-current">
                                    <div>
                                        <div className="result-temp">{Math.round(weather.temperatureCelsius)}°</div>
                                        <div className="result-temp-meta">
                                            Feels like {Math.round(weather.feelsLikeCelsius)}°
                                        </div>
                                    </div>
                                    <div className="result-condition">
                                        <WeatherIcon icon={weather.icon} description={weather.description} />
                                        <span className="result-condition-text">{weather.description}</span>
                                    </div>
                                </div>
                            </>
                        )}

                        <SunArc sunriseLocal={result.sunriseLocal} sunsetLocal={result.sunsetLocal} />

                        {weather && (
                            <div className="stat-grid">
                                <div className="stat-card">
                                    <div className="stat-card-label">Humidity</div>
                                    <div className="stat-card-value">{weather.humidityPercent}%</div>
                                </div>
                                <div className="stat-card">
                                    <div className="stat-card-label">Wind</div>
                                    <div className="stat-card-value">{weather.windSpeedMps} m/s</div>
                                </div>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

export default SolarWatchPage;