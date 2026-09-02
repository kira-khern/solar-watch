import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../apiClient";
import { useAuth } from "../context/AuthContext.jsx";

function RegisterPage() {
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
            const response = await registerUser(username, password);
            login(response.token);
            navigate("/solar-watch");
        } catch (err) {
            setError(err.message);
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="page-container">
            <div className="card">
                <h1>Create your account</h1>
                <p className="subtitle">Track sunrise and sunset for any city.</p>

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
                        {isSubmitting ? "Creating account..." : "Register"}
                    </button>
                </form>

                <p className="link-text">
                    Already have an account? <Link to="/login">Log in</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;