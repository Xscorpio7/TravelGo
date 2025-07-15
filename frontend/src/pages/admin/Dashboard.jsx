import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const mockData = [
  { month: "Ene", reservas: 12 },
  { month: "Feb", reservas: 19 },
  { month: "Mar", reservas: 22 },
  { month: "Abr", reservas: 15 },
  { month: "May", reservas: 28 },
];

export default function Dashboard() {
  const [darkMode, setDarkMode] = useState(false);
  const [seccionActiva, setSeccionActiva] = useState("inicio");

  useEffect(() => {
    const saved = localStorage.getItem("color-theme");
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    const html = document.documentElement;
    if (saved === "dark" || (!saved && prefersDark)) {
      html.classList.add("dark");
      setDarkMode(true);
    } else {
      html.classList.remove("dark");
    }
  }, []);

  const toggleDark = () => {
    const html = document.documentElement;
    if (html.classList.contains("dark")) {
      html.classList.remove("dark");
      localStorage.setItem("color-theme", "light");
      setDarkMode(false);
    } else {
      html.classList.add("dark");
      localStorage.setItem("color-theme", "dark");
      setDarkMode(true);
    }
  };
  const [usuarios, setUsuarios] = useState([]);
const [reservas, setReservas] = useState([]);
const [loading, setLoading] = useState(true);
useEffect(() => {
  const fetchData = async () => {
    try {
      const resUsuarios = await fetch("http://localhost:9090/api/usuarios");
      const usuariosData = await resUsuarios.json();

      const resReservas = await fetch("http://localhost:9090/api/reservas");
      const reservasData = await resReservas.json();

      setUsuarios(usuariosData);
      setReservas(reservasData);
    } catch (error) {
      console.error("Error al cargar datos:", error);
    } finally {
      setLoading(false);
    }
  };

  fetchData();
}, []);
  return (
    <div className="flex min-h-screen font-sans bg-gradient-to-br from-[#391e37] to-[#212b4a]">
      {/* Sidebar */}
      <aside className="w-64 bg-white dark:bg-astronaut-dark shadow-lg p-6 space-y-4">
        <div className="flex items-center justify-center h-16 px-auto bg-cosmic-light dark:bg-cosmic-dark">
          <h2 className="text-2xl font-bold text-cosmic-base">TravelGo</h2>
        </div>
        <nav className="flex flex-col space-y-3 text-gray-700 dark:text-gray-100">
          {[
            "Inicio",
            "Usuarios",
            "Reservas",
            "Viajes",
            "Transportes",
            "Pagos",
            "Alojamiento",
          ].map((item) => (
            <button
              key={item}
              onClick={() => setSeccionActiva(item.toLowerCase())}
              className={`text-left px-4 py-2 rounded-lg font-medium transition-colors duration-200 hover:bg-cosmic-light dark:hover:bg-cosmic-dark hover:text-cosmic-dark dark:hover:text-cosmic-light
                ${seccionActiva === item.toLowerCase() ? "bg-cosmic-light dark:bg-cosmic-dark" : ""}
              `}
            >
              {item}
            </button>
          ))}
          <Link to="/" className="text-red-600 hover:text-red-800">
            Cerrar sesión
          </Link>
        </nav>
      </aside>

      {/* Main Panel */}
      <div className="flex-1 p-8 space-y-8">
        {/* Header */}
        <header className="flex justify-between items-center">
          <h1 className="text-3xl font-bold text-gray-800 dark:text-white capitalize">
            {seccionActiva}
          </h1>
          <button
            onClick={toggleDark}
            className="bg-cosmic-base hover:bg-cosmic-dark text-white px-4 py-2 rounded-lg transition"
          >
            {darkMode ? "Modo Claro" : "Modo Oscuro"}
          </button>
        </header>

     {seccionActiva === "inicio" && (
  <>
    {/* Summary Cards */}
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      {[
        { label: "Usuarios", value: usuarios.length },
        { label: "Reservas", value: reservas.length },
        { label: "Ingresos (mes)", value: "$7,840" },
        { label: "Viajes activos", value: 67 },
      ].map(({ label, value }) => (
        <div
          key={label}
          className="card-hover bg-white dark:bg-gray-900 rounded-xl p-6 shadow-testimonial transition-all duration-300 hover:shadow-indigo-500/50"
        >
          <h3 className="text-sm text-gray-500 dark:text-gray-400 mb-2">
            {label}
          </h3>
          <p className="text-2xl font-semibold text-gray-800 dark:text-white">
            {value}
          </p>
        </div>
      ))}
    </div>

    {/* Chart & Table */}
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* Chart: usa mockData o genera dinámicamente desde reservas */}
      <div className="bg-white dark:bg-[#2a3655] rounded-xl shadow-md p-6">
        <h2 className="text-lg font-semibold text-gray-700 dark:text-white mb-4">
          Reservas por mes
        </h2>
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={mockData}>
            <XAxis dataKey="month" stroke="#888" />
            <YAxis stroke="#888" />
            <Tooltip />
            <Bar dataKey="reservas" fill="#b97cb9" />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Tabla de usuarios recientes */}
      <div className="bg-white dark:bg-[#2a3655] rounded-xl shadow-md p-6 overflow-auto">
        <h2 className="text-lg font-semibold text-gray-700 dark:text-white mb-4">
          Usuarios recientes
        </h2>
        <table className="w-full text-sm text-left">
          <thead className="text-gray-500 dark:text-gray-300 border-b">
            <tr>
              <th className="py-2">Nombre</th>
              <th className="py-2">Correo</th>
              <th className="py-2">Nacionalidad</th>
              <th className="py-2">Registro</th>
            </tr>
          </thead>
          <tbody className="text-gray-700 dark:text-gray-100">
            {usuarios.slice(-5).reverse().map((user) => (
              <tr
                key={user.id}
                className="border-b border-gray-100 dark:border-gray-600"
              >
                <td className="py-2">{user.nombre}</td>
                <td className="py-2">{user.email}</td>
                <td className="py-2">{user.nacionalidad || "—"}</td>
                <td className="py-2">{user.fechaRegistro || "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  </>
)}


        {/* Sección "Usuarios" */}
        {seccionActiva === "usuarios" && (
          <div className="text-white">
            <h2 className="text-xl font-semibold mb-4">Gestión de Usuarios</h2>
             <div className="bg-white dark:bg-gray-900 p-6 rounded-xl shadow">
            <h2 className="text-xl font-semibold mb-4 text-gray-900 dark:text-white">Administrar Usuarios</h2>
            <div className="mb-4 flex items-center justify-between">
              <div className="relative w-full max-w-sm">
                <input
                  type="text"
                  placeholder="Buscar usuario..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring focus:border-blue-500"
                />
                <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              </div>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50 dark:bg-gray-800">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Correo</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rol</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Registrado</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
                  </tr>
                </thead>
                <tbody className="bg-white dark:bg-gray-900 divide-y divide-gray-200">
                  {filteredUsers.map((user) => (
                    <tr key={user.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <img className="w-10 h-10 rounded-full" src={user.avatar} alt={user.name} />
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900 dark:text-white">{user.name}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-300">{user.email}</td>
                      <td className="px-6 py-4 text-sm">
                        <RoleBadge role={user.role} />
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <StatusBadge status={user.status} />
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-300">{user.joined}</td>
                      <td className="px-6 py-4 text-right space-x-2">
                        {user.role === "user" && (
                          <button className="px-2 py-1 text-xs font-medium text-white bg-blue-600 rounded hover:bg-blue-700">
                            <ShieldCheck className="inline w-4 h-4 mr-1" /> Hacer admin
                          </button>
                        )}
                        <button className="px-2 py-1 text-xs font-medium text-white bg-yellow-500 rounded hover:bg-yellow-600">
                          <Ban className="inline w-4 h-4 mr-1" />
                          {user.status === "active" ? "Bloquear" : "Desbloquear"}
                        </button>
                        <button className="px-2 py-1 text-xs font-medium text-white bg-red-600 rounded hover:bg-red-700">
                          <Trash2 className="inline w-4 h-4 mr-1" /> Eliminar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          </div>
        )}

        {/* Puedes añadir más secciones así */}
        {seccionActiva === "viajes" && (
          <div className="text-white">
            <h2 className="text-xl font-semibold mb-4">Gestión de Viajes</h2>
            <p>Aquí puedes gestionar los viajes de TravelGo.</p>
          </div>
        )}

        {seccionActiva === "alojamiento" && (
          <div className="text-white">
            <h2 className="text-xl font-semibold mb-4">Gestión de Alojamiento</h2>
            <p>Aquí puedes administrar alojamientos disponibles.</p>
          </div>
        )}
      </div>
    </div>
  );
}
