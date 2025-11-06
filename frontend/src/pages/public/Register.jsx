import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { bookingStorage } from "../../utils/bookingStorage";

const API_URL_USUARIOS = "http://localhost:9090/api/usuarios";
const API_URL_LOGIN = "http://localhost:9090/api/auth/login";

export default function Register() {
  const [form, setForm] = useState({
    primerNombre: "",
    primerApellido: "",
    telefono: "",
    nacionalidad: "",
    fecha_nacimiento: "",
    genero: "",
    correo: "",
    contrasena: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // **NUEVO: Verificar si hay reserva pendiente**
  const [hasPendingBooking, setHasPendingBooking] = useState(false);
  const [bookingSummary, setBookingSummary] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    // Verificar si hay reserva pendiente
    const pendingBooking = bookingStorage.hasPendingBooking();
    setHasPendingBooking(pendingBooking);
    
    if (pendingBooking) {
      const summary = bookingStorage.getSummary();
      setBookingSummary(summary);
      console.log('📋 Reserva pendiente detectada en registro:', summary);
    }
  }, []);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    console.log('📝 Iniciando registro:', form);

    try {
      // Validar campos requeridos
      if (!form.primerNombre || !form.primerApellido || !form.correo || !form.contrasena) {
        setError("Por favor completa todos los campos obligatorios");
        setLoading(false);
        return;
      }

      // 1. Registrar usuario
      const response = await fetch(API_URL_USUARIOS, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al registrar usuario");
      }

      const registroResult = await response.text();
      console.log("✅ Usuario registrado correctamente:", registroResult);

      // 2. Hacer login automático
      console.log('🔐 Iniciando sesión automática...');
      
      const loginResponse = await fetch(API_URL_LOGIN, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          correo: form.correo.trim(),
          contrasena: form.contrasena,
        }),
      });

      if (!loginResponse.ok) {
        // Si falla el login automático, redirigir a login manual
        console.log('⚠️ Login automático falló, redirigiendo a login manual');
        setTimeout(() => {
          navigate("/login");
        }, 1000);
        return;
      }

      const loginData = await loginResponse.json();
      console.log('✅ Login automático exitoso:', loginData);

      // 3. Guardar datos de sesión
      localStorage.setItem("token", loginData.token);
      localStorage.setItem("usuarioId", loginData.usuarioId);
      localStorage.setItem("correo", loginData.correo);
      localStorage.setItem("primerNombre", loginData.primerNombre);
      localStorage.setItem("primerApellido", loginData.primerApellido);
      localStorage.setItem("tipoUsuario", loginData.tipoUsuario);

      // 4. Redirigir según si hay reserva pendiente
      if (bookingStorage.hasPendingBooking()) {
        console.log('🎫 Continuando con reserva pendiente...');
        setTimeout(() => {
          navigate("/booking");
        }, 500);
      } else {
        console.log('🏠 No hay reserva pendiente, redirigiendo al home');
        setTimeout(() => {
          navigate("/");
        }, 500);
      }
      
    } catch (error) {
      console.error("❌ Error en el registro:", error.message);
      setError(error.message || "Error al registrar el usuario. Por favor, inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gradient-to-br from-[#391e37] to-[#b97cb9] font-sans antialiased min-h-screen flex items-center justify-center px-4 py-8">
      <div className="container mx-auto px-4 py-8 flex flex-col items-center justify-center min-h-screen">
        <div className="mb-8 text-center">
          <h1 className="text-4xl font-bold text-[#361c34] dark:text-[#f2f6fc]">
            <span className="text-[#b97cb9]">Travel</span> Go
          </h1>
        </div>

        {/* **NUEVO: Banner de reserva pendiente** */}
        {hasPendingBooking && bookingSummary && (
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
                  {bookingSummary.origin} → {bookingSummary.destination}
                </p>
              </div>
            </div>
          </div>
        )}

        <div className="w-full max-w-md bg-white dark:bg-astronaut-light rounded-xl shadow-lg p-8 md:p-10 transition-colors duration-300">
          <h1 className="text-2xl font-bold text-center text-gray-800 dark:text-[#391e37] mb-8">
            {hasPendingBooking ? 'Crea tu cuenta y continúa' : 'Regístrate en TravelGo'}
          </h1>

          {/* Mensaje de error */}
          {error && (
            <div className="mb-4 p-4 bg-red-100 border border-red-300 text-red-700 rounded-lg flex items-center">
              <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd"/>
              </svg>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            {[
              { id: "primerNombre", label: "Primer Nombre *", type: "text" },
              { id: "primerApellido", label: "Primer Apellido *", type: "text" },
              { id: "telefono", label: "Teléfono *", type: "tel" },
              {
                id: "fecha_nacimiento",
                label: "Fecha de nacimiento *",
                type: "date",
              },
              { id: "correo", label: "Correo electrónico *", type: "email" },
              { id: "contrasena", label: "Contraseña *", type: "password", placeholder: "••••••••" },
            ].map(({ id, label, type, placeholder }) => (
              <div key={id}>
                <label
                  htmlFor={id}
                  className="block text-sm font-medium mb-1  text-gray-700 dark:text-gray-600"
                >
                  {label}
                </label>
                {id === "contrasena" ? (
                  <div className="relative">
                    <input
                      type={showPassword ? "text" : "password"}
                      id={id}
                      name={id}
                      required
                      value={form[id]}
                      onChange={handleChange}
                      placeholder={placeholder}
                      className="form-input w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-cosmic-base dark:focus:ring-astronaut-base dark:bg-[#2a3655] dark:text-white pr-10"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-2 text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200"
                    >
                      {showPassword ? (
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                      ) : (
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      )}
                    </button>
                  </div>
                ) : (
                  <input
                    type={type}
                    id={id}
                    name={id}
                    required
                    value={form[id]}
                    onChange={handleChange}
                    placeholder={placeholder}
                    className="form-input w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-cosmic-base dark:focus:ring-astronaut-base dark:bg-[#2a3655] dark:text-white"
                  />
                )}
              </div>
            ))}
            {/* Nacionalidad */}
            <div>
              <label
                htmlFor="nacionalidad"
                className="block text-sm font-medium  text-gray-700 dark:text-gray-600 mb-1"
              >
                Nacionalidad *
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
                <option value="Panama">Panamá</option>
                <option value="Costa_rica">Costa Rica</option>
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
                Género *
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
                disabled={loading}
                className="w-full bg-[#8a4c85] text-white py-2.5 px-4 rounded-lg font-medium hover:bg-[#391e37] shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? (
                  <span className="flex items-center justify-center">
                    <svg className="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                    {hasPendingBooking ? 'Creando cuenta...' : 'Registrando...'}
                  </span>
                ) : (
                  <span>
                    {hasPendingBooking ? '🎫 Registrarse y continuar reserva' : 'Registrarse'}
                  </span>
                )}
              </button>
              <button
                type="button"
                onClick={() => {
                  if (hasPendingBooking) {
                    const confirmExit = window.confirm(
                      '¿Estás seguro? Tu reserva en progreso se guardará por 30 minutos.'
                    );
                    if (!confirmExit) return;
                  }
                  navigate("/");
                }}
                className="w-full text-center bg-gray-100 dark:bg-[#ca9bcb] text-gray-800 dark:text-white py-2.5 px-4 rounded-lg font-medium hover:bg-[#a35f9f] dark:hover:bg-[#a35f9f] transition-colors"
              >
                Volver al inicio
              </button>
            </div>
          </form>

          <div className="text-center mt-6">
            <p className="text-sm text-gray-600 dark:text-gray-400">
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