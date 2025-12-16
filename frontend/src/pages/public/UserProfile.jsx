import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  User,
  Mail,
  Phone,
  Calendar,
  Globe,
  Lock,
  Edit2,
  Save,
  X,
  LogOut,
  Ticket,
  Home,
  AlertCircle,
  CheckCircle2,
  Eye,
  EyeOff,
  Plane,
  MapPin,
  FileText,
  Shield,
  PartyPopper,
} from 'lucide-react';

export default function UserProfile() {
  const navigate = useNavigate();
  const location = useLocation();
  const [activeTab, setActiveTab] = useState('info');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  

  // Detectar si viene de una reserva exitosa
  useEffect(() => {
  if (location.state?.bookingSuccess) {
    setActiveTab('reservas');
    setSuccess(`🎉 ¡Reserva confirmada! Número: ${location.state.confirmationNumber}`);
    
    // Limpiar el estado
    window.history.replaceState({}, document.title);
    
    // ✅ AGREGAR: Recargar reservas para mostrar la nueva
    setTimeout(() => {
      loadReservasCompletas();
    }, 500);
    
    // Auto-ocultar mensaje después de 5 segundos
    setTimeout(() => setSuccess(''), 5000);
  }
}, [location]);

  // Usuario actual
  const [user, setUser] = useState(null);
  const [reservas, setReservas] = useState([]);
  const [reservasCompletas, setReservasCompletas] = useState([]); 
const [loadingReservas, setLoadingReservas] = useState(false);  

  // Modo edición
  const [editMode, setEditMode] = useState(false);
  const [editData, setEditData] = useState({});

  // Cambio de contraseña
  const [passwordData, setPasswordData] = useState({
    actual: '',
    nueva: '',
    confirmar: '',
  });
  const [showPasswords, setShowPasswords] = useState({
    actual: false,
    nueva: false,
    confirmar: false,
  });
  

  // Cargar datos del usuario al montar
 useEffect(() => {
  loadUserData();
  loadReservasCompletas(); 
}, []);

  const loadUserData = async () => {
    try {
      const token = localStorage.getItem('token');
      const usuarioId = localStorage.getItem('usuarioId');

      if (!token || !usuarioId) {
        navigate('/login');
        return;
      }

      const response = await fetch(`http://localhost:9090/api/usuarios/${usuarioId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error('Error al cargar datos');

      const data = await response.json();
      setUser(data);
      setEditData({
        primerNombre: data.primerNombre,
        segundoNombre: data.segundoNombre || '',
        primerApellido: data.primerApellido,
        segundoApellido: data.segundoApellido || '',
        telefono: data.telefono,
        nacionalidad: data.nacionalidad,
      });
    } catch (err) {
      console.error('Error:', err);
      setError('No se pudieron cargar los datos del usuario');
    } finally {
      setLoading(false);
    }
  };

  /**
 * ✅ NUEVA VERSIÓN: Cargar reservas con todos los detalles
 */
const loadReservasCompletas = async () => {
  setLoadingReservas(true);
  try {
    const token = localStorage.getItem('token');
    const usuarioId = localStorage.getItem('usuarioId');
    
    if (!token || !usuarioId) {
      console.log('❌ No hay sesión');
      return;
    }

    console.log('📋 Cargando reservas completas para usuario:', usuarioId);
    
    const response = await fetch(
      `http://localhost:9090/api/reservas/usuario/${usuarioId}/completas`,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      console.error('❌ Error en respuesta:', errorData);
      throw new Error(errorData.error || 'Error al cargar reservas');
    }

    const data = await response.json();
    console.log('✅ Reservas completas recibidas:', data);
    
    const reservasArray = Array.isArray(data) ? data : (data.data || []);
    setReservasCompletas(reservasArray);
    
    console.log(`✅ ${reservasArray.length} reservas completas cargadas`);
  } catch (err) {
    console.error('❌ Error al cargar reservas:', err);
    setReservasCompletas([]);
  } finally {
    setLoadingReservas(false);
  }
};

  const handleEdit = () => {
    setEditMode(true);
    setError('');
    setSuccess('');
  };

  const handleCancel = () => {
    setEditMode(false);
    setEditData({
      primerNombre: user.primerNombre,
      segundoNombre: user.segundoNombre || '',
      primerApellido: user.primerApellido,
      segundoApellido: user.segundoApellido || '',
      telefono: user.telefono,
      nacionalidad: user.nacionalidad,
    });
  };

  const handleSave = async () => {
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      const usuarioId = localStorage.getItem('usuarioId');

      const response = await fetch(`http://localhost:9090/api/usuarios/${usuarioId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...user,
          ...editData,
        }),
      });

      if (!response.ok) throw new Error('Error al actualizar datos');

      const updatedUser = await response.json();
      setUser(updatedUser);
      setEditMode(false);
      setSuccess('✅ Información actualizada correctamente');

      // Actualizar localStorage
      localStorage.setItem('primerNombre', editData.primerNombre);
      localStorage.setItem('primerApellido', editData.primerApellido);
      
      // Auto-ocultar mensaje de éxito después de 3 segundos
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      console.error('Error:', err);
      setError('❌ Error al actualizar la información');
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    
    if (passwordData.nueva !== passwordData.confirmar) {
      setError('❌ Las contraseñas no coinciden');
      return;
    }

    if (passwordData.nueva.length < 6) {
      setError('❌ La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const credencialId = user.credencial.id;
      const token = localStorage.getItem('token');

      const response = await fetch(`http://localhost:9090/api/usuarios/cambiar-contrasena/${credencialId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nuevaContrasena: passwordData.nueva,
        }),
      });

      if (!response.ok) throw new Error('Error al cambiar contraseña');

      setSuccess('✅ Contraseña actualizada correctamente');
      setPasswordData({ actual: '', nueva: '', confirmar: '' });
      
      // Auto-ocultar mensaje de éxito
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      console.error('Error:', err);
      setError('❌ Error al cambiar la contraseña');
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
  if (window.confirm('¿Estás seguro que deseas cerrar sesión?')) {
    localStorage.clear();
    
    // ✅ Disparar evento para notificar cambios en localStorage
    window.dispatchEvent(new Event('storage'));
    
    // Alternativa: disparar evento personalizado
    window.dispatchEvent(new CustomEvent('logout'));
    
    navigate('/');
  }
};

  // Función para formatear fecha
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
    });
  };

  // Función para calcular edad
  const calculateAge = (birthDate) => {
    if (!birthDate) return null;
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
  };
  /**
 * ✅ NUEVA: Cancelar una reserva
 */
