// Utilidad para manejar el almacenamiento temporal de reservas
const BOOKING_KEY = 'pendingBooking';
const BOOKING_EXPIRY = 30 * 60 * 1000; // 30 minutos

export const bookingStorage = {
  save: (bookingData) => {
    try {
      const data = {
        ...bookingData,
        timestamp: Date.now(),
        expiresAt: Date.now() + BOOKING_EXPIRY,
      };
      localStorage.setItem(BOOKING_KEY, JSON.stringify(data));
      console.log('💾 Reserva guardada en localStorage:', data);
      return true;
    } catch (error) {
      console.error('❌ Error guardando reserva:', error);
      return false;
    }
  },

  get: () => {
    try {
      const stored = localStorage.getItem(BOOKING_KEY);
      if (!stored) {
        console.log('📭 No hay reserva guardada');
        return null;
      }

      const data = JSON.parse(stored);
      
      // Verificar si expiró
      if (Date.now() > data.expiresAt) {
        console.log('⏰ Reserva expirada');
        bookingStorage.clear();
        return null;
      }

      console.log('✅ Reserva recuperada:', data);
      return data;
    } catch (error) {
      console.error('❌ Error recuperando reserva:', error);
      return null;
    }
  },

  clear: () => {
    try {
      localStorage.removeItem(BOOKING_KEY);
      console.log('🗑️ Reserva limpiada');
      return true;
    } catch (error) {
      console.error('❌ Error limpiando reserva:', error);
      return false;
    }
  },

  updateStep: (step) => {
    const current = bookingStorage.get();
    if (current) {
      current.currentStep = step;
      bookingStorage.save(current);
    }
  },

  update: (updates) => {
    const current = bookingStorage.get();
    if (current) {
      bookingStorage.save({ ...current, ...updates });
    }
  },
};

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