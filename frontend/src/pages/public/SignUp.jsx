import { Link, useNavigate } from "react-router-dom";
import Home from "./Home";
import Register from "./Register";
import React, {useEffect,useState} from 'react';
import axios from 'axios';



const API_URL = "http://localhost:9090/api/credenciales";
export default function SignUp() {
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [remember, setRemember] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
   addCredencial();
  };
  const [credenciales, setCredenciales] = useState([]);

    useEffect(()=>{
        axios.get(API_URL).then(res=>setCredenciales(res.data))
    }, []);
const addCredencial = () => {
    if (email && password) {
        const newCredencial = { correo, contrasena };
        axios.post(API_URL, newCredencial)
          .then(response => {
            setCredenciales([...credenciales, response.data]);
            setCorreo("");
            setContrasena("");
            console.log("Credencial agregada:", response.data);
          })
          .catch(error => {
            console.error("Error al agregar credencial:", error);
          });
      }
}

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#391e37] to-[#b97cb9] dark:bg-cosmic-dark dark:text-light font-['Roboto']">
      <div className="container mx-auto px-4 py-8 flex flex-col items-center justify-center min-h-screen">
        {/* Logo */}
        <div className="mb-8 text-center">
          <h1 className="text-4xl font-bold text-[#361c34] dark:text-[#f2f6fc]">
            <span className="text-[#b97cb9]">Travel</span> Go
          </h1>
        </div>

        <div className="w-full max-w-md bg-white dark:bg-astronaut-dark rounded-xl shadow-lg p-8 md:p-10">
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
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-input-dark focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none transition-all"
                placeholder="tucorreo@ejemplo.com"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
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
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-input-dark focus:border-[#b97cb9] dark:focus:border-cosmic placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none transition-all"
                placeholder="••••••••"
                value={contrasena}
                onChange={(e) => setContrasena(e.target.value)}
                required
              />
            </div>

            <div className="flex items-center justify-between mb-6">
              <label className="flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={remember}
                  onChange={(e) => setRemember(e.target.checked)}
                  className="hidden"
                />
                <span className="w-5 h-5 border border-gray-300 dark:border-gray-600 rounded mr-2 flex items-center justify-center bg-white dark:bg-input-dark">
                  {remember && (
                    <span className="w-2 h-3 border-white border-b-2 border-r-2 transform rotate-45 mt-[-2px]" />
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
            >
              Iniciar sesión
            </button>

            <div className="text-center">
              <p className="text-gray-600 dark:text-gray-400">
                ¿No tienes cuenta?
                <Link
                  to={Register}
                  className="text-[#5b8bd6] dark:text-[#b97cb9] font-medium hover:underline ml-1"
                >
                  Regístrate
                </Link>
              </p>
            </div>
          </form>

          <button
            onClick={() => navigate({Home})}
            className="w-full bg-[#ca9bcb] hover:bg-[#a35f9f] dark:bg-flamepea dark:hover:bg-flamepea-dark text-black font-bold py-3 px-4 rounded-lg transition-colors duration-300 mt-6"
          >
            Volver al inicio
          </button>
        </div>

        <p className="mt-8 text-sm text-gray-500 dark:text-gray-800 text-center">
          © 2025 Travel Go.
        </p>
      </div>
    </div>
  );
}
