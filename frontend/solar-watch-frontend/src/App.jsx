import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext.jsx";
import ProtectedRoute from "./components/ProtectedRoute";
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";
import SolarWatchPage from "./pages/SolarWatchPage";

function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/registration" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route
                path="/solar-watch"
                element={
                  <ProtectedRoute>
                    <SolarWatchPage />
                  </ProtectedRoute>
                }
            />
            <Route path="/" element={<Navigate to="/solar-watch" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}

export default App;