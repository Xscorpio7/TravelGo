import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const API_URL = "http://localhost:9090/api/credenciales";
export default function Login() {
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [remember, setRemember] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

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

        <div className="w-full max-w-md bg-white dark:bg-astronaut-light rounded-xl shadow-lg p-8 md:p-10">
          <h2 className="text-2xl font-bold text-center text-[#361c34] dark:text-[#391e37] mb-8">
            Inicia sesión en Travel Go
          </h2>

          <form onSubmit={handleSubmit}>
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
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-[#2a3655] input-field focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:text-white focus:outline-none"
                placeholder="tucorreo@ejemplo.com"
                required
              />
            </div>

            <div className="mb-6">
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 dark:text-gray-600 mb-2"
              >
                Contraseña
              </label>
              <input
                type="password"
                id="password"
                value={contrasena}
                onChange={(e) => setContrasena(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600  dark:bg-[#2a3655] input-field focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:text-white focus:outline-none"
                placeholder="••••••••"
                required
              />
            </div>

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
                    <span className="hidden peer-checked:block absolute w-1.5 h-2.5 border-b-2 border-r-2 border-white transform rotate-45" />
                  )}
                </span>
                <span className="text-sm text-gray-700 dark:text-gray-500">
                  Recuérdame
                </span>
              </label>

              <a
                href="#"
                className="text-sm text-[#5b8bd6] dark:text-[#b97cb9] hover:underline"
              >
                ¿Olvidaste tu contraseña?
              </a>
            </div>

            <button
              type="submit"
              className="w-full bg-[#8a4c85] hover:bg-[#391e37] dark:bg-flamepea dark:hover:bg-flamepea-dark text-white font-bold py-3 px-4 rounded-lg transition-colors duration-300 mb-6"
              onClick={() => window.location.href = './UserProfile'}
            >
              Iniciar sesión
            </button>

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

          <button
            onClick={() => navigate("/")}
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