const handleCancelarReserva = async (reservaId) => {
  if (!window.confirm('¿Estás seguro de que deseas cancelar esta reserva?')) {
    return;
  }

  try {
    const token = localStorage.getItem('token');
    
    console.log('❌ Cancelando reserva:', reservaId);
    
    const response = await fetch(
      `http://localhost:9090/api/reservas/${reservaId}/cancelar`,
      {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Error al cancelar');
    }

    console.log('✅ Reserva cancelada');
    setSuccess('✅ Reserva cancelada correctamente');
    
    // Recargar reservas
    await loadReservasCompletas();
    
    setTimeout(() => setSuccess(''), 3000);
    
  } catch (err) {
    console.error('❌ Error:', err);
    setError(`Error al cancelar: ${err.message}`);
    setTimeout(() => setError(''), 5000);
  }
};

/**
 * ✅ NUEVA: Descargar PDF de confirmación
 */
const handleDescargarPDF = async (reservaId) => {
  try {
    const token = localStorage.getItem('token');
    
    setSuccess('⏳ Generando PDF...');
    
    console.log('📄 Descargando PDF para reserva:', reservaId);
    
    const response = await fetch(
      `http://localhost:9090/api/reservas/${reservaId}/pdf`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      throw new Error('Error al generar PDF');
    }

    // Descargar el archivo
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Reserva_TG-${String(reservaId).padStart(8, '0')}.pdf`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    console.log('✅ PDF descargado');
    setSuccess('✅ PDF descargado correctamente');
    setTimeout(() => setSuccess(''), 3000);
    
  } catch (err) {
    console.error('❌ Error:', err);
    setError('Error al descargar PDF. Inténtalo nuevamente.');
    setTimeout(() => setError(''), 5000);
  }
};

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-cosmic-base mx-auto mb-4"></div>
          <p className="text-gray-600 text-lg">Cargando perfil...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light flex items-center justify-center">
        <div className="text-center bg-white rounded-2xl p-8 shadow-lg max-w-md">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Error al cargar perfil</h2>
          <p className="text-gray-600 mb-6">No se pudo cargar la información del usuario</p>
          <button
            onClick={() => navigate('/')}
            className="px-6 py-3 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium"
          >
            Volver al inicio
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light py-8">
      <div className="container mx-auto px-4 max-w-6xl">
        {/* Header con avatar y info básica */}
        <div className="bg-white rounded-2xl shadow-lg p-8 mb-6">
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
            {/* Avatar */}
            <div className="relative">
              <div className="w-32 h-32 rounded-full bg-gradient-to-br from-cosmic-base to-astronaut-base flex items-center justify-center text-white text-4xl font-bold shadow-lg ring-4 ring-white">
                {user.primerNombre?.charAt(0)}{user.primerApellido?.charAt(0)}
              </div>
              <div className="absolute -bottom-2 -right-2 bg-green-500 w-8 h-8 rounded-full border-4 border-white"></div>
            </div>

            {/* Info básica */}
            <div className="flex-1 text-center md:text-left">
              <h1 className="text-3xl font-bold text-astronaut-dark mb-2">
                {user.primerNombre} {user.segundoNombre || ''} {user.primerApellido} {user.segundoApellido || ''}
              </h1>
              <div className="flex flex-col md:flex-row items-center md:items-start gap-3 text-gray-600 mb-4">
                <div className="flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  <span>{user.credencial?.correo}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  <span>{user.telefono}</span>
                </div>
              </div>
              <div className="flex flex-wrap gap-3 justify-center md:justify-start">
                <span className="px-4 py-2 bg-cosmic-light text-cosmic-dark rounded-full text-sm font-medium flex items-center gap-2">
                  <Ticket className="w-4 h-4" />
                  {reservas.length} Reservas
                </span>
                <span className="px-4 py-2 bg-astronaut-light text-astronaut-dark rounded-full text-sm font-medium flex items-center gap-2">
                  <Calendar className="w-4 h-4" />
                  Miembro desde 2025
                </span>
                {user.fechaNacimiento && (
                  <span className="px-4 py-2 bg-flame-light text-flame-dark rounded-full text-sm font-medium flex items-center gap-2">
                    <User className="w-4 h-4" />
                    {calculateAge(user.fechaNacimiento)} años
                  </span>
                )}
              </div>
            </div>

            {/* Botones de acción */}
            <div className="flex flex-col gap-2">
              <button
                onClick={() => navigate('/')}
                className="flex items-center gap-2 px-4 py-2 bg-cosmic-base hover:bg-cosmic-dark text-white rounded-lg transition-colors font-medium"
              >
                <Home className="w-4 h-4" />
                Inicio
              </button>
              <button
                onClick={handleLogout}
                className="flex items-center gap-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors font-medium"
              >
                <LogOut className="w-4 h-4" />
                Cerrar sesión
              </button>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div className="border-b border-gray-200">
            <nav className="flex overflow-x-auto">
              {[
                { id: 'info', label: 'Información Personal', icon: User },
                { id: 'reservas', label: 'Mis Reservas', icon: Ticket },
                { id: 'security', label: 'Seguridad', icon: Lock },
              ].map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  onClick={() => {
                    setActiveTab(id);
                    setError('');
                    setSuccess('');
                  }}
                  className={`flex items-center gap-2 px-6 py-4 font-medium border-b-2 transition-colors whitespace-nowrap ${
                    activeTab === id
                      ? 'border-cosmic-base text-cosmic-base bg-cosmic-light bg-opacity-10'
                      : 'border-transparent text-gray-600 hover:text-cosmic-base hover:bg-gray-50'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  {label}
                </button>
              ))}
            </nav>
          </div>

          {/* Contenido de tabs */}
          <div className="p-8">
            {/* Mensajes */}
            {error && (
              <div className="mb-6 flex items-center gap-2 p-4 bg-red-100 border border-red-300 text-red-700 rounded-lg animate-fade-in">
                <AlertCircle className="w-5 h-5 flex-shrink-0" />
                <span>{error}</span>
                <button
                  onClick={() => setError('')}
                  className="ml-auto p-1 hover:bg-red-200 rounded"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            )}

            {success && (
              <div className="mb-6 flex items-center gap-2 p-4 bg-green-100 border border-green-300 text-green-700 rounded-lg animate-fade-in">
                <CheckCircle2 className="w-5 h-5 flex-shrink-0" />
                <span>{success}</span>
                <button
                  onClick={() => setSuccess('')}
                  className="ml-auto p-1 hover:bg-green-200 rounded"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            )}

            {/* Tab: Información Personal */}
            {activeTab === 'info' && (
  <div>
    <div className="flex justify-between items-center mb-6">
      <div>
        <h2 className="text-2xl font-bold text-astronaut-dark">
          Información Personal
        </h2>
        <p className="text-gray-600 text-sm mt-1">
          Gestiona tu información de contacto y preferencias
        </p>
      </div>

      {!editMode ? (
        <button
          onClick={handleEdit}
          className="flex items-center gap-2 px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium"
        >
          <Edit2 className="w-4 h-4" />
          Editar
        </button>
      ) : (
        <div className="flex gap-2">
          <button
            onClick={handleCancel}
            className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors font-medium"
          >
            <X className="w-4 h-4" />
            Cancelar
          </button>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex items-center gap-2 px-4 py-2 bg-flame-base text-white rounded-lg hover:bg-flame-dark transition-colors disabled:opacity-50 font-medium"
          >
            <Save className="w-4 h-4" />
            {saving ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      )}
    </div>

    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Primer Nombre */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Primer nombre</label>
        <input
          type="text"
          name="primerNombre"
          value={editData.primerNombre || ''}
          disabled={true}
          className="mt-1 w-full border rounded-lg px-3 py-2 bg-gray-100 cursor-not-allowed"
        />
      </div>

      {/* Primer Apellido */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Primer apellido</label>
        <input
          type="text"
          name="primerApellido"
          value={editData.primerApellido || ''}
          disabled={true}
          className="mt-1 w-full border rounded-lg px-3 py-2 bg-gray-100 cursor-not-allowed"
        />
      </div>

      {/* Teléfono */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Teléfono</label>
        <input
          type="tel"
          name="telefono"
          value={editData.telefono || ''}
          onChange={(e) => setEditData({ ...editData, telefono: e.target.value })}
          disabled={!editMode}
          className={`mt-1 w-full border rounded-lg px-3 py-2 ${
            editMode
              ? 'border-gray-300 focus:border-cosmic-base focus:ring-cosmic-base'
              : 'bg-gray-100 cursor-not-allowed'
          }`}
          placeholder="Ej: +57 3101234567"
        />
      </div>

      {/* Nacionalidad */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Nacionalidad</label>
        <input
          type="text"
          name="nacionalidad"
          value={editData.nacionalidad || ''}
          onChange={(e) => setEditData({ ...editData, nacionalidad: e.target.value })}
          disabled={!editMode}
          className={`mt-1 w-full border rounded-lg px-3 py-2 ${
            editMode
              ? 'border-gray-300 focus:border-cosmic-base focus:ring-cosmic-base'
              : 'bg-gray-100 cursor-not-allowed'
          }`}
          placeholder="Ej: Colombiana"
        />
      </div>

      {/* Fecha de nacimiento */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Fecha de nacimiento</label>
        <input
          type="date"
          name="fechaNacimiento"
          value={editData.fechaNacimiento || user.fechaNacimiento || ''}
          onChange={(e) => setEditData({ ...editData, fechaNacimiento: e.target.value })}
          disabled={!editMode}
          className={`mt-1 w-full border rounded-lg px-3 py-2 ${
            editMode
              ? 'border-gray-300 focus:border-cosmic-base focus:ring-cosmic-base'
              : 'bg-gray-100 cursor-not-allowed'
          }`}
        />
      </div>

      {/* Género */}
      <div>
        <label className="block text-sm font-medium text-gray-700">Género</label>
        <select
          name="genero"
          value={editData.genero || user.genero || ''}
          onChange={(e) => setEditData({ ...editData, genero: e.target.value })}
          disabled={!editMode}
          className={`mt-1 w-full border rounded-lg px-3 py-2 ${
            editMode
              ? 'border-gray-300 focus:border-cosmic-base focus:ring-cosmic-base'
              : 'bg-gray-100 cursor-not-allowed'
          }`}
        >
          <option value="">Selecciona...</option>
          <option value="Masculino">Masculino</option>
          <option value="Femenino">Femenino</option>
          <option value="Otro">Otro</option>
        </select>
      </div>

      {/* Email */}
      <div className="md:col-span-2">
        <label className="block text-sm font-medium text-gray-700">Correo electrónico</label>
        <input
          type="email"
          name="email"
          value={user.credencial?.correo || ''}
          disabled={true}
          className="mt-1 w-full border rounded-lg px-3 py-2 bg-gray-100 cursor-not-allowed"
        />
      </div>
    </div>
  </div>
)}


            {/* Tab: Reservas */}
      
{activeTab === 'reservas' && (
  <div>
    <h2 className="text-2xl font-bold text-astronaut-dark mb-2">
      Mis Reservas
    </h2>
    <p className="text-gray-600 text-sm mb-6">
      Historial completo de tus viajes y reservas
    </p>

    {loadingReservas ? (
      <div className="flex items-center justify-center py-16">
        <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
        <span className="ml-3 text-gray-600">Cargando reservas...</span>
      </div>
    ) : reservasCompletas.length === 0 ? (
      <div className="text-center py-16 bg-gray-50 rounded-xl">
        <Ticket className="w-20 h-20 text-gray-300 mx-auto mb-4" />
        <h3 className="text-xl font-semibold text-gray-700 mb-2">
          No tienes reservas aún
        </h3>
        <p className="text-gray-500 mb-6">
          Comienza a planear tu próxima aventura
        </p>
        <button
          onClick={() => navigate('/')}
          className="px-6 py-3 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium inline-flex items-center gap-2"
        >
          <Plane className="w-5 h-5" />
          Buscar vuelos
        </button>
      </div>
    ) : (
      <div className="space-y-6">
        {reservasCompletas.map((reserva) => (
          <div
            key={reserva.id}
            className="border-2 border-gray-200 rounded-2xl overflow-hidden hover:shadow-2xl transition-all duration-300 bg-white"
          >
            {/* Header de la reserva */}
            <div className="bg-gradient-to-r from-cosmic-base to-astronaut-base p-6 text-white">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center backdrop-blur-sm">
                    <Ticket className="w-8 h-8" />
                  </div>
                  <div>
                    <h3 className="text-2xl font-bold">
                      Confirmación: {reserva.numeroConfirmacion}
                    </h3>
                    <p className="text-sm text-white text-opacity-90 flex items-center gap-2 mt-1">
                      <Calendar className="w-4 h-4" />
                      Reservado el {new Date(reserva.fechaReserva).toLocaleDateString('es-ES', {
                        day: '2-digit',
                        month: 'long',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })}
                    </p>
                  </div>
                </div>

                <div className="flex flex-col items-end gap-2">
                  <span
                    className={`px-6 py-2 rounded-full text-sm font-bold shadow-lg ${
                      reserva.estado === 'confirmada'
                        ? 'bg-green-500 text-white'
                        : reserva.estado === 'pendiente'
                        ? 'bg-yellow-500 text-white'
                        : 'bg-red-500 text-white'
                    }`}
                  >
                    {reserva.estado === 'confirmada' ? '✓ Confirmada' : 
                     reserva.estado === 'pendiente' ? '⏳ Pendiente' : 
                     '✗ Cancelada'}
                  </span>
                  
                  {reserva.estado === 'confirmada' && (
                    <button
                      onClick={() => handleCancelarReserva(reserva.id)}
                      className="text-sm text-white hover:text-red-200 underline"
                    >
                      Cancelar reserva
                    </button>
                  )}
                </div>
              </div>
            </div>

            {/* Contenido de la reserva */}
            <div className="p-6 space-y-6">
              {/* VUELO */}
              {reserva.viaje && (
                <div className="border-l-4 border-blue-500 pl-6 py-4 bg-blue-50 rounded-r-xl">
                  <div className="flex items-center gap-3 mb-4">
                    <Plane className="w-6 h-6 text-blue-600" />
                    <h4 className="text-xl font-bold text-gray-800">Vuelo</h4>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Ruta</p>
                      <p className="text-lg font-bold text-gray-800">
                        {reserva.viaje.origen} → {reserva.viaje.destino}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Fecha de Salida</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {new Date(reserva.viaje.fechaSalida).toLocaleDateString('es-ES', {
                          day: '2-digit',
                          month: 'long',
                          year: 'numeric'
                        })}
                      </p>
                    </div>
                    
                    {reserva.viaje.fechaRegreso && (
                      <div>
                        <p className="text-sm text-gray-600 font-medium">Fecha de Regreso</p>
                        <p className="text-lg font-semibold text-gray-800">
                          {new Date(reserva.viaje.fechaRegreso).toLocaleDateString('es-ES', {
                            day: '2-digit',
                            month: 'long',
                            year: 'numeric'
                          })}
                        </p>
                      </div>
                    )}
                    
                    {reserva.viaje.aerolinea && (
                      <div>
                        <p className="text-sm text-gray-600 font-medium">Aerolínea</p>
                        <p className="text-lg font-semibold text-gray-800">
                          {reserva.viaje.aerolinea}
                        </p>
                      </div>
                    )}
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Tipo</p>
                      <p className="text-lg font-semibold text-gray-800 capitalize">
                        {reserva.viaje.tipoViaje === 'round-trip' ? 'Ida y vuelta' : 'Solo ida'}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Precio Vuelo</p>
                      <p className="text-xl font-bold text-blue-600">
                        {reserva.viaje.precio} {reserva.viaje.moneda}
                      </p>
                    </div>
                  </div>
                </div>
              )}

              {/* HOTEL */}
              {reserva.hotel && (
                <div className="border-l-4 border-purple-500 pl-6 py-4 bg-purple-50 rounded-r-xl">
                  <div className="flex items-center gap-3 mb-4">
                    <MapPin className="w-6 h-6 text-purple-600" />
                    <h4 className="text-xl font-bold text-gray-800">Hotel</h4>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Nombre</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {reserva.hotel.nombre}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Ciudad</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {reserva.hotel.ciudad}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Precio</p>
                      <p className="text-xl font-bold text-purple-600">
                        {reserva.hotel.precio} USD
                      </p>
                    </div>
                  </div>
                </div>
              )}

              {/* TRANSPORTE */}
              {reserva.transporte && (
                <div className="border-l-4 border-green-500 pl-6 py-4 bg-green-50 rounded-r-xl">
                  <div className="flex items-center gap-3 mb-4">
                    <MapPin className="w-6 h-6 text-green-600" />
                    <h4 className="text-xl font-bold text-gray-800">Transporte</h4>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Tipo</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {reserva.transporte.vehiculoTipo || reserva.transporte.tipo}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Desde</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {reserva.transporte.origen}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Hasta</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {reserva.transporte.destino}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Precio</p>
                      <p className="text-xl font-bold text-green-600">
                        {reserva.transporte.precio} {reserva.transporte.moneda}
                      </p>
                    </div>
                  </div>
                </div>
              )}

              {/* PAGO */}
              {reserva.pago && (
                <div className="border-l-4 border-orange-500 pl-6 py-4 bg-orange-50 rounded-r-xl">
                  <div className="flex items-center gap-3 mb-4">
                    <FileText className="w-6 h-6 text-orange-600" />
                    <h4 className="text-xl font-bold text-gray-800">Información de Pago</h4>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Método</p>
                      <p className="text-lg font-semibold text-gray-800 capitalize">
                        {reserva.pago.metodoPago}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Fecha</p>
                      <p className="text-lg font-semibold text-gray-800">
                        {new Date(reserva.pago.fechaPago).toLocaleDateString('es-ES')}
                      </p>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Estado</p>
                      <span className={`inline-block px-3 py-1 rounded-full text-sm font-bold ${
                        reserva.pago.estado === 'pagado' ? 'bg-green-200 text-green-800' : 
                        reserva.pago.estado === 'pendiente' ? 'bg-yellow-200 text-yellow-800' : 
                        'bg-red-200 text-red-800'
                      }`}>
                        {reserva.pago.estado.toUpperCase()}
                      </span>
                    </div>
                    
                    <div>
                      <p className="text-sm text-gray-600 font-medium">Monto Total</p>
                      <p className="text-2xl font-bold text-orange-600">
                        {reserva.pago.monto} USD
                      </p>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Footer con acciones */}
            <div className="bg-gray-50 px-6 py-4 flex flex-col md:flex-row justify-between items-center gap-4 border-t">
              <div className="text-sm text-gray-600">
                ID de Reserva: <span className="font-mono font-bold">{reserva.id}</span>
              </div>
              
              <div className="flex gap-3">
                <button
                  onClick={() => handleDescargarPDF(reserva.id)}
                  className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors font-medium flex items-center gap-2"
                >
                  <FileText className="w-4 h-4" />
                  Descargar PDF
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    )}
  </div>
)}

            {/* Tab: Seguridad */}
            {activeTab === 'security' && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-2">
                  Seguridad de la Cuenta
                </h2>
                <p className="text-gray-600 text-sm mb-6">
                  Mantén tu cuenta segura actualizando tu contraseña regularmente
                </p>

                <form onSubmit={handleChangePassword} className="max-w-md space-y-6">
                  {/* Contraseña actual */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Contraseña Actual *
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.actual ? 'text' : 'password'}
                        value={passwordData.actual}
                        onChange={(e) => setPasswordData({ ...passwordData, actual: e.target.value })}
                        required
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                        placeholder="••••••••"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, actual: !showPasswords.actual })}
                        className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                      >
                        {showPasswords.actual ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                  </div>

                  {/* Nueva contraseña */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nueva Contraseña *
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.nueva ? 'text' : 'password'}
                        value={passwordData.nueva}
                        onChange={(e) => setPasswordData({ ...passwordData, nueva: e.target.value })}
                        required
                        minLength={6}
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                        placeholder="••••••••"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, nueva: !showPasswords.nueva })}
                        className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                      >
                        {showPasswords.nueva ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                    <p className="text-xs text-gray-500 mt-1">
                      Mínimo 6 caracteres
                    </p>
                  </div>

                  {/* Confirmar contraseña */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Confirmar Nueva Contraseña *
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.confirmar ? 'text' : 'password'}
                        value={passwordData.confirmar}
                        onChange={(e) => setPasswordData({ ...passwordData, confirmar: e.target.value })}
                        required
                        minLength={6}
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                        placeholder="••••••••"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, confirmar: !showPasswords.confirmar })}
                        className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                      >
                        {showPasswords.confirmar ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                  </div>

                  {/* Indicador de fortaleza de contraseña */}
                  {passwordData.nueva && (
                    <div className="bg-gray-50 rounded-lg p-4">
                      <p className="text-sm font-medium text-gray-700 mb-2">
                        Seguridad de la contraseña:
                      </p>
                      <div className="flex gap-1 mb-2">
                        <div className={`h-2 flex-1 rounded ${passwordData.nueva.length >= 6 ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                        <div className={`h-2 flex-1 rounded ${passwordData.nueva.length >= 8 ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                        <div className={`h-2 flex-1 rounded ${/[A-Z]/.test(passwordData.nueva) ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                        <div className={`h-2 flex-1 rounded ${/[0-9]/.test(passwordData.nueva) ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                      </div>
                      <ul className="text-xs text-gray-600 space-y-1">
                        <li className={passwordData.nueva.length >= 6 ? 'text-green-600' : ''}>
                          • Al menos 6 caracteres
                        </li>
                        <li className={passwordData.nueva.length >= 8 ? 'text-green-600' : ''}>
                          • Recomendado: 8+ caracteres
                        </li>
                        <li className={/[A-Z]/.test(passwordData.nueva) ? 'text-green-600' : ''}>
                          • Incluir mayúsculas
                        </li>
                        <li className={/[0-9]/.test(passwordData.nueva) ? 'text-green-600' : ''}>
                          • Incluir números
                        </li>
                      </ul>
                    </div>
                  )}

                  <button
                    type="submit"
                    disabled={saving}
                    className="w-full bg-flame-base hover:bg-flame-dark text-white font-medium py-3 px-6 rounded-lg transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                  >
                    {saving ? (
                      <>
                        <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                        Cambiando contraseña...
                      </>
                    ) : (
                      <>
                        <Lock className="w-5 h-5" />
                        Cambiar Contraseña
                      </>
                    )}
                  </button>
                </form>

                {/* Info de seguridad adicional */}
                <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <h3 className="font-semibold text-blue-900 mb-2 flex items-center gap-2">
                    <Shield className="w-5 h-5" />
                    Consejos de seguridad
                  </h3>
                  <ul className="text-sm text-blue-800 space-y-1">
                    <li>• Usa una contraseña única que no uses en otros sitios</li>
                    <li>• Combina letras, números y símbolos</li>
                    <li>• No compartas tu contraseña con nadie</li>
                    <li>• Cambia tu contraseña cada 3-6 meses</li>
                  </ul>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}