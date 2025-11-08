import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Users,
  Ticket,
  DollarSign,
  TrendingUp,
  Search,
  Filter,
  Shield,
  ShieldCheck,
  ShieldOff,
  Ban,
  Trash2,
  Edit2,
  UserPlus,
  X,
  CheckCircle2,
  AlertCircle,
  Eye,
  EyeOff,
  Calendar,
  Plane,
  Hotel,
  Car,
  LogOut,
  Home,
  Settings,
  BarChart3,
  Mail,
  Phone,
  MapPin,
  CreditCard,
  Clock,
  Activity,
  Download,
  RefreshCw,
  ChevronDown,
  ChevronUp,
  Info,
} from "lucide-react";

export default function AdminDashboard() {
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState("dashboard");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Datos del admin
  const [adminData, setAdminData] = useState(null);

  // Estados de datos
  const [usuarios, setUsuarios] = useState([]);
  const [reservas, setReservas] = useState([]);
  const [viajes, setViajes] = useState([]);
  const [pagos, setPagos] = useState([]);
  
  // Estadísticas detalladas
  const [stats, setStats] = useState({
    totalUsuarios: 0,
    usuariosActivos: 0,
    usuariosInactivos: 0,
    administradores: 0,
    totalReservas: 0,
    reservasConfirmadas: 0,
    reservasPendientes: 0,
    reservasCanceladas: 0,
    ingresosMes: 0,
    ingresosTotales: 0,
    viajesActivos: 0,
    pagosCompletados: 0,
    pagosPendientes: 0,
  });

  // Búsqueda y filtros - USUARIOS
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("todos");
  const [filterRole, setFilterRole] = useState("todos");
  const [filterNationality, setFilterNationality] = useState("todos");

  // Búsqueda y filtros - RESERVAS
  const [searchReserva, setSearchReserva] = useState("");
  const [filterReservaStatus, setFilterReservaStatus] = useState("todos");
  const [filterReservaDate, setFilterReservaDate] = useState("todos");

  // Modales
  const [showEditModal, setShowEditModal] = useState(false);
  const [showPromoteModal, setShowPromoteModal] = useState(false);
  const [showDemoteModal, setShowDemoteModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showCreateAdminModal, setShowCreateAdminModal] = useState(false);
  const [showUserDetailsModal, setShowUserDetailsModal] = useState(false);
  const [showReservaDetailsModal, setShowReservaDetailsModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedReserva, setSelectedReserva] = useState(null);

  // Formulario de edición completo
  const [editForm, setEditForm] = useState({
    primerNombre: "",
    segundoNombre: "",
    primerApellido: "",
    segundoApellido: "",
    telefono: "",
    nacionalidad: "",
    fechaNacimiento: "",
    genero: "",
  });

  // Formulario crear admin
  const [createAdminForm, setCreateAdminForm] = useState({
    nombre: "",
    cargo: "",
    correo: "",
    contrasena: "",
    primerNombre: "",
    primerApellido: "",
    telefono: "",
  });

  // Paginación
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  // Vista expandida de filas
  const [expandedRows, setExpandedRows] = useState(new Set());

  useEffect(() => {
    loadAdminData();
    loadDashboardData();
  }, []);

  const loadAdminData = async () => {
    try {
      const token = localStorage.getItem("token");
      const tipoUsuario = localStorage.getItem("tipoUsuario");

      if (!token || tipoUsuario !== "admin") {
        navigate("/admin/login");
        return;
      }

      const adminNombre = localStorage.getItem("primerNombre");
      const adminApellido = localStorage.getItem("primerApellido");
      setAdminData({ 
        nombre: adminNombre || "Admin",
        apellido: adminApellido || ""
      });
    } catch (err) {
      console.error("Error al cargar datos admin:", err);
      setError("Error al verificar autenticación");
    }
  };

  const loadDashboardData = async () => {
  try {
    const token = localStorage.getItem("token");
    const tipoUsuario = localStorage.getItem("tipoUsuario");
if (!token || tipoUsuario !== "admin") {
      navigate("/admin/login");
      return;
    }

    et usuariosArray = [];
    let reservasArray = [];

    // ==========================================
    // CARGAR USUARIOS con manejo de errores
    // ==========================================
    try {
      const resUsuarios = await fetch("http://localhost:9090/api/usuarios", {
        headers: { 
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        },
      });

      console.log("Status usuarios:", resUsuarios.status);

      if (resUsuarios.status === 401 || resUsuarios.status === 403) {
        console.error("Token inválido o expirado");
        localStorage.clear();
        navigate("/admin/login");
        return;
      }

      if (resUsuarios.ok) {
        const dataUsuarios = await resUsuarios.json();
        usuariosArray = Array.isArray(dataUsuarios) ? dataUsuarios : [];
        console.log("✅ Usuarios cargados:", usuariosArray.length);
      } else {
        console.error("Error al cargar usuarios:", resUsuarios.status);
        const errorData = await resUsuarios.text();
        console.error("Error data:", errorData);
      }
    } catch (errUsuarios) {
      console.error("❌ Error fetch usuarios:", errUsuarios);
      setError("Error al cargar usuarios. Verifica la conexión con el backend.");
    }
      // Cargar usuarios
      const resUsuarios = await fetch("http://localhost:9090/api/usuarios", {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (resUsuarios.ok) {
        const dataUsuarios = await resUsuarios.json();
        const usuariosArray = Array.isArray(dataUsuarios) ? dataUsuarios : [];
        setUsuarios(usuariosArray);

        // Calcular estadísticas de usuarios
        const activos = usuariosArray.filter((u) => u.credencial?.estaActivo).length;
        const admins = usuariosArray.filter((u) => u.credencial?.tipoUsuario === "admin").length;

        // Cargar reservas
        const resReservas = await fetch("http://localhost:9090/api/reservas", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (resReservas.ok) {
          const dataReservas = await resReservas.json();
          const reservasArray = Array.isArray(dataReservas) ? dataReservas : [];
          setReservas(reservasArray);

          // Calcular estadísticas de reservas
          const confirmadas = reservasArray.filter((r) => r.estado === "confirmada").length;
          const pendientes = reservasArray.filter((r) => r.estado === "pendiente").length;
          const canceladas = reservasArray.filter((r) => r.estado === "cancelada").length;

          setStats({
            totalUsuarios: usuariosArray.length,
            usuariosActivos: activos,
            usuariosInactivos: usuariosArray.length - activos,
            administradores: admins,
            totalReservas: reservasArray.length,
            reservasConfirmadas: confirmadas,
            reservasPendientes: pendientes,
            reservasCanceladas: canceladas,
            ingresosMes: 15420.50, // Esto debe venir del backend
            ingresosTotales: 148250.75,
            viajesActivos: 24,
            pagosCompletados: confirmadas,
            pagosPendientes: pendientes,
          });
        }
      }
    } catch (err) {
      console.error("Error al cargar datos:", err);
      setError("Error al cargar información del dashboard");
    } finally {
      setLoading(false);
    }
  };

  // Filtrar usuarios con búsqueda avanzada
  const filteredUsers = usuarios.filter((user) => {
    const searchLower = searchTerm.toLowerCase();
    const matchesSearch =
      user.primerNombre?.toLowerCase().includes(searchLower) ||
      user.segundoNombre?.toLowerCase().includes(searchLower) ||
      user.primerApellido?.toLowerCase().includes(searchLower) ||
      user.segundoApellido?.toLowerCase().includes(searchLower) ||
      user.credencial?.correo?.toLowerCase().includes(searchLower) ||
      user.telefono?.includes(searchTerm) ||
      user.id?.toString().includes(searchTerm);

    const matchesStatus =
      filterStatus === "todos" ||
      (filterStatus === "activos" && user.credencial?.estaActivo) ||
      (filterStatus === "inactivos" && !user.credencial?.estaActivo);

    const matchesRole =
      filterRole === "todos" ||
      (filterRole === "usuarios" && user.credencial?.tipoUsuario === "usuario") ||
      (filterRole === "admins" && user.credencial?.tipoUsuario === "admin");

    const matchesNationality =
      filterNationality === "todos" ||
      user.nacionalidad === filterNationality;

    return matchesSearch && matchesStatus && matchesRole && matchesNationality;
  });

  // Filtrar reservas
  const filteredReservas = reservas.filter((reserva) => {
    const usuario = usuarios.find((u) => u.id === reserva.usuarioId);
    const searchLower = searchReserva.toLowerCase();
    
    const matchesSearch =
      reserva.id?.toString().includes(searchReserva) ||
      usuario?.primerNombre?.toLowerCase().includes(searchLower) ||
      usuario?.primerApellido?.toLowerCase().includes(searchLower) ||
      usuario?.credencial?.correo?.toLowerCase().includes(searchLower);

    const matchesStatus =
      filterReservaStatus === "todos" ||
      reserva.estado === filterReservaStatus;

    const matchesDate =
      filterReservaDate === "todos" ||
      (filterReservaDate === "hoy" && isToday(reserva.fechaReserva)) ||
      (filterReservaDate === "semana" && isThisWeek(reserva.fechaReserva)) ||
      (filterReservaDate === "mes" && isThisMonth(reserva.fechaReserva));

    return matchesSearch && matchesStatus && matchesDate;
  });

  // Paginación
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentUsers = filteredUsers.slice(indexOfFirstItem, indexOfLastItem);
  const currentReservas = filteredReservas.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);

  // Funciones de fecha
  const isToday = (date) => {
    const today = new Date();
    const reservaDate = new Date(date);
    return reservaDate.toDateString() === today.toDateString();
  };

  const isThisWeek = (date) => {
    const today = new Date();
    const reservaDate = new Date(date);
    const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
    return reservaDate >= weekAgo && reservaDate <= today;
  };

  const isThisMonth = (date) => {
    const today = new Date();
    const reservaDate = new Date(date);
    return reservaDate.getMonth() === today.getMonth() && 
           reservaDate.getFullYear() === today.getFullYear();
  };

  // Activar/Desactivar usuario
  const toggleUserStatus = async (userId, currentStatus) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:9090/api/usuarios/${userId}/toggle-status`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        setSuccess(`✅ Usuario ${currentStatus ? "desactivado" : "activado"} correctamente`);
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        throw new Error("Error al cambiar estado");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("❌ Error al cambiar estado del usuario");
    }
  };

  // Promover usuario a admin
  const promoteToAdmin = async (userId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:9090/api/usuarios/${userId}/promote-to-admin`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        setSuccess("✅ Usuario promovido a administrador correctamente");
        setShowPromoteModal(false);
        setSelectedUser(null);
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        throw new Error("Error al promover usuario");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("❌ Error al promover usuario a administrador");
    }
  };

  // Degradar admin a usuario
  const demoteToUser = async (userId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:9090/api/usuarios/${userId}/demote-to-user`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        setSuccess("✅ Administrador degradado a usuario correctamente");
        setShowDemoteModal(false);
        setSelectedUser(null);
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        throw new Error("Error al degradar administrador");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("❌ Error al degradar administrador a usuario");
    }
  };

  // Editar usuario
  const handleEditUser = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:9090/api/usuarios/${selectedUser.id}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...selectedUser,
          ...editForm,
        }),
      });

      if (response.ok) {
        setSuccess("✅ Usuario actualizado correctamente");
        setShowEditModal(false);
        setSelectedUser(null);
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        throw new Error("Error al actualizar usuario");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("❌ Error al actualizar usuario");
    }
  };

  // Eliminar usuario
  const handleDeleteUser = async (userId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:9090/api/usuarios/${userId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        setSuccess("✅ Usuario eliminado correctamente");
        setShowDeleteModal(false);
        setSelectedUser(null);
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        throw new Error("Error al eliminar usuario");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("❌ Error al eliminar usuario");
    }
  };

  // Crear administrador
  const handleCreateAdmin = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("token");
      
     const response = await fetch("http://localhost:9090/api/administrador/register-admin", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(createAdminForm),
      });

      if (response.ok) {
        setSuccess("✅ Administrador creado correctamente");
        setShowCreateAdminModal(false);
        setCreateAdminForm({ 
          nombre: "", 
          cargo: "", 
          correo: "", 
          contrasena: "",
          primerNombre: "",
          primerApellido: "",
          telefono: "",
        });
        loadDashboardData();
        setTimeout(() => setSuccess(""), 3000);
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || "Error al crear administrador");
      }
    } catch (err) {
      console.error("Error:", err);
      setError(`❌ ${err.message}`);
    }
  };

  // Exportar datos
  const exportToCSV = (data, filename) => {
    const csv = data.map(row => Object.values(row).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${filename}_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  const handleLogout = () => {
    if (window.confirm("¿Estás seguro que deseas cerrar sesión?")) {
      localStorage.clear();
      navigate("/admin/login");
    }
  };

  // Toggle expandir fila
  const toggleExpandRow = (id) => {
    const newExpanded = new Set(expandedRows);
    if (newExpanded.has(id)) {
      newExpanded.delete(id);
    } else {
      newExpanded.add(id);
    }
    setExpandedRows(newExpanded);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-dark to-cosmic-dark flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-white mx-auto mb-4"></div>
          <p className="text-white text-lg">Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 bg-gradient-to-b from-astronaut-dark to-cosmic-dark text-white shadow-2xl fixed h-full overflow-y-auto">
        <div className="p-6">
          {/* Logo y título */}
          <div className="flex items-center space-x-3 mb-8 pb-6 border-b border-gray-700">
            <div className="w-12 h-12 bg-gradient-to-br from-cosmic-base to-flame-base rounded-full flex items-center justify-center shadow-lg">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-xl font-bold">TravelGo</h2>
              <p className="text-sm text-gray-300">Admin Panel</p>
            </div>
          </div>

          {/* Navegación */}
          <nav className="space-y-2">
            {[
              { id: "dashboard", label: "Dashboard", icon: BarChart3 },
              { id: "usuarios", label: "Usuarios", icon: Users, badge: stats.totalUsuarios },
              { id: "reservas", label: "Reservas", icon: Ticket, badge: stats.totalReservas },
              { id: "estadisticas", label: "Estadísticas", icon: Activity },
              { id: "configuracion", label: "Configuración", icon: Settings },
            ].map(({ id, label, icon: Icon, badge }) => (
              <button
                key={id}
                onClick={() => setActiveSection(id)}
                className={`w-full flex items-center justify-between px-4 py-3 rounded-lg transition-all duration-200 ${
                  activeSection === id
                    ? "bg-cosmic-base text-white shadow-lg"
                    : "text-gray-300 hover:bg-astronaut-base hover:text-white"
                }`}
              >
                <div className="flex items-center space-x-3">
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{label}</span>
                </div>
                {badge !== undefined && (
                  <span className="px-2 py-1 bg-flame-base text-white text-xs rounded-full font-bold">
                    {badge}
                  </span>
                )}
              </button>
            ))}
          </nav>

          {/* Info del admin */}
          <div className="mt-8 pt-8 border-t border-gray-700">
            <div className="flex items-center space-x-3 px-4 py-2 mb-4 bg-astronaut-base rounded-lg">
              <div className="w-10 h-10 bg-gradient-to-br from-flame-base to-cosmic-base rounded-full flex items-center justify-center text-white font-bold shadow-md">
                {adminData?.nombre?.charAt(0) || "A"}
              </div>
              <div className="flex-1 overflow-hidden">
                <p className="text-sm font-medium truncate">
                  {adminData?.nombre} {adminData?.apellido}
                </p>
                <p className="text-xs text-gray-400">Administrador</p>
              </div>
            </div>

            <button
              onClick={() => navigate("/")}
              className="w-full flex items-center space-x-2 px-4 py-2 text-gray-300 hover:text-white hover:bg-astronaut-base rounded-lg transition-colors mb-2"
            >
              <Home className="w-5 h-5" />
              <span>Ir al sitio</span>
            </button>

            <button
              onClick={handleLogout}
              className="w-full flex items-center space-x-2 px-4 py-2 text-red-400 hover:text-red-300 hover:bg-red-900 hover:bg-opacity-20 rounded-lg transition-colors"
            >
              <LogOut className="w-5 h-5" />
              <span>Cerrar sesión</span>
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 ml-64">
        {/* Header */}
        <header className="bg-white shadow-sm sticky top-0 z-10 border-b border-gray-200">
          <div className="px-8 py-4 flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-astronaut-dark capitalize flex items-center gap-2">
                {activeSection === "dashboard" && <BarChart3 className="w-7 h-7 text-cosmic-base" />}
                {activeSection === "usuarios" && <Users className="w-7 h-7 text-cosmic-base" />}
                {activeSection === "reservas" && <Ticket className="w-7 h-7 text-cosmic-base" />}
                {activeSection === "estadisticas" && <Activity className="w-7 h-7 text-cosmic-base" />}
                {activeSection === "configuracion" && <Settings className="w-7 h-7 text-cosmic-base" />}
                {activeSection}
              </h1>
              <p className="text-sm text-gray-600 mt-1">
                Bienvenido, {adminData?.nombre || "Admin"} • {new Date().toLocaleDateString('es-ES', { 
                  weekday: 'long', 
                  year: 'numeric', 
                  month: 'long', 
                  day: 'numeric' 
                })}
              </p>
            </div>

            <button
              onClick={loadDashboardData}
              className="flex items-center gap-2 px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors"
              title="Actualizar datos"
            >
              <RefreshCw className="w-5 h-5" />
              <span className="hidden md:inline">Actualizar</span>
            </button>
          </div>
        </header>

        {/* Content */}
        <main className="p-8">
          {/* Mensajes */}
          {error && (
            <div className="mb-6 flex items-center gap-2 p-4 bg-red-100 border border-red-300 text-red-700 rounded-lg animate-fade-in shadow-md">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <span className="flex-1">{error}</span>
              <button onClick={() => setError("")} className="hover:bg-red-200 p-1 rounded">
                <X className="w-4 h-4" />
              </button>
            </div>
          )}

          {success && (
            <div className="mb-6 flex items-center gap-2 p-4 bg-green-100 border border-green-300 text-green-700 rounded-lg animate-fade-in shadow-md">
              <CheckCircle2 className="w-5 h-5 flex-shrink-0" />
              <span className="flex-1">{success}</span>
              <button onClick={() => setSuccess("")} className="hover:bg-green-200 p-1 rounded">
                <X className="w-4 h-4" />
              </button>
            </div>
          )}

          {/* DASHBOARD */}
          {activeSection === "dashboard" && (
            <DashboardSection 
              stats={stats} 
              usuarios={usuarios.slice(0, 5)} 
              reservas={reservas.slice(0, 5)}
              onViewAllUsers={() => setActiveSection("usuarios")}
              onViewAllReservas={() => setActiveSection("reservas")}
            />
          )}

          {/* USUARIOS */}
          {activeSection === "usuarios" && (
            <UsersSection
              usuarios={currentUsers}
              totalUsers={filteredUsers.length}
              searchTerm={searchTerm}
              setSearchTerm={setSearchTerm}
              filterStatus={filterStatus}
              setFilterStatus={setFilterStatus}
              filterRole={filterRole}
              setFilterRole={setFilterRole}
              filterNationality={filterNationality}
              setFilterNationality={setFilterNationality}
              currentPage={currentPage}
              totalPages={totalPages}
              setCurrentPage={setCurrentPage}
              expandedRows={expandedRows}
              toggleExpandRow={toggleExpandRow}
              onEdit={(user) => {
                setSelectedUser(user);
                setEditForm({
                  primerNombre: user.primerNombre || "",
                  segundoNombre: user.segundoNombre || "",
                  primerApellido: user.primerApellido || "",
                  segundoApellido: user.segundoApellido || "",
                  telefono: user.telefono || "",
                  nacionalidad: user.nacionalidad || "",
                  fechaNacimiento: user.fechaNacimiento || "",
                  genero: user.genero || "",
                });
                setShowEditModal(true);
              }}
              onViewDetails={(user) => {
                setSelectedUser(user);
                setShowUserDetailsModal(true);
              }}
              onPromote={(user) => {
                setSelectedUser(user);
                setShowPromoteModal(true);
              }}
              onDemote={(user) => {
                setSelectedUser(user);
                setShowDemoteModal(true);
              }}
              onDelete={(user) => {
                setSelectedUser(user);
                setShowDeleteModal(true);
              }}
              onToggleStatus={toggleUserStatus}
              onCreateAdmin={() => setShowCreateAdminModal(true)}
              onExport={() => exportToCSV(
                filteredUsers.map(u => ({
                  ID: u.id,
                  Nombre: `${u.primerNombre} ${u.primerApellido}`,
                  Email: u.credencial?.correo,
                  Telefono: u.telefono,
                  Nacionalidad: u.nacionalidad,
                  Rol: u.credencial?.tipoUsuario,
                  Estado: u.credencial?.estaActivo ? 'Activo' : 'Inactivo'
                })),
                'usuarios'
              )}
            />
          )}

          {/* RESERVAS */}
          {activeSection === "reservas" && (
            <ReservasSection
              reservas={currentReservas}
              totalReservas={filteredReservas.length}
              usuarios={usuarios}
              searchReserva={searchReserva}
              setSearchReserva={setSearchReserva}
              filterReservaStatus={filterReservaStatus}
              setFilterReservaStatus={setFilterReservaStatus}
              filterReservaDate={filterReservaDate}
              setFilterReservaDate={setFilterReservaDate}
              currentPage={currentPage}
              totalPages={Math.ceil(filteredReservas.length / itemsPerPage)}
              setCurrentPage={setCurrentPage}
              onViewDetails={(reserva) => {
                setSelectedReserva(reserva);
                setShowReservaDetailsModal(true);
              }}
              onExport={() => exportToCSV(
                filteredReservas.map(r => {
                  const usuario = usuarios.find(u => u.id === r.usuarioId);
                  return {
                    ID: r.id,
                    Usuario: `${usuario?.primerNombre} ${usuario?.primerApellido}`,
                    Email: usuario?.credencial?.correo,
                    Fecha: new Date(r.fechaReserva).toLocaleDateString(),
                    Estado: r.estado,
                    Servicios: `${r.viajeId ? 'Vuelo ' : ''}${r.alojamientoId ? 'Hotel ' : ''}${r.transporteId ? 'Transporte' : ''}`
                  };
                }),
                'reservas'
              )}
            />
          )}

          {/* ESTADÍSTICAS */}
          {activeSection === "estadisticas" && (
            <EstadisticasSection stats={stats} reservas={reservas} usuarios={usuarios} />
          )}
        </main>
      </div>

      {/* Modales */}
      {showEditModal && (
        <EditUserModal
          user={selectedUser}
          editForm={editForm}
          setEditForm={setEditForm}
          onSave={handleEditUser}
          onClose={() => {
            setShowEditModal(false);
            setSelectedUser(null);
          }}
        />
      )}

      {showPromoteModal && (
        <PromoteModal
          user={selectedUser}
          onConfirm={() => promoteToAdmin(selectedUser.id)}
          onClose={() => {
            setShowPromoteModal(false);
            setSelectedUser(null);
          }}
        />
      )}

      {showDemoteModal && (
        <DemoteModal
          user={selectedUser}
          onConfirm={() => demoteToUser(selectedUser.id)}
          onClose={() => {
            setShowDemoteModal(false);
            setSelectedUser(null);
          }}
        />
      )}

      {showDeleteModal && (
        <DeleteModal
          user={selectedUser}
          onConfirm={() => handleDeleteUser(selectedUser.id)}
          onClose={() => {
            setShowDeleteModal(false);
            setSelectedUser(null);
          }}
        />
      )}

      {showCreateAdminModal && (
        <CreateAdminModal
          form={createAdminForm}
          setForm={setCreateAdminForm}
          onSubmit={handleCreateAdmin}
          onClose={() => {
            setShowCreateAdminModal(false);
            setCreateAdminForm({ 
              nombre: "", 
              cargo: "", 
              correo: "", 
              contrasena: "",
              primerNombre: "",
              primerApellido: "",
              telefono: "",
            });
          }}
        />
      )}

      {showUserDetailsModal && (
        <UserDetailsModal
          user={selectedUser}
          reservas={reservas.filter(r => r.usuarioId === selectedUser?.id)}
          onClose={() => {
            setShowUserDetailsModal(false);
            setSelectedUser(null);
          }}
        />
      )}

      {showReservaDetailsModal && (
        <ReservaDetailsModal
          reserva={selectedReserva}
          usuario={usuarios.find(u => u.id === selectedReserva?.usuarioId)}
          onClose={() => {
            setShowReservaDetailsModal(false);
            setSelectedReserva(null);
          }}
        />
      )}
    </div>
  );
}

// ==================== COMPONENTES AUXILIARES ====================

// Stats Card
function StatCard({ title, value, icon: Icon, color, trend, subtitle }) {
  const colorClasses = {
    cosmic: "from-cosmic-base to-cosmic-dark",
    astronaut: "from-astronaut-base to-astronaut-dark",
    green: "from-green-500 to-green-600",
    yellow: "from-yellow-500 to-yellow-600",
    red: "from-red-500 to-red-600",
    flame: "from-flame-base to-flame-dark",
    purple: "from-purple-500 to-purple-600",
    blue: "from-blue-500 to-blue-600",
  };

  return (
    <div className={`bg-gradient-to-br ${colorClasses[color]} rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition-transform duration-200`}>
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-sm font-medium opacity-90 mb-1">{title}</h3>
          {subtitle && <p className="text-xs opacity-75">{subtitle}</p>}
        </div>
        <div className="w-12 h-12 bg-white bg-opacity-20 rounded-lg flex items-center justify-center">
          <Icon className="w-7 h-7" />
        </div>
      </div>
      <div className="flex items-end justify-between">
        <p className="text-3xl font-bold">{value}</p>
        {trend && (
          <div className="flex items-center gap-1 text-sm">
            <TrendingUp className="w-4 h-4" />
            <span>{trend}</span>
          </div>
        )}
      </div>
    </div>
  );
}

// Role Badge
function RoleBadge({ role }) {
  return (
    <span
      className={`px-3 py-1 rounded-full text-xs font-semibold inline-flex items-center gap-1 ${
        role === "admin"
          ? "bg-purple-100 text-purple-800"
          : "bg-blue-100 text-blue-800"
      }`}
    >
      {role === "admin" ? (
        <>
          <ShieldCheck className="w-3 h-3" />
          Admin
        </>
      ) : (
        <>
          <Users className="w-3 h-3" />
          Usuario
        </>
      )}
    </span>
  );
}

// Status Badge
function StatusBadge({ status }) {
  return (
    <span
      className={`px-3 py-1 rounded-full text-xs font-semibold inline-flex items-center gap-1 ${
        status
          ? "bg-green-100 text-green-800"
          : "bg-red-100 text-red-800"
      }`}
    >
      {status ? (
        <>
          <CheckCircle2 className="w-3 h-3" />
          Activo
        </>
      ) : (
        <>
          <Ban className="w-3 h-3" />
          Inactivo
        </>
      )}
    </span>
  );
}

// ==================== CONTINUACIÓN: COMPONENTES DE SECCIONES ====================

// Dashboard Section
function DashboardSection({ stats, usuarios, reservas, onViewAllUsers, onViewAllReservas }) {
  return (
    <div className="space-y-6">
      {/* Stats Grid - 8 cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Usuarios"
          value={stats.totalUsuarios}
          subtitle={`${stats.usuariosActivos} activos`}
          icon={Users}
          color="cosmic"
          trend="+12%"
        />
        <StatCard
          title="Total Reservas"
          value={stats.totalReservas}
          subtitle={`${stats.reservasConfirmadas} confirmadas`}
          icon={Ticket}
          color="astronaut"
          trend="+8%"
        />
        <StatCard
          title="Ingresos del Mes"
          value={`$${stats.ingresosMes.toLocaleString()}`}
          subtitle="USD"
          icon={DollarSign}
          color="green"
          trend="+15%"
        />
        <StatCard
          title="Reservas Pendientes"
          value={stats.reservasPendientes}
          subtitle="Requieren atención"
          icon={Clock}
          color="yellow"
        />
      </div>

      {/* Segunda fila de stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Administradores"
          value={stats.administradores}
          subtitle="Usuarios con permisos"
          icon={ShieldCheck}
          color="purple"
        />
        <StatCard
          title="Usuarios Inactivos"
          value={stats.usuariosInactivos}
          subtitle="Cuentas suspendidas"
          icon={Ban}
          color="red"
        />
        <StatCard
          title="Ingresos Totales"
          value={`$${stats.ingresosTotales.toLocaleString()}`}
          subtitle="Histórico"
          icon={TrendingUp}
          color="flame"
        />
        <StatCard
          title="Viajes Activos"
          value={stats.viajesActivos}
          subtitle="Disponibles"
          icon={Plane}
          color="blue"
        />
      </div>

      {/* Usuarios y Reservas Recientes */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Usuarios Recientes */}
        <div className="bg-white rounded-xl shadow-md p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
              <Users className="w-5 h-5 text-cosmic-base" />
              Usuarios Recientes
            </h3>
            <button
              onClick={onViewAllUsers}
              className="text-sm text-cosmic-base hover:text-cosmic-dark font-medium"
            >
              Ver todos →
            </button>
          </div>
          <div className="space-y-3">
            {usuarios.map((user) => (
              <div key={user.id} className="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div className="w-10 h-10 bg-gradient-to-br from-cosmic-light to-astronaut-light rounded-full flex items-center justify-center text-cosmic-dark font-semibold">
                  {user.primerNombre?.charAt(0)}{user.primerApellido?.charAt(0)}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {user.primerNombre} {user.primerApellido}
                  </p>
                  <p className="text-xs text-gray-500 truncate">{user.credencial?.correo}</p>
                </div>
                <div className="flex flex-col items-end gap-1">
                  <RoleBadge role={user.credencial?.tipoUsuario} />
                  <StatusBadge status={user.credencial?.estaActivo} />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Reservas Recientes */}
        <div className="bg-white rounded-xl shadow-md p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
              <Ticket className="w-5 h-5 text-cosmic-base" />
              Reservas Recientes
            </h3>
            <button
              onClick={onViewAllReservas}
              className="text-sm text-cosmic-base hover:text-cosmic-dark font-medium"
            >
              Ver todas →
            </button>
          </div>
          <div className="space-y-3">
            {reservas.map((reserva) => (
              <div key={reserva.id} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gradient-to-br from-flame-light to-cosmic-light rounded-full flex items-center justify-center">
                    <Ticket className="w-5 h-5 text-flame-dark" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">
                      Reserva #{reserva.id}
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(reserva.fechaReserva).toLocaleDateString('es-ES')}
                    </p>
                  </div>
                </div>
                <div className="flex flex-col items-end gap-1">
                  <span
                    className={`px-2 py-1 rounded-full text-xs font-semibold ${
                      reserva.estado === "confirmada"
                        ? "bg-green-100 text-green-800"
                        : reserva.estado === "pendiente"
                        ? "bg-yellow-100 text-yellow-800"
                        : "bg-red-100 text-red-800"
                    }`}
                  >
                    {reserva.estado}
                  </span>
                  <div className="flex gap-1">
                    {reserva.viajeId && <span className="p-1 bg-blue-100 text-blue-600 rounded" title="Vuelo"><Plane className="w-3 h-3" /></span>}
                    {reserva.alojamientoId && <span className="p-1 bg-green-100 text-green-600 rounded" title="Hotel"><Hotel className="w-3 h-3" /></span>}
                    {reserva.transporteId && <span className="p-1 bg-purple-100 text-purple-600 rounded" title="Transporte"><Car className="w-3 h-3" /></span>}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// Users Section
function UsersSection({
  usuarios,
  totalUsers,
  searchTerm,
  setSearchTerm,
  filterStatus,
  setFilterStatus,
  filterRole,
  setFilterRole,
  filterNationality,
  setFilterNationality,
  currentPage,
  totalPages,
  setCurrentPage,
  expandedRows,
  toggleExpandRow,
  onEdit,
  onViewDetails,
  onPromote,
  onDemote,
  onDelete,
  onToggleStatus,
  onCreateAdmin,
  onExport,
}) {
  // Obtener nacionalidades únicas
  const uniqueNationalities = [...new Set(usuarios.map(u => u.nacionalidad).filter(Boolean))];

  return (
    <div className="space-y-6">
      {/* Header con acciones */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
          <div>
            <h2 className="text-xl font-semibold text-gray-800 flex items-center gap-2">
              <Users className="w-6 h-6 text-cosmic-base" />
              Gestión de Usuarios
            </h2>
            <p className="text-sm text-gray-600 mt-1">
              Mostrando {usuarios.length} de {totalUsers} usuarios
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <button
              onClick={onExport}
              className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
            >
              <Download className="w-5 h-5" />
              Exportar CSV
            </button>
            <button
              onClick={onCreateAdmin}
              className="flex items-center gap-2 px-4 py-2 bg-flame-base text-white rounded-lg hover:bg-flame-dark transition-colors"
            >
              <UserPlus className="w-5 h-5" />
              Crear Administrador
            </button>
          </div>
        </div>

        {/* Búsqueda y Filtros Avanzados */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Búsqueda */}
          <div className="relative">
            <Search className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, email, ID, teléfono..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
            />
          </div>

          {/* Filtro Estado */}
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
          >
            <option value="todos">📊 Todos los estados</option>
            <option value="activos">✅ Activos</option>
            <option value="inactivos">❌ Inactivos</option>
          </select>

          {/* Filtro Rol */}
          <select
            value={filterRole}
            onChange={(e) => setFilterRole(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
          >
            <option value="todos">👥 Todos los roles</option>
            <option value="usuarios">👤 Usuarios</option>
            <option value="admins">👑 Administradores</option>
          </select>

          {/* Filtro Nacionalidad */}
          <select
            value={filterNationality}
            onChange={(e) => setFilterNationality(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
          >
            <option value="todos">🌎 Todas las nacionalidades</option>
            {uniqueNationalities.map(nat => (
              <option key={nat} value={nat}>{nat}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Tabla de Usuarios */}
      <div className="bg-white rounded-xl shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gradient-to-r from-cosmic-base to-astronaut-base text-white">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Usuario</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Contacto</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Ubicación</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Rol</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Estado</th>
                <th className="px-6 py-4 text-right text-xs font-semibold uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {usuarios.map((user) => (
                <React.Fragment key={user.id}>
                  <tr className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center gap-3">
                        <button
                          onClick={() => toggleExpandRow(user.id)}
                          className="text-gray-400 hover:text-cosmic-base transition-colors"
                        >
                          {expandedRows.has(user.id) ? (
                            <ChevronUp className="w-5 h-5" />
                          ) : (
                            <ChevronDown className="w-5 h-5" />
                          )}
                        </button>
                        <div className="w-12 h-12 bg-gradient-to-br from-cosmic-base to-astronaut-base rounded-full flex items-center justify-center text-white font-bold shadow-md">
                          {user.primerNombre?.charAt(0)}{user.primerApellido?.charAt(0)}
                        </div>
                        <div>
                          <p className="text-sm font-semibold text-gray-900">
                            {user.primerNombre} {user.segundoNombre} {user.primerApellido} {user.segundoApellido}
                          </p>
                          <p className="text-xs text-gray-500">ID: #{user.id}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex flex-col gap-1">
                        <div className="flex items-center gap-2 text-sm text-gray-700">
                          <Mail className="w-4 h-4 text-cosmic-base" />
                          <span className="truncate max-w-[200px]" title={user.credencial?.correo}>
                            {user.credencial?.correo}
                          </span>
                        </div>
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <Phone className="w-4 h-4 text-cosmic-base" />
                          {user.telefono || "N/A"}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 text-sm text-gray-700">
                        <MapPin className="w-4 h-4 text-cosmic-base" />
                        {user.nacionalidad || "N/A"}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <RoleBadge role={user.credencial?.tipoUsuario} />
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge status={user.credencial?.estaActivo} />
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => onViewDetails(user)}
                          className="p-2 text-cosmic-base hover:bg-cosmic-light rounded-lg transition-colors"
                          title="Ver detalles"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => onEdit(user)}
                          className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Editar"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        {user.credencial?.tipoUsuario === "usuario" ? (
                          <button
                            onClick={() => onPromote(user)}
                            className="p-2 text-purple-600 hover:bg-purple-50 rounded-lg transition-colors"
                            title="Promover a Admin"
                          >
                            <ShieldCheck className="w-4 h-4" />
                          </button>
                        ) : (
                          <button
                            onClick={() => onDemote(user)}
                            className="p-2 text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
                            title="Degradar a Usuario"
                          >
                            <ShieldOff className="w-4 h-4" />
                          </button>
                        )}
                        <button
                          onClick={() => onToggleStatus(user.id, user.credencial?.estaActivo)}
                          className={`p-2 rounded-lg transition-colors ${
                            user.credencial?.estaActivo
                              ? "text-red-600 hover:bg-red-50"
                              : "text-green-600 hover:bg-green-50"
                          }`}
                          title={user.credencial?.estaActivo ? "Desactivar" : "Activar"}
                        >
                          <Ban className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => onDelete(user)}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Eliminar"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                  {/* Fila expandida con más detalles */}
                  {expandedRows.has(user.id) && (
                    <tr className="bg-gray-50">
                      <td colSpan="6" className="px-6 py-4">
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                          <div>
                            <p className="text-gray-500 font-medium mb-1">Género</p>
                            <p className="text-gray-900">
                              {user.genero === 'MALE' ? '👨 Masculino' : user.genero === 'FEMALE' ? '👩 Femenino' : '🧑 Otro'}
                            </p>
                          </div>
                          <div>
                            <p className="text-gray-500 font-medium mb-1">Fecha Nacimiento</p>
                            <p className="text-gray-900">{user.fechaNacimiento || 'N/A'}</p>
                          </div>
                          <div>
                            <p className="text-gray-500 font-medium mb-1">Edad</p>
                            <p className="text-gray-900">
                              {user.fechaNacimiento 
                                ? new Date().getFullYear() - new Date(user.fechaNacimiento).getFullYear() + ' años'
                                : 'N/A'}
                            </p>
                          </div>
                          <div>
                            <p className="text-gray-500 font-medium mb-1">Miembro desde</p>
                            <p className="text-gray-900">2025</p>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>

        {usuarios.length === 0 && (
          <div className="text-center py-16">
            <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600 text-lg">No se encontraron usuarios</p>
            <p className="text-gray-500 text-sm">Intenta ajustar los filtros de búsqueda</p>
          </div>
        )}

        {/* Paginación */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-gray-200 bg-gray-50">
            <p className="text-sm text-gray-600">
              Página {currentPage} de {totalPages}
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                disabled={currentPage === 1}
                className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Anterior
              </button>
              <button
                onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                disabled={currentPage === totalPages}
                className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Siguiente
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// Reservas Section
function ReservasSection({
  reservas,
  totalReservas,
  usuarios,
  searchReserva,
  setSearchReserva,
  filterReservaStatus,
  setFilterReservaStatus,
  filterReservaDate,
  setFilterReservaDate,
  currentPage,
  totalPages,
  setCurrentPage,
  onViewDetails,
  onExport,
}) {
  return (
    <div className="space-y-6">
      {/* Header con acciones */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
          <div>
            <h2 className="text-xl font-semibold text-gray-800 flex items-center gap-2">
              <Ticket className="w-6 h-6 text-cosmic-base" />
              Gestión de Reservas
            </h2>
            <p className="text-sm text-gray-600 mt-1">
              Mostrando {reservas.length} de {totalReservas} reservas
            </p>
          </div>
          <button
            onClick={onExport}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          >
            <Download className="w-5 h-5" />
            Exportar CSV
          </button>
        </div>

        {/* Búsqueda y Filtros */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por ID, usuario, email..."
              value={searchReserva}
              onChange={(e) => setSearchReserva(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
            />
          </div>

          <select
            value={filterReservaStatus}
            onChange={(e) => setFilterReservaStatus(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
          >
            <option value="todos">📊 Todos los estados</option>
            <option value="confirmada">✅ Confirmadas</option>
            <option value="pendiente">⏳ Pendientes</option>
            <option value="cancelada">❌ Canceladas</option>
          </select>

          <select
            value={filterReservaDate}
            onChange={(e) => setFilterReservaDate(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
          >
            <option value="todos">📅 Todas las fechas</option>
            <option value="hoy">Hoy</option>
            <option value="semana">Esta semana</option>
            <option value="mes">Este mes</option>
          </select>
        </div>
      </div>

      {/* Tabla de Reservas */}
      <div className="bg-white rounded-xl shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gradient-to-r from-cosmic-base to-astronaut-base text-white">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">ID</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Usuario</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Fecha Reserva</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Estado</th>
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">Servicios</th>
                <th className="px-6 py-4 text-right text-xs font-semibold uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {reservas.map((reserva) => {
                const usuario = usuarios.find((u) => u.id === reserva.usuarioId);
                return (
                  <tr key={reserva.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm font-bold text-cosmic-base">#{reserva.id}</span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gradient-to-br from-flame-light to-cosmic-light rounded-full flex items-center justify-center text-flame-dark font-semibold">
                          {usuario?.primerNombre?.charAt(0)}{usuario?.primerApellido?.charAt(0)}
                        </div>
                        <div>
                          <p className="text-sm font-medium text-gray-900">
                            {usuario?.primerNombre} {usuario?.primerApellido}
                          </p>
                          <p className="text-xs text-gray-500">{usuario?.credencial?.correo}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center gap-2">
                        <Calendar className="w-4 h-4 text-cosmic-base" />
                        <span className="text-sm text-gray-700">
                          {new Date(reserva.fechaReserva).toLocaleDateString("es-ES", {
                            day: '2-digit',
                            month: 'short',
                            year: 'numeric'
                          })}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold inline-flex items-center gap-1 ${
                          reserva.estado === "confirmada"
                            ? "bg-green-100 text-green-800"
                            : reserva.estado === "pendiente"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {reserva.estado === "confirmada" && <CheckCircle2 className="w-3 h-3" />}
                        {reserva.estado === "pendiente" && <Clock className="w-3 h-3" />}
                        {reserva.estado === "cancelada" && <X className="w-3 h-3" />}
                        {reserva.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex gap-2">
                        {reserva.viajeId && (
                          <span className="p-2 bg-blue-100 text-blue-600 rounded-lg" title="Vuelo">
                            <Plane className="w-4 h-4" />
                          </span>
                        )}
                        {reserva.alojamientoId && (
                          <span className="p-2 bg-green-100 text-green-600 rounded-lg" title="Hotel">
                            <Hotel className="w-4 h-4" />
                          </span>
                        )}
                        {reserva.transporteId && (
                          <span className="p-2 bg-purple-100 text-purple-600 rounded-lg" title="Transporte">
                            <Car className="w-4 h-4" />
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => onViewDetails(reserva)}
                        className="inline-flex items-center gap-2 px-3 py-2 text-cosmic-base hover:bg-cosmic-light rounded-lg transition-colors"
                      >
                        <Eye className="w-4 h-4" />
                        Ver
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {reservas.length === 0 && (
          <div className="text-center py-16">
            <Ticket className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600 text-lg">No se encontraron reservas</p>
            <p className="text-gray-500 text-sm">Intenta ajustar los filtros de búsqueda</p>
          </div>
        )}

        {/* Paginación */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-gray-200 bg-gray-50">
            <p className="text-sm text-gray-600">
              Página {currentPage} de {totalPages}
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                disabled={currentPage === 1}
                className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Anterior
              </button>
              <button
                onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                disabled={currentPage === totalPages}
                className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Siguiente
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// Estadísticas Section
function EstadisticasSection({ stats, reservas, usuarios }) {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-6 flex items-center gap-2">
          <Activity className="w-6 h-6 text-cosmic-base" />
          Estadísticas Detalladas
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="p-6 bg-gradient-to-br from-cosmic-light to-astronaut-light rounded-xl">
            <h3 className="text-sm font-medium text-gray-700 mb-2">Tasa de Conversión</h3>
            <p className="text-3xl font-bold text-cosmic-dark">
              {stats.totalReservas > 0 
                ? ((stats.reservasConfirmadas / stats.totalReservas) * 100).toFixed(1) 
                : 0}%
            </p>
            <p className="text-xs text-gray-600 mt-1">Reservas confirmadas/Total</p>
          </div>

          <div className="p-6 bg-gradient-to-br from-green-100 to-emerald-100 rounded-xl">
            <h3 className="text-sm font-medium text-gray-700 mb-2">Ingresos Promedio</h3>
            <p className="text-3xl font-bold text-green-700">
              ${stats.reservasConfirmadas > 0 
                ? (stats.ingresosTotales / stats.reservasConfirmadas).toFixed(2) 
                : 0}
            </p>
            <p className="text-xs text-gray-600 mt-1">Por reserva confirmada</p>
          </div>

          <div className="p-6 bg-gradient-to-br from-yellow-100 to-orange-100 rounded-xl">
            <h3 className="text-sm font-medium text-gray-700 mb-2">Usuarios por Admin</h3>
            <p className="text-3xl font-bold text-orange-700">
              {stats.administradores > 0 
                ? (stats.totalUsuarios / stats.administradores).toFixed(1) 
                : stats.totalUsuarios}
            </p>
            <p className="text-xs text-gray-600 mt-1">Ratio usuario/administrador</p>
          </div>
        </div>
      </div>

      {/* Más estadísticas aquí */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Análisis de Actividad</h3>
        <div className="space-y-4">
          <div className="flex items-center justify-between p-4 bg-blue-50 rounded-lg">
            <span className="text-sm font-medium text-gray-700">Total de Usuarios Registrados</span>
            <span className="text-2xl font-bold text-blue-600">{stats.totalUsuarios}</span>
          </div>
          <div className="flex items-center justify-between p-4 bg-green-50 rounded-lg">
            <span className="text-sm font-medium text-gray-700">Usuarios Activos</span>
            <span className="text-2xl font-bold text-green-600">{stats.usuariosActivos}</span>
          </div>
          <div className="flex items-center justify-between p-4 bg-purple-50 rounded-lg">
            <span className="text-sm font-medium text-gray-700">Administradores</span>
            <span className="text-2xl font-bold text-purple-600">{stats.administradores}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

// ==================== MODALES ====================

// Modal: Editar Usuario
function EditUserModal({ user, editForm, setEditForm, onSave, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Edit2 className="w-6 h-6 text-cosmic-base" />
            Editar Usuario
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Primer Nombre *</label>
            <input
              type="text"
              value={editForm.primerNombre}
              onChange={(e) => setEditForm({ ...editForm, primerNombre: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
              placeholder="Juan"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Segundo Nombre</label>
            <input
              type="text"
              value={editForm.segundoNombre}
              onChange={(e) => setEditForm({ ...editForm, segundoNombre: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
              placeholder="Carlos"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Primer Apellido *</label>
            <input
              type="text"
              value={editForm.primerApellido}
              onChange={(e) => setEditForm({ ...editForm, primerApellido: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
              placeholder="Pérez"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Segundo Apellido</label>
            <input
              type="text"
              value={editForm.segundoApellido}
              onChange={(e) => setEditForm({ ...editForm, segundoApellido: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
              placeholder="González"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono *</label>
            <input
              type="tel"
              value={editForm.telefono}
              onChange={(e) => setEditForm({ ...editForm, telefono: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
              placeholder="+57 300 123 4567"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nacionalidad *</label>
            <select
              value={editForm.nacionalidad}
              onChange={(e) => setEditForm({ ...editForm, nacionalidad: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
            >
              <option value="">Selecciona...</option>
              <option value="Colombia">Colombia</option>
              <option value="Mexico">México</option>
              <option value="Argentina">Argentina</option>
              <option value="Peru">Perú</option>
              <option value="Chile">Chile</option>
              <option value="Ecuador">Ecuador</option>
            </select>
          </div>
        </div>

        <div className="flex gap-3 mt-6">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
          >
            Cancelar
          </button>
          <button
            onClick={onSave}
            className="flex-1 px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium"
          >
            Guardar Cambios
          </button>
        </div>
      </div>
    </div>
  );
}

// Modal: Promover a Admin
function PromoteModal({ user, onConfirm, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6">
        <div className="text-center mb-6">
          <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <ShieldCheck className="w-8 h-8 text-purple-600" />
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Promover a Administrador</h3>
          <p className="text-gray-600">
            ¿Estás seguro de promover a{" "}
            <span className="font-semibold text-gray-900">
              {user?.primerNombre} {user?.primerApellido}
            </span>{" "}
            a administrador?
          </p>
        </div>

        <div className="bg-purple-50 border border-purple-200 rounded-lg p-4 mb-6">
          <p className="text-sm text-purple-900 font-medium mb-2">
            ⚠️ Esta acción le dará permisos completos de administrador:
          </p>
          <ul className="text-sm text-purple-800 space-y-1 ml-4">
            <li>✓ Acceso al panel de administración</li>
            <li>✓ Gestión de usuarios</li>
            <li>✓ Gestión de reservas</li>
            <li>✓ Ver estadísticas completas</li>
          </ul>
        </div>

        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium"
          >
            Confirmar Promoción
          </button>
        </div>
      </div>
    </div>
  );
}

// Modal: Degradar a Usuario
function DemoteModal({ user, onConfirm, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6">
        <div className="text-center mb-6">
          <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <ShieldOff className="w-8 h-8 text-orange-600" />
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Degradar a Usuario Regular</h3>
          <p className="text-gray-600">
            ¿Estás seguro de degradar a{" "}
            <span className="font-semibold text-gray-900">
              {user?.primerNombre} {user?.primerApellido}
            </span>{" "}
            a usuario regular?
          </p>
        </div>

        <div className="bg-orange-50 border border-orange-200 rounded-lg p-4 mb-6">
          <p className="text-sm text-orange-900 font-medium mb-2">
            ⚠️ Esta acción removerá todos los permisos de administrador:
          </p>
          <ul className="text-sm text-orange-800 space-y-1 ml-4">
            <li>✗ Sin acceso al panel de administración</li>
            <li>✗ Sin gestión de usuarios</li>
            <li>✗ Sin gestión de reservas</li>
            <li>✗ Sin acceso a estadísticas</li>
          </ul>
        </div>

        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors font-medium"
          >
            Confirmar Degradación
          </button>
        </div>
      </div>
    </div>
  );
}

// Modal: Eliminar Usuario
function DeleteModal({ user, onConfirm, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6">
        <div className="text-center mb-6">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Trash2 className="w-8 h-8 text-red-600" />
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Eliminar Usuario</h3>
          <p className="text-gray-600">
            ¿Estás seguro de eliminar a{" "}
            <span className="font-semibold text-gray-900">
              {user?.primerNombre} {user?.primerApellido}
            </span>
            ?
          </p>
        </div>

        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <p className="text-sm text-red-900 font-bold mb-2">
            ⚠️ ADVERTENCIA: Esta acción NO se puede deshacer
          </p>
          <ul className="text-sm text-red-800 space-y-1 ml-4">
            <li>• Se eliminará permanentemente el usuario</li>
            <li>• Se perderán todas sus reservas</li>
            <li>• No se podrá recuperar esta información</li>
          </ul>
        </div>

        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-medium"
          >
            Eliminar Permanentemente
          </button>
        </div>
      </div>
    </div>
  );
}

// Modal: Crear Administrador
function CreateAdminModal({ form, setForm, onSubmit, onClose }) {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <UserPlus className="w-6 h-6 text-flame-base" />
            Crear Nuevo Administrador
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Primer Nombre *</label>
              <input
                type="text"
                value={form.primerNombre}
                onChange={(e) => setForm({ ...form, primerNombre: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                placeholder="Juan"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Primer Apellido *</label>
              <input
                type="text"
                value={form.primerApellido}
                onChange={(e) => setForm({ ...form, primerApellido: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                placeholder="Pérez"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Cargo *</label>
              <input
                type="text"
                value={form.cargo}
                onChange={(e) => setForm({ ...form, cargo: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                placeholder="Gerente de Operaciones"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
              <input
                type="tel"
                value={form.telefono}
                onChange={(e) => setForm({ ...form, telefono: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                placeholder="+57 300 123 4567"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
              <input
                type="email"
                value={form.correo}
                onChange={(e) => setForm({ ...form, correo: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                placeholder="admin@travelgo.com"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña *</label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  value={form.contrasena}
                  onChange={(e) => setForm({ ...form, contrasena: e.target.value })}
                  required
                  minLength={6}
                  className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                  placeholder="Mínimo 6 caracteres"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-sm text-blue-900 font-medium">
              ℹ️ El nuevo administrador tendrá acceso completo al panel de administración
            </p>
          </div>

          <div className="flex gap-3 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2 bg-flame-base text-white rounded-lg hover:bg-flame-dark transition-colors font-medium"
            >
              Crear Administrador
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// Modal: Detalles del Usuario
function UserDetailsModal({ user, reservas, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-3xl w-full p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Info className="w-6 h-6 text-cosmic-base" />
            Detalles del Usuario
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="space-y-6">
          {/* Info Personal */}
          <div className="bg-gradient-to-r from-cosmic-light to-astronaut-light rounded-xl p-6">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-20 h-20 bg-gradient-to-br from-cosmic-base to-astronaut-base rounded-full flex items-center justify-center text-white text-3xl font-bold shadow-lg">
                {user?.primerNombre?.charAt(0)}{user?.primerApellido?.charAt(0)}
              </div>
              <div>
                <h4 className="text-2xl font-bold text-gray-900">
                  {user?.primerNombre} {user?.segundoNombre} {user?.primerApellido} {user?.segundoApellido}
                </h4>
                <div className="flex gap-2 mt-2">
                  <RoleBadge role={user?.credencial?.tipoUsuario} />
                  <StatusBadge status={user?.credencial?.estaActivo} />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center gap-2 text-sm">
                <Mail className="w-4 h-4 text-cosmic-base" />
                <span className="text-gray-700">{user?.credencial?.correo}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <Phone className="w-4 h-4 text-cosmic-base" />
                <span className="text-gray-700">{user?.telefono || 'N/A'}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <MapPin className="w-4 h-4 text-cosmic-base" />
                <span className="text-gray-700">{user?.nacionalidad || 'N/A'}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <Calendar className="w-4 h-4 text-cosmic-base" />
                <span className="text-gray-700">{user?.fechaNacimiento || 'N/A'}</span>
              </div>
            </div>
          </div>

          {/* Reservas del Usuario */}
          <div>
            <h4 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
              <Ticket className="w-5 h-5 text-cosmic-base" />
              Reservas ({reservas.length})
            </h4>
            {reservas.length > 0 ? (
              <div className="space-y-3">
                {reservas.map(reserva => (
                  <div key={reserva.id} className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-semibold text-gray-900">Reserva #{reserva.id}</p>
                        <p className="text-sm text-gray-600">
                          {new Date(reserva.fechaReserva).toLocaleDateString('es-ES')}
                        </p>
                      </div>
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          reserva.estado === "confirmada"
                            ? "bg-green-100 text-green-800"
                            : reserva.estado === "pendiente"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {reserva.estado}
                      </span>
                    </div>
                    <div className="flex gap-2 mt-2">
                      {reserva.viajeId && <span className="p-1 bg-blue-100 text-blue-600 rounded text-xs">✈️ Vuelo</span>}
                      {reserva.alojamientoId && <span className="p-1 bg-green-100 text-green-600 rounded text-xs">🏨 Hotel</span>}
                      {reserva.transporteId && <span className="p-1 bg-purple-100 text-purple-600 rounded text-xs">🚗 Transporte</span>}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-center py-8">No hay reservas registradas</p>
            )}
          </div>
        </div>

        <button
          onClick={onClose}
          className="mt-6 w-full px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium"
        >
          Cerrar
        </button>
      </div>
    </div>
  );
}

// Modal: Detalles de Reserva
function ReservaDetailsModal({ reserva, usuario, onClose }) {
  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Ticket className="w-6 h-6 text-cosmic-base" />
            Detalles de Reserva #{reserva?.id}
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="space-y-6">
          {/* Usuario */}
          <div className="bg-gray-50 rounded-xl p-4">
            <h4 className="font-semibold text-gray-800 mb-3">Cliente</h4>
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-gradient-to-br from-cosmic-base to-astronaut-base rounded-full flex items-center justify-center text-white font-bold">
                {usuario?.primerNombre?.charAt(0)}{usuario?.primerApellido?.charAt(0)}
              </div>
              <div>
                <p className="font-medium text-gray-900">
                  {usuario?.primerNombre} {usuario?.primerApellido}
                </p>
                <p className="text-sm text-gray-600">{usuario?.credencial?.correo}</p>
                <p className="text-sm text-gray-600">{usuario?.telefono}</p>
              </div>
            </div>
          </div>

          {/* Detalles de la Reserva */}
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-blue-50 rounded-lg p-4">
              <p className="text-sm text-gray-600 mb-1">Fecha de Reserva</p>
              <p className="font-semibold text-gray-900">
                {new Date(reserva?.fechaReserva).toLocaleDateString('es-ES', {
                  day: '2-digit',
                  month: 'long',
                  year: 'numeric'
                })}
              </p>
            </div>

            <div className="bg-green-50 rounded-lg p-4">
              <p className="text-sm text-gray-600 mb-1">Estado</p>
              <span
                className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${
                  reserva?.estado === "confirmada"
                    ? "bg-green-200 text-green-900"
                    : reserva?.estado === "pendiente"
                    ? "bg-yellow-200 text-yellow-900"
                    : "bg-red-200 text-red-900"
                }`}
              >
                {reserva?.estado}
              </span>
            </div>
          </div>

          {/* Servicios Incluidos */}
          <div>
            <h4 className="font-semibold text-gray-800 mb-3">Servicios Incluidos</h4>
            <div className="grid grid-cols-3 gap-3">
              {reserva?.viajeId && (
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 text-center">
                  <Plane className="w-8 h-8 text-blue-600 mx-auto mb-2" />
                  <p className="text-sm font-medium text-blue-900">Vuelo</p>
                  <p className="text-xs text-blue-700">ID: {reserva.viajeId}</p>
                </div>
              )}
              {reserva?.alojamientoId && (
                <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
                  <Hotel className="w-8 h-8 text-green-600 mx-auto mb-2" />
                  <p className="text-sm font-medium text-green-900">Hotel</p>
                  <p className="text-xs text-green-700">ID: {reserva.alojamientoId}</p>
                </div>
              )}
              {reserva?.transporteId && (
                <div className="bg-purple-50 border border-purple-200 rounded-lg p-4 text-center">
                  <Car className="w-8 h-8 text-purple-600 mx-auto mb-2" />
                  <p className="text-sm font-medium text-purple-900">Transporte</p>
                  <p className="text-xs text-purple-700">ID: {reserva.transporteId}</p>
                </div>
              )}
            </div>
          </div>
        </div>

        <button
          onClick={onClose}
          className="mt-6 w-full px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium"
        >
          Cerrar
        </button>
      </div>
    </div>
  );
}



export { 
  EditUserModal, 
  PromoteModal, 
  DemoteModal, 
  DeleteModal, 
  CreateAdminModal,
  UserDetailsModal,
  ReservaDetailsModal,
  DashboardSection,
  UsersSection,
  ReservasSection,
  EstadisticasSection,
  StatCard,
  RoleBadge,
  StatusBadge,
};
