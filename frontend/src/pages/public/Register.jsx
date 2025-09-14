import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({
    nombre_completo: "",
    telefono: "",
    nacionalidad: "",
    fecha_nacimiento: "",
    genero: "",
    correo: "",
    contrasena: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(form);

    try {
      const response = await fetch("http://localhost:9090/api/usuarios", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      const text = await response.text();

      if (!response.ok) {
        throw new Error(text);
      }
      console.log("Usuario registrado correctamente:", text);
      navigate("/login");
      
    } catch (error) {
      console.log("Error en la creación del usuario:", error.message);
      alert("Error al registrar el usuario. Por favor, inténtalo de nuevo.");
    }
  };

  useEffect(() => {
    // Dark mode toggle setup
    const prefersDark = window.matchMedia(
      "(prefers-color-scheme: dark)"
    ).matches;
    const saved = localStorage.getItem("color-theme");
    const html = document.documentElement;

    if (saved === "dark" || (!saved && prefersDark)) {
      html.classList.add("dark");
    } else {
      html.classList.remove("dark");
    }
  }, []);

  const toggleDarkMode = () => {
    const html = document.documentElement;
    const isDark = html.classList.contains("dark");
    if (isDark) {
      html.classList.remove("dark");
      localStorage.setItem("color-theme", "light");
    } else {
      html.classList.add("dark");
      localStorage.setItem("color-theme", "dark");
    }
  };

  return (
    <div className="bg-gradient-to-br from-[#391e37] to-[#b97cb9] font-sans antialiased min-h-screen flex items-center justify-center px-4 py-8">
      {/* Dark mode toggle */}
      {/*<button
        onClick={toggleDarkMode}
        className="absolute top-6 right-6 text-white dark:text-gray-300 hover:bg-white/20 dark:hover:bg-black/20 rounded-lg p-2.5"
        aria-label="Toggle dark mode"
      >
        <svg
          className="w-5 h-5 dark:hidden"
          fill="currentColor"
          viewBox="0 0 20 20"
        >
          <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z" />
        </svg>
        <svg
          className="w-5 h-5 hidden dark:inline"
          fill="currentColor"
          viewBox="0 0 20 20"
        >
          <path
            fillRule="evenodd"
            clipRule="evenodd"
            d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z"
          />
        </svg>
      </button>*/}

      {/* Registration Card */}
      <div className="container mx-auto px-4 py-8 flex flex-col items-center justify-center min-h-screen">
        <div className="mb-8 text-center">
          <h1 className="text-4xl font-bold text-[#361c34] dark:text-[#f2f6fc]">
            <span className="text-[#b97cb9]">Travel</span> Go
          </h1>
        </div>

        <div className="w-full max-w-md bg-white dark:bg-astronaut-light rounded-xl shadow-lg p-8 md:p-10 transition-colors duration-300">
          <h1 className="text-2xl font-bold text-center text-gray-800 dark:text-[#391e37] mb-8">
            Regístrate en TravelGo
          </h1>

          <form onSubmit={handleSubmit} className="space-y-5">
            {[
              { id: "primerNombre", label: "Primer Nombre", type: "text" },
              { id: "primerApellido", label: "Primer Apellido", type: "text" },
              { id: "telefono", label: "Teléfono", type: "tel" },
              
              {
                id: "fecha_nacimiento",
                label: "Fecha de nacimiento",
                type: "date",
              },
              { id: "correo", label: "Correo electrónico", type: "email" },
              { id: "contrasena", label: "Contraseña", type: "password", placeholder:"••••••••" },
            ].map(({ id, label, type }) => (
              <div key={id}>
                <label
                  htmlFor={id}
                  className="block text-sm font-medium mb-1  text-gray-700 dark:text-gray-600"
                >
                  {label}
                </label>
                <input
                  type={type}
                  id={id}
                  name={id}
                  required
                  value={form[id]}
                  onChange={handleChange}
                  className="form-input w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-cosmic-base dark:focus:ring-astronaut-base dark:bg-[#2a3655] dark:text-white"
                />
              </div>
            ))}
            {/*Nacionalidad*/}
            <div>
              <label
                htmlFor="nacionalidad"
                className="block text-sm font-medium  text-gray-700 dark:text-gray-600 mb-1"
              >
                Nacionalidad
              </label>
              <select
                id="nacionalidad"
                name="nacionalidad"
                value={form.nacionalidad}
                onChange={handleChange}
                required
                className="form-input w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-cosmic-base dark:focus:ring-astronaut-base dark:bg-[#2a3655] dark:text-white"
              >
                <option value="">Seleccione una opción</option>
                <option value="Colombia">Colombia</option>
                <option value="Mexico">México</option>
                <option value="Argentina">Argentina</option>
                <option value="Ecuador">Ecuador</option>
                <option value="Peru">Perú</option>
                <option value="Bolivia">Bolivia</option>
                <option value="Chile">Chile</option>
                <option value="Paraguay">Paraguay</option>
                <option value="Uruguay">Uruguay</option>
                <option value="Panamá">Panama</option>
                <option value="Costa_rica">Costa rica</option>
                <option value="Nicaragua">Nicaragua</option>
                <option value="Honduras">Honduras</option>
                <option value="Guatemala">Guatemala</option>
              </select>
            </div>

            {/* Genero */}
            <div>
              <label
                htmlFor="gender"
                className="block text-sm font-medium  text-gray-700 dark:text-gray-600 mb-1"
              >
                Género
              </label>
              <select
                id="genero"
                name="genero"
                value={form.genero}
                onChange={handleChange}
                required
                className="form-input w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-cosmic-base dark:focus:ring-astronaut-base dark:bg-[#2a3655] dark:text-white"
              >
                <option value="">Seleccione una opción</option>
                <option value="MALE">Masculino</option>
                <option value="FEMALE">Femenino</option>
                <option value="UNSPECIFIED">Otro</option>
              </select>
            </div>

            {/* Buttons */}
            <div className="flex flex-col space-y-3 pt-2">
              <button
                type="submit"
                className="w-full bg-[#8a4c85] text-white py-2.5 px-4 rounded-lg font-medium hover:bg-[#391e37]  shadow-md hover:shadow-lg"
              >
                Registrarse
              </button>
              <button
                type="button"
                onClick={() => navigate("/")}
                className="w-full text-center bg-gray-100 dark:bg-[#ca9bcb] text-gray-800 dark:text-white py-2.5 px-4 rounded-lg font-medium hover:bg-[#a35f9f]  dark:hover:bg-[#a35f9f]  transition-colors"
              >
                Volver al inicio
              </button>
            </div>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm  text-white dark:text-gray-400">
              ¿Ya tienes cuenta?{" "}
              <Link
                to="/login"
                className="text-cosmic-base dark:text-[#b97cb9] font-medium hover:underline"
              >
                Inicia sesión
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
