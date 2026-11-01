import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../apiClient";
import { useAuth } from "../context/AuthContext.jsx";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      const response = await loginUser(username, password);
      login(response.token);
      navigate("/solar-watch");
    } catch (err) {
      setError("Invalid username or password.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="page-container">
      <div className="card">
        <p className="brand">☀️ SolarWatch</p>
        <h1>Welcome back</h1>
        <p className="subtitle">Log in to check today's sunrise, sunset and weather.</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <p className="error-text">{error}</p>}

          <button type="submit" className="btn" disabled={isSubmitting}>
            {isSubmitting ? "Logging in..." : "Log in"}
          </button>
        </form>

        <p className="link-text">
          Don't have an account? <Link to="/registration">Register</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;