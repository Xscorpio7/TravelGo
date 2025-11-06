import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import BookingWizard from '../../components/booking/BookingWizard';
import FlightCard from '../../components/booking/FlightCard';
import HotelCard from '../../components/booking/HotelCard';
import TransportCard from '../../components/booking/TransportCard';
import BookingSummary from '../../components/booking/BookingSummary';
import PaymentForm from '../../components/booking/PaymentForm';
import LoginModal from '../../components/user/LoginModal';
import { useAuth } from '../../hooks/useAuth';
import { AlertCircle, CheckCircle, Loader, CheckCircle2 } from 'lucide-react';
import { bookingStorage } from '../../utils/bookingStorage';

export default function BookingFlow() {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, user, getAuthHeader } = useAuth();

  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Datos de reserva
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [selectedHotel, setSelectedHotel] = useState(null);
  const [selectedTransport, setSelectedTransport] = useState(null);

  // Datos para búsqueda
  const [searchData, setSearchData] = useState({
    origin: '',
    destination: '',
    departureDate: '',
    returnDate: '',
    adults: 1,
  });

  // Opciones disponibles
  const [hotels, setHotels] = useState([]);
  const [transports, setTransports] = useState([]);

  // Modal de login
  const [showLoginModal, setShowLoginModal] = useState(false);

  // **NUEVO: Recuperar datos de reserva al cargar**
  useEffect(() => {
    console.log('🔄 Verificando datos de reserva pendientes...');
    
    // 1. Intentar cargar desde location.state (navegación directa)
    if (location.state?.selectedFlight) {
      console.log('📍 Cargando desde location.state');
      setSelectedFlight(location.state.selectedFlight);
      setSelectedHotel(location.state.selectedHotel || null);
      setSelectedTransport(location.state.selectedTransport || null);
      setSearchData(location.state.searchData || searchData);
      setCurrentStep(location.state.currentStep || 1);
      return;
    }

    // 2. Intentar cargar desde localStorage (después de login/registro)
    const savedBooking = bookingStorage.get();
    if (savedBooking) {
      console.log('💾 Recuperando reserva guardada:', savedBooking);
      setSelectedFlight(savedBooking.selectedFlight);
      setSelectedHotel(savedBooking.selectedHotel || null);
      setSelectedTransport(savedBooking.selectedTransport || null);
      setSearchData(savedBooking.searchData);
      setCurrentStep(savedBooking.currentStep || 1);
      
      // **IMPORTANTE: Limpiar después de recuperar**
      bookingStorage.clear();
      return;
    }

    // 3. Si no hay datos, redirigir al home
    console.log('❌ No hay datos de reserva, redirigiendo...');
    navigate('/');
  }, [location.state, navigate]);

  // Verificar autenticación y mostrar modal si es necesario
  useEffect(() => {
    if (!isAuthenticated && selectedFlight) {
      console.log('🔐 Usuario no autenticado, guardando progreso...');
      
      // Guardar el progreso actual antes de mostrar el modal
      const currentBookingData = {
        selectedFlight,
        selectedHotel,
        selectedTransport,
        searchData,
        currentStep,
      };
      
      bookingStorage.save(currentBookingData);
      setShowLoginModal(true);
    }
  }, [isAuthenticated, selectedFlight, selectedHotel, selectedTransport, searchData, currentStep]);

  // Función para buscar hoteles
  const searchHotels = async () => {
    if (!searchData.destination) return;

    setLoading(true);
    setError('');

    try {
      const response = await fetch(
        `http://localhost:9090/hotels/search?cityCode=${searchData.destination}&max=10`,
        {
          headers: getAuthHeader(),
        }
      );

      if (!response.ok) {
        throw new Error('Error al buscar hoteles');
      }

      const data = await response.json();
      setHotels(data.data || []);
    } catch (err) {
      console.error('Error:', err);
      setError('No se pudieron cargar los hoteles');
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  // Función para buscar transportes
  const searchTransports = async () => {
    if (!searchData.destination) return;

    setLoading(true);
    setError('');

    try {
      const response = await fetch(
        `http://localhost:9090/api/transporte/search-transfers?` +
          `airportCode=${searchData.destination}&` +
          `cityName=${searchData.destination}&` +
          `countryCode=ES&` +
          `dateTime=${searchData.departureDate}T10:00:00&` +
          `passengers=${searchData.adults}`,
        {
          headers: getAuthHeader(),
        }
      );

      if (!response.ok) {
        throw new Error('Error al buscar transportes');
      }

      const data = await response.json();
      setTransports(data.data || []);
    } catch (err) {
      console.error('Error:', err);
      setError('No se pudieron cargar los transportes');
      setTransports([]);
    } finally {
      setLoading(false);
    }
  };

  // Cargar datos según el paso actual
  useEffect(() => {
    if (currentStep === 2 && hotels.length === 0 && isAuthenticated) {
      searchHotels();
    } else if (currentStep === 3 && transports.length === 0 && isAuthenticated) {
      searchTransports();
    }
  }, [currentStep, isAuthenticated]);

  // Guardar vuelo en BD
  const saveFlightBooking = async () => {
    setLoading(true);
    setError('');

    try {
      const flightData = {
        id: selectedFlight.id,
        origin: searchData.origin,
        originName: searchData.origin,
        destination: searchData.destination,
        departureDate: searchData.departureDate,
        returnDate: searchData.returnDate || null,
        price: selectedFlight.price?.total || '0',
        currency: selectedFlight.price?.currency || 'USD',
        airline: selectedFlight.itineraries?.[0]?.segments?.[0]?.carrierCode || '',
        airlineName: selectedFlight.validatingAirlineCodes?.[0] || '',
        journeyType: searchData.returnDate ? 'round-trip' : 'one-way',
        bookableSeats: selectedFlight.numberOfBookableSeats || 0,
      };

      const response = await fetch('http://localhost:9090/api/bookings/flights', {
        method: 'POST',
        headers: getAuthHeader(),
        body: JSON.stringify(flightData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al guardar el vuelo');
      }

      const data = await response.json();
      console.log('Vuelo guardado:', data);
      setSuccess('¡Vuelo reservado exitosamente!');

      // Actualizar progreso en localStorage por si cambia de página
      const updatedBooking = {
        selectedFlight,
        selectedHotel,
        selectedTransport,
        searchData,
        currentStep: 2,
        viajeId: data.viajeId, // Guardar ID del viaje
      };
      bookingStorage.save(updatedBooking);

      // Avanzar al siguiente paso después de 1.5 segundos
      setTimeout(() => {
        setSuccess('');
        setCurrentStep(2);
      }, 1500);
    } catch (err) {
      console.error('Error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Manejar siguiente paso
  const handleNext = () => {
    if (currentStep === 1) {
      saveFlightBooking();
    } else if (currentStep === 2) {
      // Actualizar progreso
      const updatedBooking = {
        selectedFlight,
        selectedHotel,
        selectedTransport,
        searchData,
        currentStep: 3,
      };
      bookingStorage.save(updatedBooking);
      setCurrentStep(3);
    } else if (currentStep === 3) {
      // Actualizar progreso
      const updatedBooking = {
        selectedFlight,
        selectedHotel,
        selectedTransport,
        searchData,
        currentStep: 4,
      };
      bookingStorage.save(updatedBooking);
      setCurrentStep(4);
    }
  };

  // Omitir paso opcional
  const handleSkip = () => {
    if (currentStep === 2) {
      setSelectedHotel(null);
      const updatedBooking = {
        selectedFlight,
        selectedHotel: null,
        selectedTransport,
        searchData,
        currentStep: 3,
      };
      bookingStorage.save(updatedBooking);
      setCurrentStep(3);
    } else if (currentStep === 3) {
      setSelectedTransport(null);
      const updatedBooking = {
        selectedFlight,
        selectedHotel,
        selectedTransport: null,
        searchData,
        currentStep: 4,
      };
      bookingStorage.save(updatedBooking);
      setCurrentStep(4);
    }
  };

  // Renderizar contenido según paso actual
  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-astronaut-dark mb-2">
                Confirma tu vuelo
              </h2>
              <p className="text-gray-600">
                Revisa los detalles de tu vuelo antes de continuar
              </p>
            </div>

            {selectedFlight && (
              <FlightCard
                flight={selectedFlight}
                onSelect={() => {}}
                isSelected={true}
              />
            )}

            {error && (
              <div className="flex items-center space-x-2 p-4 bg-red-100 text-red-700 rounded-lg">
                <AlertCircle className="w-5 h-5" />
                <span>{error}</span>
              </div>
            )}

            {success && (
              <div className="flex items-center space-x-2 p-4 bg-green-100 text-green-700 rounded-lg">
                <CheckCircle className="w-5 h-5" />
                <span>{success}</span>
              </div>
            )}

            <div className="flex justify-end space-x-4">
              <button
                onClick={() => {
                  bookingStorage.clear();
                  navigate('/');
                }}
                className="px-6 py-3 rounded-lg font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleNext}
                disabled={loading || success}
                className="px-6 py-3 rounded-lg font-medium text-white bg-flame-base hover:bg-flame-dark transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2"
              >
                {loading && <Loader className="w-5 h-5 animate-spin" />}
                <span>{loading ? 'Procesando...' : 'Confirmar y Continuar'}</span>
              </button>
            </div>
          </div>
        );

      case 2:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-astronaut-dark mb-2">
                Selecciona tu hotel (Opcional)
              </h2>
              <p className="text-gray-600">
                Elige un hotel en {searchData.destination} o continúa sin alojamiento
              </p>
            </div>

            {loading && (
              <div className="flex justify-center items-center py-12">
                <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
              </div>
            )}

            {!loading && hotels.length === 0 && (
              <div className="text-center py-12">
                <p className="text-gray-500">No se encontraron hoteles disponibles</p>
              </div>
            )}

            <div className="space-y-4">
              {hotels.map((hotel) => (
                <HotelCard
                  key={hotel.hotelId}
                  hotel={hotel}
                  onSelect={setSelectedHotel}
                  isSelected={selectedHotel?.hotelId === hotel.hotelId}
                />
              ))}
            </div>

            <div className="flex justify-between">
              <button
                onClick={handleSkip}
                className="px-6 py-3 rounded-lg font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 transition-colors"
              >
                Omitir hotel
              </button>
              <button
                onClick={handleNext}
                disabled={!selectedHotel}
                className="px-6 py-3 rounded-lg font-medium text-white bg-flame-base hover:bg-flame-dark transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Continuar
              </button>
            </div>
          </div>
        );

      case 3:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-astronaut-dark mb-2">
                Selecciona tu transporte (Opcional)
              </h2>
              <p className="text-gray-600">
                Elige un transfer desde el aeropuerto o continúa sin transporte
              </p>
            </div>

            {loading && (
              <div className="flex justify-center items-center py-12">
                <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
              </div>
            )}

            {!loading && transports.length === 0 && (
              <div className="text-center py-12">
                <p className="text-gray-500">No se encontraron transportes disponibles</p>
              </div>
            )}

            <div className="space-y-4">
              {transports.map((transport) => (
                <TransportCard
                  key={transport.id}
                  transport={transport}
                  onSelect={setSelectedTransport}
                  isSelected={selectedTransport?.id === transport.id}
                />
              ))}
            </div>

            <div className="flex justify-between">
              <button
                onClick={handleSkip}
                className="px-6 py-3 rounded-lg font-medium text-gray-700 bg-gray-200 hover:bg-gray-300 transition-colors"
              >
                Omitir transporte
              </button>
              <button
                onClick={handleNext}
                disabled={!selectedTransport}
                className="px-6 py-3 rounded-lg font-medium text-white bg-flame-base hover:bg-flame-dark transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Continuar al pago
              </button>
            </div>
          </div>
        );

      case 4:
        return (
          <PaymentStep
            selectedFlight={selectedFlight}
            selectedHotel={selectedHotel}
            selectedTransport={selectedTransport}
            searchData={searchData}
            user={user}
            getAuthHeader={getAuthHeader}
          />
        );

      default:
        return null;
    }
  };

  return (
    <>
      <LoginModal
        isOpen={showLoginModal}
        onClose={() => {
          setShowLoginModal(false);
          // NO redirigir automáticamente, el usuario decidirá
        }}
        flight={selectedFlight}
      />

      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light py-8">
        <div className="container mx-auto px-4">
          <BookingWizard currentStep={currentStep} onStepChange={setCurrentStep} />
          
          <div className="max-w-5xl mx-auto">
            {renderStepContent()}
          </div>
        </div>
      </div>
    </>
  );
}

// Componente de paso de pago
function PaymentStep({ 
  selectedFlight, 
  selectedHotel, 
  selectedTransport, 
  searchData,
  user,
  getAuthHeader 
}) {
  const [loading, setLoading] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [reservationData, setReservationData] = useState(null);
  const navigate = useNavigate();

  const handlePayment = async (paymentData) => {
    setLoading(true);

    try {
      const reservaPayload = {
        usuarioId: user.usuarioId,
        viajeId: null,
        alojamientoId: selectedHotel?.id || null,
        transporteId: selectedTransport?.id || null,
        paymentData: paymentData,
        searchData: searchData,
        flightData: selectedFlight,
      };

      const response = await fetch('http://localhost:9090/api/bookings/complete', {
        method: 'POST',
        headers: getAuthHeader(),
        body: JSON.stringify(reservaPayload),
      });

      if (!response.ok) {
        throw new Error('Error al procesar la reserva');
      }

      const data = await response.json();
      setReservationData(data);

      await new Promise(resolve => setTimeout(resolve, 3000));

      setPaymentSuccess(true);

      // **IMPORTANTE: Limpiar localStorage después de completar**
      bookingStorage.clear();

    } catch (error) {
      console.error('Error:', error);
      alert('Error al procesar el pago. Por favor intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  if (paymentSuccess) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg p-8 text-center">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle2 className="w-12 h-12 text-green-600" />
          </div>

          <h2 className="text-3xl font-bold text-gray-800 mb-4">
            ¡Reserva Confirmada!
          </h2>

          <p className="text-lg text-gray-600 mb-6">
            Tu reserva ha sido procesada exitosamente
          </p>

          <div className="bg-astronaut-light rounded-lg p-6 mb-6">
            <p className="text-sm text-gray-700 mb-2">
              Número de confirmación
            </p>
            <p className="text-2xl font-bold text-cosmic-base">
              {reservationData?.confirmationNumber || 'TG-' + Date.now()}
            </p>
          </div>

          <div className="space-y-3 text-sm text-gray-600 mb-6">
            <p>✉️ Hemos enviado la confirmación a tu correo electrónico</p>
            <p>📄 Tu PDF con los detalles del viaje está adjunto</p>
            <p>📱 Puedes ver tus reservas en tu perfil</p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3">
            <button
              onClick={() => navigate('/profile/reservations')}
              className="flex-1 bg-cosmic-base hover:bg-cosmic-dark text-white font-medium py-3 px-6 rounded-lg transition-colors"
            >
              Ver mis reservas
            </button>
            <button
              onClick={() => navigate('/')}
              className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-3 px-6 rounded-lg transition-colors"
            >
              Volver al inicio
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div className="lg:col-span-2">
        <PaymentForm onSubmit={handlePayment} loading={loading} />
      </div>

      <div className="lg:col-span-1">
        <BookingSummary
          flight={selectedFlight}
          hotel={selectedHotel}
          transport={selectedTransport}
          searchData={searchData}
        />
      </div>
    </div>
  );
}