import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Check, Loader } from 'lucide-react';
import BookingWizard from '../components/booking/BookingWizard';
import FlightCard from '../components/booking/FlightCard';
import HotelCard from '../components/booking/HotelCard';
import TransportCard from '../components/booking/TransportCard';
import PaymentForm from '../components/booking/PaymentForm';
import BookingSummary from '../components/booking/BookingSummary';
import BookingModal from '../components/booking/BookingModal';
import { bookingStorage } from '../utils/bookingStorage';

export default function BookingPage() {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showAuthModal, setShowAuthModal] = useState(false);

  // Datos de la reserva
  const [bookingData, setBookingData] = useState({
    searchData: null,
    selectedFlight: null,
    selectedHotel: null,
    selectedTransport: null,
  });

  // Datos del servidor
  const [flights, setFlights] = useState([]);
  const [hotels, setHotels] = useState([]);
  const [transports, setTransports] = useState([]);

  // Verificar autenticación al montar
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('❌ No hay token - Mostrando modal de autenticación');
      setShowAuthModal(true);
      return;
    }

    // Intentar recuperar datos guardados
    const savedBooking = bookingStorage.load();
    if (savedBooking) {
      console.log('✅ Recuperando reserva guardada:', savedBooking);
      setBookingData(savedBooking);
      
      // Si tiene vuelo seleccionado, ir directo al paso correspondiente
      if (savedBooking.selectedFlight) {
        setCurrentStep(2); // Hoteles
      }
    } else if (location.state?.searchData) {
      // Nuevos datos de búsqueda
      setBookingData({ ...bookingData, searchData: location.state.searchData });
      loadFlights(location.state.searchData);
    } else {
      // No hay datos - redirigir a búsqueda
      navigate('/');
    }
  }, []);

  const loadFlights = async (searchData) => {
    setLoading(true);
    setError('');

    try {
      const params = new URLSearchParams({
        origin: searchData.origin,
        destination: searchData.destination,
        departure: searchData.departureDate,
        adults: searchData.adults || 1,
        max: 10,
      });

      if (searchData.returnDate) {
        params.append('returnDate', searchData.returnDate);
      }

      const response = await fetch(`http://localhost:9090/flights/search?${params}`);
      
      if (!response.ok) throw new Error('Error al buscar vuelos');

      const data = await response.json();
      
      if (data.status === 'SUCCESS' && data.data) {
        setFlights(data.data);
        console.log('✅ Vuelos cargados:', data.data.length);
      } else {
        setError('No se encontraron vuelos disponibles');
      }
    } catch (err) {
      console.error('Error:', err);
      setError('Error al cargar los vuelos');
    } finally {
      setLoading(false);
    }
  };

  const loadHotels = async (destination) => {
    setLoading(true);
    setError('');

    try {
      const response = await fetch(`http://localhost:9090/hotels/search?cityCode=${destination}&max=10`);
      
      if (!response.ok) throw new Error('Error al buscar hoteles');

      const data = await response.json();
      
      if (data.status === 'SUCCESS' && data.data) {
        setHotels(data.data);
        console.log('✅ Hoteles cargados:', data.data.length);
      } else {
        setHotels([]);
        console.log('⚠️ No hay hoteles disponibles');
      }
    } catch (err) {
      console.error('Error:', err);
      setError('Error al cargar hoteles');
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  const loadTransports = async (searchData) => {
    setLoading(true);
    setError('');

    try {
      const params = new URLSearchParams({
        airportCode: searchData.origin,
        cityName: searchData.destinationName || searchData.destination,
        countryCode: 'ES', // Esto debería venir de los datos de búsqueda
        dateTime: `${searchData.departureDate}T10:00:00`,
        passengers: searchData.adults || 1,
      });

      const response = await fetch(`http://localhost:9090/api/transporte/search-transfers?${params}`);
      
      if (!response.ok) throw new Error('Error al buscar transportes');

      const data = await response.json();
      
      if (data.status === 'SUCCESS' && data.data) {
        setTransports(data.data);
        console.log('✅ Transportes cargados:', data.data.length);
      } else {
        setTransports([]);
        console.log('⚠️ No hay transportes disponibles');
      }
    } catch (err) {
      console.error('Error:', err);
      setError('Error al cargar transportes');
      setTransports([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectFlight = (flight) => {
    const updatedData = {
      ...bookingData,
      selectedFlight: flight,
    };
    setBookingData(updatedData);
    bookingStorage.save(updatedData);
    console.log('✅ Vuelo seleccionado y guardado');
  };

  const handleSelectHotel = (hotel) => {
    const updatedData = {
      ...bookingData,
      selectedHotel: hotel,
    };
    setBookingData(updatedData);
    bookingStorage.save(updatedData);
    console.log('✅ Hotel seleccionado y guardado');
  };

  const handleSelectTransport = (transport) => {
    const updatedData = {
      ...bookingData,
      selectedTransport: transport,
    };
    setBookingData(updatedData);
    bookingStorage.save(updatedData);
    console.log('✅ Transporte seleccionado y guardado');
  };

  const handleNextStep = () => {
    if (currentStep === 1 && !bookingData.selectedFlight) {
      setError('Por favor selecciona un vuelo');
      return;
    }

    if (currentStep === 1) {
      loadHotels(bookingData.searchData.destination);
    } else if (currentStep === 2) {
      loadTransports(bookingData.searchData);
    }

    setCurrentStep(currentStep + 1);
    setError('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handlePreviousStep = () => {
    setCurrentStep(currentStep - 1);
    setError('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handlePaymentSubmit = async (paymentData) => {
    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        setShowAuthModal(true);
        return;
      }

      console.log('💳 Procesando pago completo...');

      // Preparar datos para el backend
      const completeBookingData = {
        flightData: bookingData.selectedFlight,
        searchData: bookingData.searchData,
        hotelId: bookingData.selectedHotel?.hotelId || null,
        transportId: bookingData.selectedTransport?.id || null,
        paymentData: {
          metodoPago: paymentData.metodoPago,
          ...paymentData,
        },
      };

      const response = await fetch('http://localhost:9090/api/bookings/complete', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(completeBookingData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al procesar la reserva');
      }

      const result = await response.json();
      console.log('✅ Reserva completada:', result);

      // Limpiar datos guardados
      bookingStorage.clear();

      // Mostrar mensaje de éxito y redirigir
      alert('¡Reserva confirmada! Se ha enviado un email con los detalles.');
      
      // Redirigir al perfil con estado de éxito
      navigate('/profile', { 
        state: { 
          bookingSuccess: true,
          confirmationNumber: result.confirmationNumber,
        } 
      });

    } catch (err) {
      console.error('Error al procesar pago:', err);
      setError(err.message || 'Error al procesar el pago. Por favor intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  if (showAuthModal) {
    return (
      <BookingModal
        isOpen={showAuthModal}
        onClose={() => {
          setShowAuthModal(false);
          navigate('/');
        }}
        bookingData={bookingData}
      />
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light py-8">
      <div className="container mx-auto px-4 max-w-7xl">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={currentStep > 1 ? handlePreviousStep : () => navigate('/')}
            className="flex items-center space-x-2 text-cosmic-base hover:text-cosmic-dark transition-colors font-medium"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>{currentStep > 1 ? 'Paso anterior' : 'Volver a búsqueda'}</span>
          </button>

          <div className="text-right">
            <p className="text-sm text-gray-600">Paso {currentStep} de 4</p>
          </div>
        </div>

        {/* Wizard */}
        <BookingWizard currentStep={currentStep} onStepChange={setCurrentStep} />

        {/* Error Message */}
        {error && (
          <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Step 1: Flights */}
            {currentStep === 1 && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Selecciona tu vuelo
                </h2>

                {loading ? (
                  <div className="flex items-center justify-center py-12">
                    <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
                    <span className="ml-3 text-gray-600">Cargando vuelos...</span>
                  </div>
                ) : flights.length === 0 ? (
                  <div className="bg-white rounded-xl shadow-md p-8 text-center">
                    <p className="text-gray-600">No se encontraron vuelos disponibles</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {flights.map((flight, index) => (
                      <FlightCard
                        key={index}
                        flight={flight}
                        onSelect={handleSelectFlight}
                        isSelected={bookingData.selectedFlight?.id === flight.id}
                      />
                    ))}
                  </div>
                )}

                {bookingData.selectedFlight && (
                  <button
                    onClick={handleNextStep}
                    className="mt-6 w-full bg-flame-base hover:bg-flame-dark text-white font-bold py-4 px-6 rounded-lg transition-colors flex items-center justify-center space-x-2"
                  >
                    <span>Continuar a Hoteles</span>
                    <Check className="w-5 h-5" />
                  </button>
                )}
              </div>
            )}

            {/* Step 2: Hotels */}
            {currentStep === 2 && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Selecciona tu hotel (opcional)
                </h2>

                {loading ? (
                  <div className="flex items-center justify-center py-12">
                    <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
                    <span className="ml-3 text-gray-600">Cargando hoteles...</span>
                  </div>
                ) : hotels.length === 0 ? (
                  <div className="bg-white rounded-xl shadow-md p-8 text-center">
                    <p className="text-gray-600 mb-4">No hay hoteles disponibles en este momento</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {hotels.map((hotel, index) => (
                      <HotelCard
                        key={index}
                        hotel={hotel}
                        onSelect={handleSelectHotel}
                        isSelected={bookingData.selectedHotel?.hotelId === hotel.hotelId}
                      />
                    ))}
                  </div>
                )}

                <button
                  onClick={handleNextStep}
                  className="mt-6 w-full bg-flame-base hover:bg-flame-dark text-white font-bold py-4 px-6 rounded-lg transition-colors flex items-center justify-center space-x-2"
                >
                  <span>Continuar a Transporte</span>
                  <Check className="w-5 h-5" />
                </button>
              </div>
            )}

            {/* Step 3: Transport */}
            {currentStep === 3 && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Selecciona tu transporte (opcional)
                </h2>

                {loading ? (
                  <div className="flex items-center justify-center py-12">
                    <Loader className="w-8 h-8 animate-spin text-cosmic-base" />
                    <span className="ml-3 text-gray-600">Cargando transportes...</span>
                  </div>
                ) : transports.length === 0 ? (
                  <div className="bg-white rounded-xl shadow-md p-8 text-center">
                    <p className="text-gray-600 mb-4">No hay transportes disponibles en este momento</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {transports.map((transport, index) => (
                      <TransportCard
                        key={index}
                        transport={transport}
                        onSelect={handleSelectTransport}
                        isSelected={bookingData.selectedTransport?.id === transport.id}
                      />
                    ))}
                  </div>
                )}

                <button
                  onClick={handleNextStep}
                  className="mt-6 w-full bg-flame-base hover:bg-flame-dark text-white font-bold py-4 px-6 rounded-lg transition-colors flex items-center justify-center space-x-2"
                >
                  <span>Continuar a Pago</span>
                  <Check className="w-5 h-5" />
                </button>
              </div>
            )}

            {/* Step 4: Payment */}
            {currentStep === 4 && (
              <div>
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6">
                  Información de Pago
                </h2>

                <PaymentForm
                  onSubmit={handlePaymentSubmit}
                  loading={loading}
                />
              </div>
            )}
          </div>

          {/* Sidebar - Summary */}
          <div className="lg:col-span-1">
            <BookingSummary
              flight={bookingData.selectedFlight}
              hotel={bookingData.selectedHotel}
              transport={bookingData.selectedTransport}
              searchData={bookingData.searchData}
            />
          </div>
        </div>
      </div>
    </div>
  );
}