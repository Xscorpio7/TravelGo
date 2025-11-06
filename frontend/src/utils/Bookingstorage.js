/**
 * Utilidad para gestionar el almacenamiento temporal de datos de reserva
 * durante el proceso de login/registro
 */

const BOOKING_STORAGE_KEY = 'travelgo_pending_booking';

export const bookingStorage = {
  /**
   * Guardar datos de reserva temporal
   */
  save: (bookingData) => {
    try {
      const dataToSave = {
        ...bookingData,
        timestamp: new Date().getTime(),
        expiresIn: 30 * 60 * 1000, // 30 minutos
      };
      localStorage.setItem(BOOKING_STORAGE_KEY, JSON.stringify(dataToSave));
      console.log('✅ Datos de reserva guardados temporalmente:', dataToSave);
      return true;
    } catch (error) {
      console.error('❌ Error al guardar datos de reserva:', error);
      return false;
    }
  },

  /**
   * Obtener datos de reserva temporal
   */
  get: () => {
    try {
      const stored = localStorage.getItem(BOOKING_STORAGE_KEY);
      if (!stored) {
        console.log('ℹ️ No hay datos de reserva almacenados');
        return null;
      }

      const data = JSON.parse(stored);
      
      // Verificar si no ha expirado (30 minutos)
      const now = new Date().getTime();
      if (now - data.timestamp > data.expiresIn) {
        console.log('⏰ Datos de reserva expirados, eliminando...');
        bookingStorage.clear();
        return null;
      }

      console.log('✅ Datos de reserva recuperados:', data);
      return data;
    } catch (error) {
      console.error('❌ Error al recuperar datos de reserva:', error);
      return null;
    }
  },

  /**
   * Limpiar datos de reserva temporal
   */
  clear: () => {
    try {
      localStorage.removeItem(BOOKING_STORAGE_KEY);
      console.log('🗑️ Datos de reserva temporal eliminados');
      return true;
    } catch (error) {
      console.error('❌ Error al limpiar datos de reserva:', error);
      return false;
    }
  },

  /**
   * Verificar si existen datos de reserva pendientes
   */
  hasPendingBooking: () => {
    const data = bookingStorage.get();
    const hasPending = data !== null && data.selectedFlight !== null;
    console.log('🔍 ¿Tiene reserva pendiente?', hasPending);
    return hasPending;
  },

  /**
   * Obtener resumen de la reserva pendiente
   */
  getSummary: () => {
    const data = bookingStorage.get();
    if (!data) {
      console.log('ℹ️ No hay resumen disponible');
      return null;
    }

    const summary = {
      hasFlight: !!data.selectedFlight, // ✅ CORREGIDO el typo
      hasHotel: !!data.selectedHotel,
      hasTransport: !!data.selectedTransport,
      currentStep: data.currentStep || 1,
      destination: data.searchData?.destination || 'N/A',
      origin: data.searchData?.origin || 'N/A',
      departureDate: data.searchData?.departureDate || 'N/A',
      returnDate: data.searchData?.returnDate || null,
      adults: data.searchData?.adults || 1,
    };

    console.log('📋 Resumen generado:', summary);
    return summary;
  },

  /**
   * Actualizar paso actual sin perder datos
   */
  updateStep: (newStep) => {
    try {
      const current = bookingStorage.get();
      if (!current) return false;

      current.currentStep = newStep;
      current.timestamp = new Date().getTime(); // Renovar tiempo
      localStorage.setItem(BOOKING_STORAGE_KEY, JSON.stringify(current));
      console.log(`✅ Paso actualizado a: ${newStep}`);
      return true;
    } catch (error) {
      console.error('❌ Error al actualizar paso:', error);
      return false;
    }
  },
};