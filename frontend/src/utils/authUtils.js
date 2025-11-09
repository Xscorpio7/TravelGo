/**
 * Utilidad para manejo de autenticación y tokens JWT
 */

const TOKEN_KEY = 'token';
const REFRESH_KEY = 'refreshToken';
const TOKEN_EXPIRY_KEY = 'tokenExpiry';

export const authUtils = {
  /**
   * Guardar token con timestamp de expiración
   */
  saveToken: (token, expiresInSeconds = 7200) => {
    const expiryTime = Date.now() + (expiresInSeconds * 1000);
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(TOKEN_EXPIRY_KEY, expiryTime.toString());
  },

  /**
   * Obtener token actual
   */
  getToken: () => {
    return localStorage.getItem(TOKEN_KEY);
  },

  /**
   * Verificar si el token está expirado
   */
  isTokenExpired: () => {
    const expiryTime = localStorage.getItem(TOKEN_EXPIRY_KEY);
    if (!expiryTime) return true;
    
    return Date.now() > parseInt(expiryTime);
  },

  /**
   * Verificar si el token está próximo a expirar (10 minutos antes)
   */
  isTokenExpiringSoon: () => {
    const expiryTime = localStorage.getItem(TOKEN_EXPIRY_KEY);
    if (!expiryTime) return true;
    
    const tenMinutes = 10 * 60 * 1000;
    return Date.now() > (parseInt(expiryTime) - tenMinutes);
  },

  /**
   * Renovar token automáticamente
   */
  refreshToken: async () => {
    try {
      const currentToken = authUtils.getToken();
      if (!currentToken) {
        throw new Error('No hay token para renovar');
      }

      console.log('🔄 Renovando token...');

      const response = await fetch('http://localhost:9090/api/auth/refresh', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${currentToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Error al renovar token');
      }

      const data = await response.json();
      
      if (data.token) {
        authUtils.saveToken(data.token);
        console.log('✅ Token renovado exitosamente');
        return data.token;
      }

      throw new Error('No se recibió nuevo token');
    } catch (error) {
      console.error('❌ Error al renovar token:', error);
      // Si falla la renovación, limpiar sesión
      authUtils.clearAuth();
      window.location.href = '/login';
      return null;
    }
  },

  /**
   * Obtener token válido (renovar si es necesario)
   */
  getValidToken: async () => {
    const token = authUtils.getToken();
    
    if (!token) {
      console.log('❌ No hay token');
      return null;
    }

    if (authUtils.isTokenExpired()) {
      console.log('⚠️ Token expirado, intentando renovar...');
      return await authUtils.refreshToken();
    }

    if (authUtils.isTokenExpiringSoon()) {
      console.log('⏰ Token próximo a expirar, renovando...');
      await authUtils.refreshToken();
    }

    return token;
  },

  /**
   * Limpiar todos los datos de autenticación
   */
  clearAuth: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(TOKEN_EXPIRY_KEY);
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('correo');
    localStorage.removeItem('primerNombre');
    localStorage.removeItem('primerApellido');
    localStorage.removeItem('tipoUsuario');
    console.log('🗑️ Sesión limpiada');
  },

  /**
   * Hacer fetch con manejo automático de tokens
   */
  authenticatedFetch: async (url, options = {}) => {
    try {
      const token = await authUtils.getValidToken();
      
      if (!token) {
        throw new Error('No hay token válido');
      }

      const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      };

      const response = await fetch(url, {
        ...options,
        headers,
      });

      // Si el backend responde 401, el token no es válido
      if (response.status === 401) {
        console.log('🔐 Token inválido, intentando renovar...');
        const newToken = await authUtils.refreshToken();
        
        if (newToken) {
          // Reintentar con nuevo token
          headers.Authorization = `Bearer ${newToken}`;
          return await fetch(url, { ...options, headers });
        }
        
        throw new Error('No se pudo autenticar');
      }

      return response;
    } catch (error) {
      console.error('❌ Error en authenticatedFetch:', error);
      throw error;
    }
  },
};

/**
 * Hook personalizado para usar en componentes React
 */
export const useAuth = () => {
  const logout = () => {
    authUtils.clearAuth();
    window.location.href = '/login';
  };

  const isAuthenticated = () => {
    const token = authUtils.getToken();
    return token && !authUtils.isTokenExpired();
  };

  return {
    logout,
    isAuthenticated,
    getValidToken: authUtils.getValidToken,
    authenticatedFetch: authUtils.authenticatedFetch,
  };
};