import { Plane, Building2, Car, Calendar, Users, DollarSign } from 'lucide-react';

export default function BookingSummary({ flight, hotel, transport, searchData }) {
  const calculateTotal = () => {
    let total = 0;
    
    if (flight?.price?.total) {
      total += parseFloat(flight.price.total);
    }
    
    // Nota: Los hoteles de Amadeus no siempre retornan precio en la búsqueda inicial
    // En un caso real, necesitarías hacer una búsqueda de disponibilidad con precios
    
    if (transport?.precio) {
      total += parseFloat(transport.precio);
    }
    
    return total.toFixed(2);
  };

  const currency = flight?.price?.currency || 'USD';

  return (
    <div className="bg-white rounded-xl shadow-lg p-6 sticky top-8">
      <h3 className="text-2xl font-bold text-astronaut-dark mb-6">
        Resumen de tu reserva
      </h3>

      <div className="space-y-6">
        {/* Vuelo */}
        {flight && (
          <div className="border-b pb-4">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-10 h-10 bg-astronaut-light rounded-full flex items-center justify-center">
                <Plane className="w-5 h-5 text-astronaut-base" />
              </div>
              <div className="flex-1">
                <h4 className="font-semibold text-gray-800">Vuelo</h4>
                <p className="text-sm text-gray-500">
                  {flight.itineraries?.[0]?.segments?.[0]?.departure?.iataCode} → {' '}
                  {flight.itineraries?.[0]?.segments?.slice(-1)[0]?.arrival?.iataCode}
                </p>
              </div>
              <div className="text-right">
                <p className="font-bold text-cosmic-base">
                  {flight.price?.total} {currency}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-4 text-sm text-gray-600">
              <div className="flex items-center space-x-1">
                <Calendar className="w-4 h-4" />
                <span>{searchData.departureDate}</span>
              </div>
              <div className="flex items-center space-x-1">
                <Users className="w-4 h-4" />
                <span>{searchData.adults} pasajero(s)</span>
              </div>
            </div>
          </div>
        )}

        {/* Hotel */}
        {hotel && (
          <div className="border-b pb-4">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-10 h-10 bg-cosmic-light rounded-full flex items-center justify-center">
                <Building2 className="w-5 h-5 text-cosmic-base" />
              </div>
              <div className="flex-1">
                <h4 className="font-semibold text-gray-800">Hotel</h4>
                <p className="text-sm text-gray-500">
                  {hotel.name || 'Hotel seleccionado'}
                </p>
              </div>
              <div className="text-right">
                <p className="text-sm text-gray-500">Incluido</p>
              </div>
            </div>
          </div>
        )}

        {/* Transporte */}
        {transport && (
          <div className="border-b pb-4">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-10 h-10 bg-flame-light rounded-full flex items-center justify-center">
                <Car className="w-5 h-5 text-flame-base" />
              </div>
              <div className="flex-1">
                <h4 className="font-semibold text-gray-800">Transporte</h4>
                <p className="text-sm text-gray-500">
                  {transport.vehiculoTipo || 'Transfer'}
                </p>
              </div>
              <div className="text-right">
                <p className="font-bold text-cosmic-base">
                  {transport.precio} {transport.currency || 'USD'}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Total */}
        <div className="pt-4">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">Subtotal</span>
            <span className="font-medium">{calculateTotal()} {currency}</span>
          </div>
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">Impuestos y tasas</span>
            <span className="font-medium">Incluido</span>
          </div>
          <div className="border-t pt-4 mt-4">
            <div className="flex items-center justify-between">
              <span className="text-xl font-bold text-astronaut-dark">Total</span>
              <span className="text-2xl font-bold text-cosmic-base">
                {calculateTotal()} {currency}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="mt-6 p-4 bg-astronaut-light rounded-lg">
        <p className="text-sm text-gray-600 text-center">
          🔒 Pago seguro y protegido
        </p>
      </div>
    </div>
  );
}