import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { bookingStorage } from '../../utils/bookingStorage';
import BookingWizard from '../../components/booking/BookingWizard';
import FlightCard from '../../components/booking/FlightCard';
import HotelCard from '../../components/booking/HotelCard';
import TransportCard from '../../components/booking/TransportCard';
import BookingSummary from '../../components/booking/BookingSummary';
import PaymentForm from '../../components/booking/PaymentForm';
import {
  Plane,
  Hotel,
  Car,
  CreditCard,
  ArrowLeft,
  AlertCircle,
  CheckCircle2,
  Loader2,
  Home,
} from 'lucide-react';

export default function BookingFlow() {
  const navigate = useNavigate();
  const location = useLocation();

  // Estados principales
  const [loading, setLoading] = useState(true);
  const [currentStep, setCurrentStep] = useState(1);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Datos de reserva
  const [searchData, setSearchData] = useState(null);
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [selectedHotel, setSelectedHotel] = useState(null);
  const [selectedTransport, setSelectedTransport] = useState(null);

  // Listas de opciones
  const [availableHotels, setAvailableHotels] = useState([]);
  const [availableTransports, setAvailableTransports] = useState([]);
  const [loadingHotels, setLoadingHotels] = useState(false);
  const [loadingTransports, setLoadingTransports] = useState(false);

  // ✅ Cargar datos al montar el componente
  useEffect(() => {
    loadBookingData();
  }, []);

  // ✅ Función principal para cargar datos de reserva
  const loadBookingData = async () => {
    try {
      console.log('📥 Cargando datos de reserva...');

      // Verificar autenticación
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('❌ No hay sesión activa, redirigiendo a login');
        navigate('/login', { state: { from: 'booking' } });
        return;
      }

      // 1. Intentar obtener datos del location.state (prioridad)
      if (location.state?.selectedFlight) {
        console.log('✅ Datos recibidos desde location.state');
        setSelectedFlight(location.state.selectedFlight);
        setSearchData(location.state.searchData);
        setCurrentStep(location.state.currentStep || 1);
        
        // Guardar en localStorage por seguridad
        bookingStorage.save({
          selectedFlight: location.state.selectedFlight,
          searchData: location.state.searchData,
          currentStep: location.state.currentStep || 1,
        });
      } 
      // 2. Si no hay en location.state, buscar en localStorage
      else {
        const savedBooking = bookingStorage.get();
        
        if (!savedBooking) {
          console.log('⚠️ No hay reserva pendiente');
          setError('No se encontró una reserva pendiente. Por favor, realiza una nueva búsqueda.');
          setTimeout(() => navigate('/'), 3000);
          return;
        }

        console.log('✅ Datos recuperados desde localStorage:', savedBooking);
        setSelectedFlight(savedBooking.selectedFlight);
        setSelectedHotel(savedBooking.selectedHotel || null);
        setSelectedTransport(savedBooking.selectedTransport || null);
        setSearchData(savedBooking.searchData);
        setCurrentStep(savedBooking.currentStep || 1);
      }

      setLoading(false);
    } catch (err) {
      console.error('❌ Error al cargar datos de reserva:', err);
      setError('Error al cargar los datos de la reserva');
      setLoading(false);
    }
  };

  // ✅ Cargar hoteles disponibles
  const loadHotels = async () => {
    if (!searchData?.destination) {
      setError('No se puede buscar hoteles sin un destino');
      return;
    }

    setLoadingHotels(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:9090/hotels/search?cityCode=${searchData.destination}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) throw new Error('Error al buscar hoteles');

      const data = await response.json();
      setAvailableHotels(Array.isArray(data.data) ? data.data : []);
      console.log('🏨 Hoteles cargados:', data.data?.length || 0);
    } catch (err) {
      console.error('Error al cargar hoteles:', err);
      setError('No se pudieron cargar los hoteles disponibles');
      setAvailableHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  // ✅ Cargar transportes disponibles
  const loadTransports = async () => {
    if (!searchData?.destination) {
      setError('No se puede buscar transporte sin un destino');
      return;
    }

    setLoadingTransports(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:9090/transport/search?destination=${searchData.destination}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) throw new Error('Error al buscar transporte');

      const data = await response.json();
      setAvailableTransports(Array.isArray(data.data) ? data.data : []);
      console.log('🚗 Transportes cargados:', data.data?.length || 0);
    } catch (err) {
      console.error('Error al cargar transportes:', err);
      setError('No se pudieron cargar las opciones de transporte');
      setAvailableTransports([]);
    } finally {
      setLoadingTransports(false);
    }
  };

  // ✅ Seleccionar hotel
  const handleSelectHotel = (hotel) => {
    console.log('🏨 Hotel seleccionado:', hotel);
    setSelectedHotel(hotel);
    
    // Actualizar localStorage
    const currentBooking = bookingStorage.get() || {};
    bookingStorage.save({
      ...currentBooking,
      selectedHotel: hotel,
    });

    setSuccess('✅ Hotel agregado a tu reserva');
    setTimeout(() => setSuccess(''), 3000);
  };

  // ✅ Seleccionar transporte
  const handleSelectTransport = (transport) => {
    console.log('🚗 Transporte seleccionado:', transport);
    setSelectedTransport(transport);
    
    // Actualizar localStorage
    const currentBooking = bookingStorage.get() || {};
    bookingStorage.save({
      ...currentBooking,
      selectedTransport: transport,
    });

    setSuccess('✅ Transporte agregado a tu reserva');
    setTimeout(() => setSuccess(''), 3000);
  };

  // ✅ Remover hotel
  const handleRemoveHotel = () => {
    setSelectedHotel(null);
    const currentBooking = bookingStorage.get() || {};
    delete currentBooking.selectedHotel;
    bookingStorage.save(currentBooking);
  };

  // ✅ Remover transporte
  const handleRemoveTransport = () => {
    setSelectedTransport(null);
    const currentBooking = bookingStorage.get() || {};
    delete currentBooking.selectedTransport;
    bookingStorage.save(currentBooking);
  };

  // ✅ Ir al paso de pago
  const handleProceedToPayment = () => {
    if (!selectedFlight) {
      setError('Debes seleccionar un vuelo para continuar');
      return;
    }

    setCurrentStep(4);
    bookingStorage.updateStep(4);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // ✅ Procesar pago
  const handlePayment = async (paymentData) => {
    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const usuarioId = localStorage.getItem('usuarioId');

      // Construir objeto de reserva
      const reservaData = {
        usuarioId: parseInt(usuarioId),
        fechaReserva: new Date().toISOString(),
        estado: 'pendiente',
        // Datos del vuelo
        viajeId: selectedFlight.id,
        // Datos opcionales
        alojamientoId: selectedHotel?.hotelId || null,
        transporteId: selectedTransport?.id || null,
        // Datos del pasajero y pago
        pasajero: {
          primerNombre: paymentData.primerNombre,
          primerApellido: paymentData.primerApellido,
          email: paymentData.email,
          telefono: paymentData.telefono,
          documento: paymentData.documento,
        },
        pago: {
          metodoPago: paymentData.metodoPago,
          monto: calculateTotal(),
          moneda: selectedFlight.price?.currency || 'USD',
          estado: 'pendiente',
          // Datos específicos del método de pago
          ...(paymentData.metodoPago === 'Tarjeta' && {
            numeroTarjeta: paymentData.numeroTarjeta.slice(-4), // Solo últimos 4 dígitos
            nombreTitular: paymentData.nombreTitular,
          }),
          ...(paymentData.metodoPago === 'PSE' && {
            banco: paymentData.banco,
            tipoPersona: paymentData.tipoPersona,
          }),
          ...(paymentData.metodoPago === 'Nequi' && {
            numeroNequi: paymentData.numeroNequi,
          }),
        },
      };

      console.log('💳 Procesando reserva:', reservaData);

      const response = await fetch('http://localhost:9090/api/reservas', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(reservaData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al procesar la reserva');
      }

      const result = await response.json();
      console.log('✅ Reserva creada exitosamente:', result);

      // Limpiar datos temporales
      bookingStorage.clear();

      // Mostrar éxito y redirigir
      setSuccess('🎉 ¡Reserva realizada exitosamente!');
      
      setTimeout(() => {
        navigate('/UserProfile', { 
          state: { 
            showReservas: true,
            newReservaId: result.id 
          } 
        });
      }, 2000);

    } catch (err) {
      console.error('❌ Error al procesar pago:', err);
      setError(`Error al procesar la reserva: ${err.message}`);
      setLoading(false);
    }
  };

  // ✅ Calcular total
  const calculateTotal = () => {
    let total = 0;
    
    if (selectedFlight?.price?.total) {
      total += parseFloat(selectedFlight.price.total);
    }
    
    if (selectedHotel?.price) {
      total += parseFloat(selectedHotel.price);
    }
    
    if (selectedTransport?.precio) {
      total += parseFloat(selectedTransport.precio);
    }
    
    return total.toFixed(2);
  };

  // ✅ Loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-16 h-16 text-cosmic-base animate-spin mx-auto mb-4" />
          <p className="text-gray-700 text-lg font-medium">Cargando tu reserva...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-astronaut-light to-cosmic-light">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-40">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <button
              onClick={() => navigate('/')}
              className="flex items-center gap-2 text-cosmic-base hover:text-cosmic-dark transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
              <span className="font-medium">Volver al inicio</span>
            </button>
            <h1 className="text-xl md:text-2xl font-bold text-astronaut-dark">
              Completa tu Reserva
            </h1>
            <button
              onClick={() => navigate('/UserProfile')}
              className="flex items-center gap-2 px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors"
            >
              <Home className="w-5 h-5" />
              <span className="hidden md:inline">Mi Perfil</span>
            </button>
          </div>
        </div>
      </header>

      {/* Wizard de progreso */}
      <div className="container mx-auto px-4 pt-6">
        <BookingWizard currentStep={currentStep} onStepChange={setCurrentStep} />
      </div>

      {/* Mensajes */}
      <div className="container mx-auto px-4 mt-6">
        {error && (
          <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-lg flex items-start gap-3 mb-6 animate-fade-in">
            <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-red-800 font-medium">{error}</p>
            </div>
            <button onClick={() => setError('')} className="text-red-500 hover:text-red-700">
              <ArrowLeft className="w-5 h-5" />
            </button>
          </div>
        )}

        {success && (
          <div className="bg-green-50 border-l-4 border-green-500 p-4 rounded-lg flex items-start gap-3 mb-6 animate-fade-in">
            <CheckCircle2 className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-green-800 font-medium">{success}</p>
            </div>
            <button onClick={() => setSuccess('')} className="text-green-500 hover:text-green-700">
              <ArrowLeft className="w-5 h-5" />
            </button>
          </div>
        )}
      </div>

      {/* Contenido principal */}
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Columna izquierda - Selección */}
          <div className="lg:col-span-2 space-y-6">
            {/* Paso 1-3: Selección de servicios */}
            {currentStep < 4 && (
              <>
                {/* Vuelo seleccionado */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-astronaut-dark flex items-center gap-2">
                      <Plane className="w-6 h-6 text-cosmic-base" />
                      Tu Vuelo
                    </h2>
                    <span className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-semibold">
                      ✓ Seleccionado
                    </span>
                  </div>
                  {selectedFlight && (
                    <FlightCard
                      flight={selectedFlight}
                      onSelect={() => {}}
                      isSelected={true}
                    />
                  )}
                </div>

                {/* Hotel (opcional) */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-astronaut-dark flex items-center gap-2">
                      <Hotel className="w-6 h-6 text-cosmic-base" />
                      Hotel
                      <span className="text-sm font-normal text-gray-500">(Opcional)</span>
                    </h2>
                    {selectedHotel ? (
                      <button
                        onClick={handleRemoveHotel}
                        className="text-red-600 hover:text-red-700 text-sm font-medium"
                      >
                        Remover
                      </button>
                    ) : (
                      <button
                        onClick={loadHotels}
                        disabled={loadingHotels}
                        className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors disabled:opacity-50"
                      >
                        {loadingHotels ? 'Buscando...' : 'Buscar Hoteles'}
                      </button>
                    )}
                  </div>

                  {selectedHotel ? (
                    <HotelCard
                      hotel={selectedHotel}
                      onSelect={() => {}}
                      isSelected={true}
                    />
                  ) : availableHotels.length > 0 ? (
                    <div className="space-y-4 max-h-96 overflow-y-auto">
                      {availableHotels.map((hotel, index) => (
                        <HotelCard
                          key={hotel.hotelId || index}
                          hotel={hotel}
                          onSelect={handleSelectHotel}
                          isSelected={false}
                        />
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-500 text-center py-8">
                      Haz clic en "Buscar Hoteles" para ver opciones disponibles
                    </p>
                  )}
                </div>

                {/* Transporte (opcional) */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-astronaut-dark flex items-center gap-2">
                      <Car className="w-6 h-6 text-cosmic-base" />
                      Transporte
                      <span className="text-sm font-normal text-gray-500">(Opcional)</span>
                    </h2>
                    {selectedTransport ? (
                      <button
                        onClick={handleRemoveTransport}
                        className="text-red-600 hover:text-red-700 text-sm font-medium"
                      >
                        Remover
                      </button>
                    ) : (
                      <button
                        onClick={loadTransports}
                        disabled={loadingTransports}
                        className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark transition-colors disabled:opacity-50"
                      >
                        {loadingTransports ? 'Buscando...' : 'Buscar Transporte'}
                      </button>
                    )}
                  </div>

                  {selectedTransport ? (
                    <TransportCard
                      transport={selectedTransport}
                      onSelect={() => {}}
                      isSelected={true}
                    />
                  ) : availableTransports.length > 0 ? (
                    <div className="space-y-4 max-h-96 overflow-y-auto">
                      {availableTransports.map((transport, index) => (
                        <TransportCard
                          key={transport.id || index}
                          transport={transport}
                          onSelect={handleSelectTransport}
                          isSelected={false}
                        />
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-500 text-center py-8">
                      Haz clic en "Buscar Transporte" para ver opciones disponibles
                    </p>
                  )}
                </div>

                {/* Botón continuar al pago */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <button
                    onClick={handleProceedToPayment}
                    className="w-full bg-gradient-to-r from-flame-base to-flame-dark hover:from-flame-dark hover:to-cosmic-dark text-white font-bold py-4 px-6 rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
                  >
                    <CreditCard className="w-6 h-6" />
                    Continuar al Pago
                  </button>
                </div>
              </>
            )}

            {/* Paso 4: Pago */}
            {currentStep === 4 && (
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6 flex items-center gap-2">
                  <CreditCard className="w-6 h-6 text-cosmic-base" />
                  Información de Pago
                </h2>
                <PaymentForm onSubmit={handlePayment} loading={loading} />
                
                <button
                  onClick={() => setCurrentStep(3)}
                  className="mt-6 w-full bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-3 px-6 rounded-lg transition-colors flex items-center justify-center gap-2"
                >
                  <ArrowLeft className="w-5 h-5" />
                  Volver a Servicios
                </button>
              </div>
            )}
          </div>

          {/* Columna derecha - Resumen */}
          <div className="lg:col-span-1">
            <BookingSummary
              flight={selectedFlight}
              hotel={selectedHotel}
              transport={selectedTransport}
              searchData={searchData}
            />
          </div>
        </div>
      </div>
    </div>
  );
}