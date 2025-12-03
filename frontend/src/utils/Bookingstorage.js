// src/utils/bookingStorage.js
// Utilidad para manejar el almacenamiento temporal de reservas

const BOOKING_KEY = 'pendingBooking';
const BOOKING_EXPIRY = 30 * 60 * 1000; // 30 minutos

const bookingStorage = {
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
        // console.log('📭 No hay reserva guardada');
        return null;
      }

      const data = JSON.parse(stored);

      // Verificar si expiró
      if (data.expiresAt && Date.now() > data.expiresAt) {
        console.log('⏰ Reserva expirada');
        bookingStorage.clear();
        return null;
      }

      // Normalizar: asegurar campos esperados
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

  update: (updates) => {
    try {
      const current = bookingStorage.get();
      if (!current) return false;
      const merged = { ...current, ...updates, timestamp: Date.now() };
      return bookingStorage.save(merged);
    } catch (err) {
      console.error('❌ Error en update:', err);
      return false;
    }
  },

  updateStep: (newStep) => {
    try {
      const current = bookingStorage.get();
      if (!current) return false;
      current.currentStep = newStep;
      current.timestamp = Date.now();
      return bookingStorage.save(current);
    } catch (err) {
      console.error('❌ Error en updateStep:', err);
      return false;
    }
  },

  // --- Funciones útiles añadidas ---
  hasPendingBooking: () => {
    try {
      const data = bookingStorage.get();
      if (!data) return false;
      // Considerar que hay reserva pendiente si hay cualquier selección importante
      const hasSelected =
        !!data.selectedFlight || !!data.selectedHotel || !!data.selectedTransport;
      const notExpired = !(data.expiresAt && Date.now() > data.expiresAt);
      const hasPending = hasSelected && notExpired;
      console.log('🔍 ¿Tiene reserva pendiente?', hasPending);
      return hasPending;
    } catch (err) {
      console.warn('safe hasPendingBooking error:', err);
      return false;
    }
  },

  getSummary: () => {
    try {
      const data = bookingStorage.get();
      if (!data) {
        // console.log('ℹ️ No hay resumen disponible');
        return null;
      }

      const summary = {
        hasFlight: !!data.selectedFlight,
        hasHotel: !!data.selectedHotel,
        hasTransport: !!data.selectedTransport,
        currentStep: data.currentStep || 1,
        destination: data.searchData?.destination || data.destination || 'N/A',
        origin: data.searchData?.origin || data.origin || 'N/A',
        departureDate: data.searchData?.departureDate || data.departureDate || 'N/A',
        returnDate: data.searchData?.returnDate || data.returnDate || null,
        adults: data.searchData?.adults ?? data.adults ?? 1,
        raw: data,
        summaryText: `${data.searchData?.origin || data.origin || '—'} → ${data.searchData?.destination || data.destination || '—'}`,
      };

      console.log('📋 Resumen generado:', summary);
      return summary;
    } catch (err) {
      console.error('❌ Error generando resumen:', err);
      return null;
    }
  },
};

// Exportar named y default para compatibilidad con distintas importaciones
export { bookingStorage };
export default bookingStorage;
