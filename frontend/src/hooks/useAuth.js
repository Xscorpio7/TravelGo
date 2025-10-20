import { useState, useEffect } from 'react';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Verificar autenticación al cargar la página
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const usuarioId = localStorage.getItem('usuarioId');
    const correo = localStorage.getItem('correo');
    const primerNombre = localStorage.getItem('primerNombre');
    const primerApellido = localStorage.getItem('primerApellido');

    if (storedToken && usuarioId) {
      setToken(storedToken);
      setUser({
        usuarioId,
        correo,
        primerNombre,
        primerApellido,
      });
      setIsAuthenticated(true);
    }

    setLoading(false);
  }, []);

  const login = (loginData) => {
    localStorage.setItem('token', loginData.token);
    localStorage.setItem('usuarioId', loginData.usuarioId);
    localStorage.setItem('correo', loginData.correo);
    localStorage.setItem('primerNombre', loginData.primerNombre);
    localStorage.setItem('primerApellido', loginData.primerApellido);

    setToken(loginData.token);
    setUser({
      usuarioId: loginData.usuarioId,
      correo: loginData.correo,
      primerNombre: loginData.primerNombre,
      primerApellido: loginData.primerApellido,
    });
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('correo');
    localStorage.removeItem('primerNombre');
    localStorage.removeItem('primerApellido');

    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  };

  const getAuthHeader = () => {
    if (token) {
      return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      };
    }
    return { 'Content-Type': 'application/json' };
  };

  return {
    user,
    token,
    loading,
    isAuthenticated,
    login,
    logout,
    getAuthHeader,
  };
};