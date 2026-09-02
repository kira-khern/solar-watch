import { useState } from "react";
import { getSolarWatch } from "../apiClient";
import { useAuth } from "../context/AuthContext.jsx";

function SolarWatchPage() {
    const [city, setCity] = useState("");
    const [date, setDate] = useState(() => new Date().toISOString().split("T")[0]);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const { token, logout } = useAuth();

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

    return (
        <div className="page-container">
            <div className="card" style={{ maxWidth: 480 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <h1>SolarWatch</h1>
                    <button onClick={logout} className="link-text" style={{ background: "none", border: "none", cursor: "pointer" }}>
                        Log out
                    </button>
                </div>
                <p className="subtitle">Find the sunrise and sunset time for any city.</p>

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
                    <div style={{ marginTop: 28, paddingTop: 24, borderTop: "1px solid var(--color-border)" }}>
                        <h2>{result.city}, {result.country}</h2>
                        <p className="subtitle">{result.date}</p>

                        <div style={{ display: "flex", gap: 24 }}>
                            <div>
                                <p className="subtitle" style={{ marginBottom: 4 }}>Sunrise</p>
                                <h2 style={{ color: "var(--color-primary-dark)" }}>{result.sunriseLocal}</h2>
                            </div>
                            <div>
                                <p className="subtitle" style={{ marginBottom: 4 }}>Sunset</p>
                                <h2 style={{ color: "var(--color-secondary)" }}>{result.sunsetLocal}</h2>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default SolarWatchPage;