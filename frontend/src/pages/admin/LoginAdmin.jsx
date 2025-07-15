import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function AdminLogin() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:9090/api/admin/login", {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem("adminToken", data.token);
        navigate("/admin/dashboard");
      } else {
        setError(data.message || "Credenciales incorrectas");
      }
    } catch (err) {
      console.error(err);
      setError("Error de conexión");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#391e37] to-[#212b4a]">
      <form
        onSubmit={handleLogin}
        className="bg-white dark:bg-gray-800 p-8 rounded-lg shadow-md w-full max-w-md space-y-4"
      >
        <h2 className="text-2xl font-bold text-center text-cosmic-base dark:text-white">
          Admin Login
        </h2>

        {error && <p className="text-red-500 text-sm">{error}</p>}

        <div>
          <label className="block text-sm text-gray-700 dark:text-gray-300">Correo electrónico</label>
          <input
            type="email"
            className="w-full mt-1 p-2 border rounded-lg dark:bg-gray-700 dark:text-white"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div>
          <label className="block text-sm text-gray-700 dark:text-gray-300">Contraseña</label>
          <input
            type="password"
            className="w-full mt-1 p-2 border rounded-lg dark:bg-gray-700 dark:text-white"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button
          type="submit"
          className="w-full bg-cosmic-base hover:bg-cosmic-dark text-white py-2 rounded-lg transition"
         onClick={() => window.location.href = './dashboard'}
        >
          
          Iniciar sesión
        </button>
      </form>
    </div>
  );
}
