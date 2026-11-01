import { useState } from "react";

function WeatherIcon({ icon, description, size = 64 }) {
    const [failed, setFailed] = useState(false);

    if (!icon) return null;

    if (failed) {
        return (
            <span className="weather-icon weather-icon--fallback" style={{ fontSize: size * 0.6 }}>
                🌤️
            </span>
        );
    }

    return (
        <img
            className="weather-icon"
            src={`https://openweathermap.org/img/wn/${icon}@2x.png`}
            alt={description || "Weather icon"}
            width={size}
            height={size}
            loading="lazy"
            onError={() => setFailed(true)}
        />
    );
}

export default WeatherIcon;