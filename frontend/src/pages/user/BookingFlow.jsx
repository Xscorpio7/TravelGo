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

  const [loading, setLoading] = useState(true);
  const [currentStep, setCurrentStep] = useState(1);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [searchData, setSearchData] = useState(null);
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [selectedHotel, setSelectedHotel] = useState(null);
  const [selectedTransport, setSelectedTransport] = useState(null);

  const [availableHotels, setAvailableHotels] = useState([]);
  const [availableTransports, setAvailableTransports] = useState([]);
  const [loadingHotels, setLoadingHotels] = useState(false);
  const [loadingTransports, setLoadingTransports] = useState(false);

  useEffect(() => {
    loadBookingData();
  }, []);

  const loadBookingData = async () => {
    try {
      console.log('📥 Cargando datos de reserva...');

      const token = localStorage.getItem('token');
      if (!token) {
        console.log('❌ No hay sesión activa');
        navigate('/login', { state: { from: 'booking' } });
        return;
      }

      if (location.state?.selectedFlight) {
        console.log('✅ Datos desde location.state');
        setSelectedFlight(location.state.selectedFlight);
        setSearchData(location.state.searchData);
        setCurrentStep(location.state.currentStep || 1);
        
        bookingStorage.save({
          selectedFlight: location.state.selectedFlight,
          searchData: location.state.searchData,
          currentStep: location.state.currentStep || 1,
        });
      } else {
        const savedBooking = bookingStorage.get();
        
        if (!savedBooking) {
          console.log('⚠️ No hay reserva pendiente');
          setError('No se encontró una reserva pendiente.');
          setTimeout(() => navigate('/'), 3000);
          return;
        }

        console.log('✅ Datos desde localStorage');
        setSelectedFlight(savedBooking.selectedFlight);
        setSelectedHotel(savedBooking.selectedHotel || null);
        setSelectedTransport(savedBooking.selectedTransport || null);
        setSearchData(savedBooking.searchData);
        setCurrentStep(savedBooking.currentStep || 1);
      }

      setLoading(false);
    } catch (err) {
      console.error('❌ Error:', err);
      setError('Error al cargar los datos');
      setLoading(false);
    }
  };

  const loadHotels = async () => {
    if (!searchData?.destination) {
      setError('No se puede buscar hoteles sin destino');
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
    } catch (err) {
      console.error('Error:', err);
      setError('No se pudieron cargar los hoteles');
      setAvailableHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  const loadTransports = async () => {
    if (!searchData?.destination) {
      setError('No se puede buscar transporte sin destino');
      return;
    }

    setLoadingTransports(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:9090/api/transporte/por-tipo?tipo=Transfer`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) throw new Error('Error al buscar transporte');

      const data = await response.json();
      setAvailableTransports(Array.isArray(data.data) ? data.data : []);
    } catch (err) {
      console.error('Error:', err);
      setError('No se pudieron cargar las opciones de transporte');
      setAvailableTransports([]);
    } finally {
      setLoadingTransports(false);
    }
  };

  const handleSelectHotel = (hotel) => {
    setSelectedHotel(hotel);
    const currentBooking = bookingStorage.get() || {};
    bookingStorage.save({ ...currentBooking, selectedHotel: hotel });
    setSuccess('✅ Hotel agregado');
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleSelectTransport = (transport) => {
    setSelectedTransport(transport);
    const currentBooking = bookingStorage.get() || {};
    bookingStorage.save({ ...currentBooking, selectedTransport: transport });
    setSuccess('✅ Transporte agregado');
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleRemoveHotel = () => {
    setSelectedHotel(null);
    const currentBooking = bookingStorage.get() || {};
    delete currentBooking.selectedHotel;
    bookingStorage.save(currentBooking);
  };

  const handleRemoveTransport = () => {
    setSelectedTransport(null);
    const currentBooking = bookingStorage.get() || {};
    delete currentBooking.selectedTransport;
    bookingStorage.save(currentBooking);
  };

  const handleProceedToPayment = () => {
    if (!selectedFlight) {
      setError('Debes seleccionar un vuelo');
      return;
    }

    setCurrentStep(4);
    bookingStorage.updateStep(4);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // ✅ CORRECCIÓN PRINCIPAL: Proceso de pago simplificado
  const handlePayment = async (paymentData) => {
    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const usuarioId = parseInt(localStorage.getItem('usuarioId'));

      console.log('💳 Iniciando proceso de reserva...');

      // PASO 1: Crear el viaje
      let viajeId = null;
      
      if (selectedFlight) {
        const viajePayload = {
          flightOfferId: selectedFlight.id,
          origin: searchData.origin,
          destinationCode: searchData.destination,
          departureDate: searchData.departureDate,
          returnDate: searchData.returnDate || null,
          precio: parseFloat(selectedFlight.price?.total || 0),
          currency: selectedFlight.price?.currency || 'USD',
          airline: selectedFlight.itineraries?.[0]?.segments?.[0]?.carrierCode,
          bookableSeats: selectedFlight.numberOfBookableSeats,
          tipoViaje: 'vuelo',
          titulo: `${searchData.origin} → ${searchData.destination}`,
        };

        console.log('📦 Guardando viaje:', viajePayload);

        const viajeResponse = await fetch('http://localhost:9090/api/viajes', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(viajePayload),
        });

        if (!viajeResponse.ok) {
          const errorText = await viajeResponse.text();
          console.error('❌ Error al guardar viaje:', errorText);
          throw new Error('Error al guardar el viaje');
        }

        const viajeData = await viajeResponse.json();
        viajeId = viajeData.data?.id || viajeData.id;
        console.log('✅ Viaje guardado con ID:', viajeId);
      }

      // PASO 2: Crear la reserva
      // ✅ IMPORTANTE: Los campos deben coincidir EXACTAMENTE con la BD
      const reservaPayload = {
        usuarioId: usuarioId,        // → usuario_id en BD
        viajeId: viajeId,            // → viaje_id en BD
        alojamientoId: selectedHotel?.id || null,      // → alojamiento_id en BD
        transporteId: selectedTransport?.id || null,    // → transporte_id en BD
        estado: 'pendiente',          // → estado en BD
      };

      console.log('📦 Creando reserva:', reservaPayload);

      const reservaResponse = await fetch('http://localhost:9090/api/reservas', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(reservaPayload),
      });

      if (!reservaResponse.ok) {
        const errorText = await reservaResponse.text();
        console.error('❌ Error del servidor:', errorText);
        throw new Error('Error al crear la reserva');
      }

      const reservaData = await reservaResponse.json();
      const reservaId = reservaData.data?.id || reservaData.id;
      console.log('✅ Reserva creada con ID:', reservaId);

      // PASO 3: Crear el pago
      const pagoPayload = {
        reserva: { id: reservaId },
        metodoPago: paymentData.metodoPago,
        monto: parseFloat(calculateTotal()),
        estado: 'pagado',
        fechaPago: new Date().toISOString().split('T')[0],
      };

      console.log('💰 Creando pago:', pagoPayload);

      const pagoResponse = await fetch('http://localhost:9090/api/pago', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(pagoPayload),
      });

      if (!pagoResponse.ok) {
        console.warn('⚠️ Error al crear pago, pero reserva fue exitosa');
      } else {
        console.log('✅ Pago registrado');
      }

      // PASO 4: Limpiar y redirigir
      bookingStorage.clear();
      setSuccess('🎉 ¡Reserva realizada exitosamente!');
      
      setTimeout(() => {
        navigate('/UserProfile', { 
          state: { 
            activeTab: 'reservas',
            newReservaId: reservaId,
            showSuccess: true,
          } 
        });
      }, 2000);

    } catch (err) {
      console.error('❌ Error:', err);
      setError(`Error: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

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

      {/* Wizard */}
      <div className="container mx-auto px-4 pt-6">
        <BookingWizard currentStep={currentStep} onStepChange={setCurrentStep} />
      </div>

      {/* Mensajes */}
      <div className="container mx-auto px-4 mt-6">
        {error && (
          <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-lg flex items-start gap-3 mb-6">
            <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            <p className="text-red-800 font-medium">{error}</p>
            <button onClick={() => setError('')} className="ml-auto text-red-500">×</button>
          </div>
        )}

        {success && (
          <div className="bg-green-50 border-l-4 border-green-500 p-4 rounded-lg flex items-start gap-3 mb-6">
            <CheckCircle2 className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" />
            <p className="text-green-800 font-medium">{success}</p>
            <button onClick={() => setSuccess('')} className="ml-auto text-green-500">×</button>
          </div>
        )}
      </div>

      {/* Contenido */}
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Columna principal */}
          <div className="lg:col-span-2 space-y-6">
            {currentStep < 4 && (
              <>
                {/* Vuelo */}
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
                    <FlightCard flight={selectedFlight} onSelect={() => {}} isSelected={true} />
                  )}
                </div>

                {/* Hotel */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-astronaut-dark flex items-center gap-2">
                      <Hotel className="w-6 h-6 text-cosmic-base" />
                      Hotel <span className="text-sm font-normal text-gray-500">(Opcional)</span>
                    </h2>
                    {selectedHotel ? (
                      <button onClick={handleRemoveHotel} className="text-red-600 text-sm">
                        Remover
                      </button>
                    ) : (
                      <button
                        onClick={loadHotels}
                        disabled={loadingHotels}
                        className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark disabled:opacity-50"
                      >
                        {loadingHotels ? 'Buscando...' : 'Buscar Hoteles'}
                      </button>
                    )}
                  </div>
                  {selectedHotel ? (
                    <HotelCard hotel={selectedHotel} onSelect={() => {}} isSelected={true} />
                  ) : availableHotels.length > 0 ? (
                    <div className="space-y-4 max-h-96 overflow-y-auto">
                      {availableHotels.map((hotel, i) => (
                        <HotelCard key={i} hotel={hotel} onSelect={handleSelectHotel} isSelected={false} />
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-500 text-center py-8">
                      Haz clic en "Buscar Hoteles"
                    </p>
                  )}
                </div>

                {/* Transporte */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-astronaut-dark flex items-center gap-2">
                      <Car className="w-6 h-6 text-cosmic-base" />
                      Transporte <span className="text-sm font-normal text-gray-500">(Opcional)</span>
                    </h2>
                    {selectedTransport ? (
                      <button onClick={handleRemoveTransport} className="text-red-600 text-sm">
                        Remover
                      </button>
                    ) : (
                      <button
                        onClick={loadTransports}
                        disabled={loadingTransports}
                        className="px-4 py-2 bg-cosmic-base text-white rounded-lg hover:bg-cosmic-dark disabled:opacity-50"
                      >
                        {loadingTransports ? 'Buscando...' : 'Buscar Transporte'}
                      </button>
                    )}
                  </div>
                  {selectedTransport ? (
                    <TransportCard transport={selectedTransport} onSelect={() => {}} isSelected={true} />
                  ) : availableTransports.length > 0 ? (
                    <div className="space-y-4 max-h-96 overflow-y-auto">
                      {availableTransports.map((t, i) => (
                        <TransportCard key={i} transport={t} onSelect={handleSelectTransport} isSelected={false} />
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-500 text-center py-8">
                      Haz clic en "Buscar Transporte"
                    </p>
                  )}
                </div>

                <button
                  onClick={handleProceedToPayment}
                  className="w-full bg-gradient-to-r from-flame-base to-flame-dark text-white font-bold py-4 rounded-lg hover:scale-105 transition-transform flex items-center justify-center gap-2"
                >
                  <CreditCard className="w-6 h-6" />
                  Continuar al Pago
                </button>
              </>
            )}

            {currentStep === 4 && (
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-2xl font-bold text-astronaut-dark mb-6 flex items-center gap-2">
                  <CreditCard className="w-6 h-6 text-cosmic-base" />
                  Información de Pago
                </h2>
                <PaymentForm onSubmit={handlePayment} loading={loading} />
                
                <button
                  onClick={() => setCurrentStep(3)}
                  className="mt-6 w-full bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-3 rounded-lg flex items-center justify-center gap-2"
                >
                  <ArrowLeft className="w-5 h-5" />
                  Volver
                </button>
              </div>
            )}
          </div>

          {/* Resumen */}
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