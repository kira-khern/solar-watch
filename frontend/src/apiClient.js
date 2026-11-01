const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function request(path, { method = "GET", body, token } = {}) {
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    if (response.status === 401 && token) {
        localStorage.removeItem("token");
        window.location.assign("/login");
        throw new Error("Your session has expired. Please log in again.");
    }

    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        throw new Error(errorBody.message || `Request failed with status ${response.status}`);
    }

    return response.json();
}

export function registerUser(username, password) {
    return request("/api/auth/register", { method: "POST", body: { username, password } });
}

export function loginUser(username, password) {
    return request("/api/auth/login", { method: "POST", body: { username, password } });
}

export function getSolarWatch(city, date, token) {
    const params = new URLSearchParams({ city, date });
    return request(`/api/solarwatch?${params}`, { token });
}