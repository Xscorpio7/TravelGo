import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Heart,
  Settings,
  AlertCircle,
  CheckCircle2,
  Eye,
  EyeOff,
} from 'lucide-react';

export default function UserProfile() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('info');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Usuario actual
  const [user, setUser] = useState(null);
  const [reservas, setReservas] = useState([]);

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
    loadReservas();
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
        primerApellido: data.primerApellido,
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

  const loadReservas = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:9090/api/bookings/reservations', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setReservas(data.data || []);
      }
    } catch (err) {
      console.error('Error al cargar reservas:', err);
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
      primerApellido: user.primerApellido,
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
      setError('Las contraseñas no coinciden');
      return;
    }

    if (passwordData.nueva.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
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
    } catch (err) {
      console.error('Error:', err);
      setError('❌ Error al cambiar la contraseña');
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-cosmic-base mx-auto mb-4"></div>
          <p className="text-gray-600">Cargando perfil...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
          <p className="text-gray-800 font-medium">No se pudo cargar el perfil</p>
          <button
            onClick={() => navigate('/')}
            className="mt-4 px-6 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark"
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
        {/* Header con avatar */}
        <div className="bg-white rounded-2xl shadow-lg p-8 mb-6">
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
            {/* Avatar */}
            <div className="relative">
              <div className="w-32 h-32 rounded-full bg-gradient-to-br from-cosmic-base to-astronaut-base flex items-center justify-center text-white text-4xl font-bold shadow-lg">
                {user.primerNombre?.charAt(0)}{user.primerApellido?.charAt(0)}
              </div>
            </div>

            {/* Info básica */}
            <div className="flex-1 text-center md:text-left">
              <h1 className="text-3xl font-bold text-astronaut-dark mb-2">
                {user.primerNombre} {user.primerApellido}
              </h1>
              <p className="text-gray-600 flex items-center justify-center md:justify-start gap-2 mb-4">
                <Mail className="w-4 h-4" />
                {user.credencial?.correo}
              </p>
              <div className="flex flex-wrap gap-3 justify-center md:justify-start">
                <span className="px-4 py-2 bg-cosmic-light text-cosmic-dark rounded-full text-sm font-medium">
                  {reservas.length} Reservas
                </span>
                <span className="px-4 py-2 bg-astronaut-light text-astronaut-dark rounded-full text-sm font-medium">
                  Usuario desde {new Date().getFullYear()}
                </span>
              </div>
            </div>

            {/* Botón logout */}
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
              Cerrar sesión
            </button>
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
                  onClick={() => setActiveTab(id)}
                  className={`flex items-center gap-2 px-6 py-4 font-medium border-b-2 transition-colors whitespace-nowrap ${
                    activeTab === id
                      ? 'border-cosmic-base text-cosmic-base'
                      : 'border-transparent text-gray-600 hover:text-cosmic-base'
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
              <div className="mb-6 flex items-center gap-2 p-4 bg-red-100 text-red-700 rounded-lg">
                <AlertCircle className="w-5 h-5" />
                {error}
              </div>
            )}

            {success && (
              <div className="mb-6 flex items-center gap-2 p-4 bg-green-100 text-green-700 rounded-lg">
                <CheckCircle2 className="w-5 h-5" />
                {success}
              </div>
            )}

            {/* Tab: Información Personal */}
            {activeTab === 'info' && (
              <div>
                <div className="flex justify-between items-center mb-6">
                  <h2 className="text-2xl font-bold text-astronaut-dark">
                    Información Personal
                  </h2>
                  {!editMode ? (
                    <button
                      onClick={handleEdit}
                      className="flex items-center gap-2 px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors"
                    >
                      <Edit2 className="w-4 h-4" />
                      Editar
                    </button>
                  ) : (
                    <div className="flex gap-2">
                      <button
                        onClick={handleCancel}
                        className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors"
                      >
                        <X className="w-4 h-4" />
                        Cancelar
                      </button>
                      <button
                        onClick={handleSave}
                        disabled={saving}
                        className="flex items-center gap-2 px-4 py-2 bg-flame-base text-white rounded-lg hover:bg-flame-dark transition-colors disabled:opacity-50"
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
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Primer Nombre
                    </label>
                    {editMode ? (
                      <input
                        type="text"
                        value={editData.primerNombre}
                        onChange={(e) => setEditData({ ...editData, primerNombre: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                    ) : (
                      <p className="text-gray-900 font-medium">{user.primerNombre}</p>
                    )}
                  </div>

                  {/* Primer Apellido */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Primer Apellido
                    </label>
                    {editMode ? (
                      <input
                        type="text"
                        value={editData.primerApellido}
                        onChange={(e) => setEditData({ ...editData, primerApellido: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                    ) : (
                      <p className="text-gray-900 font-medium">{user.primerApellido}</p>
                    )}
                  </div>

                  {/* Email (solo lectura) */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center gap-2">
                      <Mail className="w-4 h-4" />
                      Email
                    </label>
                    <p className="text-gray-900 font-medium">{user.credencial?.correo}</p>
                    <p className="text-xs text-gray-500 mt-1">
                      Contacta soporte para cambiar tu email
                    </p>
                  </div>

                  {/* Teléfono */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center gap-2">
                      <Phone className="w-4 h-4" />
                      Teléfono
                    </label>
                    {editMode ? (
                      <input
                        type="tel"
                        value={editData.telefono}
                        onChange={(e) => setEditData({ ...editData, telefono: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                    ) : (
                      <p className="text-gray-900 font-medium">{user.telefono}</p>
                    )}
                  </div>

                  {/* Fecha Nacimiento */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      Fecha de Nacimiento
                    </label>
                    <p className="text-gray-900 font-medium">{user.fechaNacimiento}</p>
                  </div>

                  {/* Nacionalidad */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center gap-2">
                      <Globe className="w-4 h-4" />
                      Nacionalidad
                    </label>
                    {editMode ? (
                      <select
                        value={editData.nacionalidad}
                        onChange={(e) => setEditData({ ...editData, nacionalidad: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      >
                        <option value="Colombia">Colombia</option>
                        <option value="Mexico">México</option>
                        <option value="Argentina">Argentina</option>
                        <option value="Peru">Perú</option>
                      </select>
                    ) : (
                      <p className="text-gray-900 font-medium">{user.nacionalidad}</p>
                    )}
                  </div>

                  {/* Género */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Género
                    </label>
                    <p className="text-gray-900 font-medium">
                      {user.genero === 'MALE' ? 'Masculino' : user.genero === 'FEMALE' ? 'Femenino' : 'Otro'}
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Tab: Reservas */}
            {activeTab === 'reservas' && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Mis Reservas
                </h2>

                {reservas.length === 0 ? (
                  <div className="text-center py-12">
                    <Ticket className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                    <p className="text-gray-600 mb-4">No tienes reservas aún</p>
                    <button
                      onClick={() => navigate('/')}
                      className="px-6 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark"
                    >
                      Buscar vuelos
                    </button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {reservas.map((reserva) => (
                      <div
                        key={reserva.id}
                        className="border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow"
                      >
                        <div className="flex justify-between items-start mb-4">
                          <div>
                            <h3 className="font-bold text-lg text-astronaut-dark">
                              Reserva #{reserva.id}
                            </h3>
                            <p className="text-sm text-gray-500">
                              {new Date(reserva.fechaReserva).toLocaleDateString('es-ES')}
                            </p>
                          </div>
                          <span
                            className={`px-3 py-1 rounded-full text-sm font-medium ${
                              reserva.estado === 'confirmada'
                                ? 'bg-green-100 text-green-800'
                                : reserva.estado === 'pendiente'
                                ? 'bg-yellow-100 text-yellow-800'
                                : 'bg-red-100 text-red-800'
                            }`}
                          >
                            {reserva.estado}
                          </span>
                        </div>
                        <button
                          className="text-cosmic-base hover:underline text-sm font-medium"
                        >
                          Ver detalles →
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Tab: Seguridad */}
            {activeTab === 'security' && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Cambiar Contraseña
                </h2>

                <form onSubmit={handleChangePassword} className="max-w-md space-y-6">
                  {/* Contraseña actual */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Contraseña Actual
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.actual ? 'text' : 'password'}
                        value={passwordData.actual}
                        onChange={(e) => setPasswordData({ ...passwordData, actual: e.target.value })}
                        required
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, actual: !showPasswords.actual })}
                        className="absolute right-3 top-2.5 text-gray-500"
                      >
                        {showPasswords.actual ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                  </div>

                  {/* Nueva contraseña */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nueva Contraseña
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.nueva ? 'text' : 'password'}
                        value={passwordData.nueva}
                        onChange={(e) => setPasswordData({ ...passwordData, nueva: e.target.value })}
                        required
                        minLength={6}
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, nueva: !showPasswords.nueva })}
                        className="absolute right-3 top-2.5 text-gray-500"
                      >
                        {showPasswords.nueva ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                  </div>

                  {/* Confirmar contraseña */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Confirmar Nueva Contraseña
                    </label>
                    <div className="relative">
                      <input
                        type={showPasswords.confirmar ? 'text' : 'password'}
                        value={passwordData.confirmar}
                        onChange={(e) => setPasswordData({ ...passwordData, confirmar: e.target.value })}
                        required
                        minLength={6}
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-cosmic-base focus:border-transparent"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPasswords({ ...showPasswords, confirmar: !showPasswords.confirmar })}
                        className="absolute right-3 top-2.5 text-gray-500"
                      >
                        {showPasswords.confirmar ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                      </button>
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={saving}
                    className="w-full bg-flame-base hover:bg-flame-dark text-white font-medium py-3 px-6 rounded-lg transition-colors disabled:opacity-50"
                  >
                    {saving ? 'Cambiando...' : 'Cambiar Contraseña'}
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>

        {/* Botón volver */}
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate('/')}
            className="text-cosmic-base hover:underline font-medium"
          >
            ← Volver al inicio
          </button>
        </div>
      </div>
    </div>
  );
}