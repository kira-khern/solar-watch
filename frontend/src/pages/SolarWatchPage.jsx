import { useState } from "react";
import { getSolarWatch } from "../apiClient";
import { useAuth } from "../context/AuthContext.jsx";
import SunArc from "../components/SunArc.jsx";
import WeatherIcon from "../components/WeatherIcon.jsx";

function SolarWatchPage() {
    const [city, setCity] = useState("");
    const [date, setDate] = useState(() => new Date().toISOString().split("T")[0]);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const { logout, token } = useAuth();

    async function handleSubmit(event) {
        event.preventDefault();
        setError(null);
        setResult(null);
        setIsLoading(true);

        try {
            const data = await getSolarWatch(city, date, token);
            setResult(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    }

    const weather = result?.weather;

    return (
        <div className="page-container">
            <div className="card" style={{ maxWidth: 460 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <h1>SolarWatch</h1>
                    <button onClick={logout} className="btn-ghost">
                        Log out
                    </button>
                </div>
                <p className="subtitle">Find the sunrise, sunset and current weather for any city.</p>

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

                    {error && <p className="error-text">{error}</p>}

                    <button type="submit" className="btn" disabled={isLoading}>
                        {isLoading ? "Searching..." : "Get sunrise & sunset"}
                    </button>
                </form>

                {result && (
                    <>
                        <hr className="divider" />

                        <div className="result-header">
                            <div className="result-location">
                                <h2>{result.city}, {result.country}</h2>
                                <p className="subtitle">{result.date}</p>
                            </div>
                        </div>

                        {weather && (
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