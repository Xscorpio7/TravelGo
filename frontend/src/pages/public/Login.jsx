import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { bookingStorage } from "../../utils/bookingStorage";

const API_URL = "http://localhost:9090/api/auth/login";

export default function Login() {
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [remember, setRemember] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  // **NUEVO: Verificar si hay reserva pendiente**
  const [hasPendingBooking, setHasPendingBooking] = useState(false);

  useEffect(() => {
    // Verificar si hay reserva pendiente
    const pendingBooking = bookingStorage.hasPendingBooking();
    setHasPendingBooking(pendingBooking);
    
    if (pendingBooking) {
      const summary = bookingStorage.getSummary();
      console.log('📋 Reserva pendiente detectada:', summary);
    }

    // Cargar correo guardado si existe
    const correoGuardado = localStorage.getItem("correoGuardado");
    if (correoGuardado) {
      setCorreo(correoGuardado);
      setRemember(true);
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      if (!correo || !contrasena) {
        setError("Por favor completa todos los campos");
        setLoading(false);
        return;
      }

      const response = await fetch(API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          correo: correo,
          contrasena: contrasena,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.error || "Error al iniciar sesión");
        setLoading(false);
        return;
      }

      // Guardar token y datos del usuario en localStorage
      localStorage.setItem("token", data.token);
      localStorage.setItem("usuarioId", data.usuarioId);
      localStorage.setItem("correo", data.correo);
      localStorage.setItem("primerNombre", data.primerNombre);
      localStorage.setItem("primerApellido", data.primerApellido);
      localStorage.setItem("tipoUsuario", data.tipoUsuario);

      if (remember) {
        localStorage.setItem("correoGuardado", correo);
      }

      console.log("✅ Sesión iniciada correctamente");
      
      // **IMPORTANTE: Verificar si hay reserva pendiente**
      if (bookingStorage.hasPendingBooking()) {
        console.log('🎫 Continuando con la reserva pendiente...');
        // Redirigir a BookingFlow - el componente se encargará de recuperar los datos
        navigate("/booking");
      } else {
        console.log('🏠 No hay reserva pendiente, redirigiendo al home');
        // Redirigir al home
        navigate("/");
      }
      
    } catch (err) {
      console.error("Error:", err);
      setError("Error de conexión. Por favor intenta de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-screen bg-gradient-to-br from-[#391e37] to-[#b97cb9] dark:bg-cosmic-dark dark:text-light">
      <div className="container mx-auto px-4 py-8 flex flex-col items-center justify-center min-h-screen">
        {/* Logo */}  
        <div className="mb-8 text-center">
          <h1 className="text-4xl font-bold text-[#361c34] dark:text-[#f2f6fc]">
            <span className="text-[#b97cb9]">Travel</span> Go
          </h1>
        </div>

        {/* **NUEVO: Banner de reserva pendiente** */}
        {hasPendingBooking && (
          <div className="w-full max-w-md mb-4 bg-blue-50 border-2 border-blue-400 rounded-lg p-4">
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0">
                <svg className="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-blue-800">
                  🎫 Tienes una reserva en progreso
                </p>
                <p className="text-xs text-blue-600 mt-1">
                  Inicia sesión para continuar con tu reserva
                </p>
              </div>
            </div>
          </div>
        )}

        <div className="w-full max-w-md bg-white dark:bg-astronaut-light rounded-xl shadow-lg p-8 md:p-10">
          <h2 className="text-2xl font-bold text-center text-[#361c34] dark:text-[#391e37] mb-8">
            Inicia sesión en Travel Go
          </h2>

          {/* Mensaje de error */}
          {error && (
            <div className="mb-4 p-4 bg-red-100 border border-red-300 text-red-700 rounded-lg flex items-center">
              <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd"/>
              </svg>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Correo */}
            <div className="mb-6">
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 dark:text-gray-600 mb-2"
              >
                Correo electrónico
              </label>
              <input
                type="email"
                id="email"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-[#2a3655] input-field focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:text-white focus:outline-none focus:ring-2 focus:ring-[#b97cb9]"
                placeholder="tucorreo@ejemplo.com"
                required
              />
            </div>

            {/* Contraseña */}
            <div className="mb-6">
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 dark:text-gray-600 mb-2"
              >
                Contraseña
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  value={contrasena}
                  onChange={(e) => setContrasena(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-[#2a3655] input-field focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:text-white focus:outline-none focus:ring-2 focus:ring-[#b97cb9] pr-10"
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-3 text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200"
                >
                  {showPassword ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>

            {/* Recordar y Olvidé contraseña */}
            <div className="flex items-center justify-between mb-6">
              <label className="relative flex items-center cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={remember}
                  onChange={(e) => setRemember(e.target.checked)}
                  className="peer hidden"
                />
                <span className="w-5 h-5 mr-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-input-dark peer-checked:bg-[#b97cb9] peer-checked:border-[#b97cb9] flex items-center justify-center">
                  {remember && (
                    <span className="text-white text-xs">✓</span>
                  )}
                </span>
                <span className="text-sm text-gray-700 dark:text-gray-500">
                  Recuérdame
                </span>
              </label>

              <Link
                to="/recuperar-contrasena"
                className="text-sm text-[#5b8bd6] dark:text-[#b97cb9] hover:underline"
              >
                ¿Olvidaste tu contraseña?
              </Link>
            </div>

            {/* Botón de iniciar sesión */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-[#8a4c85] hover:bg-[#391e37] dark:bg-flamepea dark:hover:bg-flamepea-dark text-white font-bold py-3 px-4 rounded-lg transition-colors duration-300 mb-6 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <span className="flex items-center justify-center">
                  <svg className="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                  Iniciando sesión...
                </span>
              ) : (
                <span>
                  {hasPendingBooking ? '🎫 Iniciar sesión y continuar reserva' : 'Iniciar sesión'}
                </span>
              )}
            </button>

            {/* Link a registro */}
            <div className="text-center">
              <p className="text-gray-600 dark:text-gray-400">
                ¿No tienes cuenta?
                <Link
                  to="/register"
                  className="text-[#5b8bd6] dark:text-[#b97cb9] font-medium hover:underline ml-1 hover:bg-transparent"
                >
                  Regístrate
                </Link>
              </p>
            </div>
          </form>

          {/* Botón volver */}
          <button
            onClick={() => {
              // Si hay reserva pendiente, preguntar antes de salir
              if (hasPendingBooking) {
                const confirmExit = window.confirm(
                  '¿Estás seguro? Tu reserva en progreso se guardará por 30 minutos.'
                );
                if (!confirmExit) return;
              }
              navigate("/");
            }}
            className="w-full bg-[#ca9bcb] hover:bg-[#a35f9f] dark:bg-flamepea dark:hover:bg-flamepea-dark text-white font-bold py-3 px-4 rounded-lg transition-colors duration-300 mt-6"
          >
            Volver
          </button>
        </div>

        <p className="mt-8 text-sm text-gray-500 dark:text-gray-800 text-center">
          © 2025 Travel Go.
        </p>
      </div>
    </div>
  );
}