function SunArc({ sunriseLocal, sunsetLocal, isDaytime = true }) {
    return (
        <div className="sun-arc">
            <svg viewBox="0 0 200 110" className="sun-arc__svg" aria-hidden="true">
                <path
                    d="M 10 100 A 90 90 0 0 1 190 100"
                    fill="none"
                    stroke="rgba(255,255,255,0.35)"
                    strokeWidth="2"
                    strokeDasharray="4 6"
                    strokeLinecap="round"
                />
                <circle cx="100" cy="10" r="9" className={isDaytime ? "sun-arc__sun" : "sun-arc__moon"} />
            </svg>

            <div className="sun-arc__labels">
                <div className="sun-arc__label">
                    <span className="sun-arc__time">{formatTime(sunriseLocal)}</span>
                    <span className="sun-arc__caption">Sunrise</span>
                </div>
                <div className="sun-arc__label sun-arc__label--right">
                    <span className="sun-arc__time">{formatTime(sunsetLocal)}</span>
                    <span className="sun-arc__caption">Sunset</span>
                </div>
            </div>
        </div>
    );
}

function formatTime(value) {
    if (!value) return "—";
    return value.slice(0, 5);
}

export default SunArc;