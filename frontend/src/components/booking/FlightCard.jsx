import { Plane, Clock, Calendar, Users } from 'lucide-react';

export default function FlightCard({ flight, onSelect, isSelected }) {
  const formatDuration = (duration) => {
    if (!duration) return 'N/A';
    return duration.replace('PT', '').replace('H', 'h ').replace('M', 'm');
  };

  const formatDateTime = (dateTime) => {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('es-ES', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const segments = flight.itineraries?.[0]?.segments || [];
  const firstSegment = segments[0];
  const lastSegment = segments[segments.length - 1];

  return (
    <div
      className={`bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 cursor-pointer border-2 ${
        isSelected ? 'border-cosmic-base ring-2 ring-cosmic-light' : 'border-transparent'
      }`}
      onClick={() => onSelect(flight)}
    >
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
        {/* Flight Info */}
        <div className="flex-1 w-full">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-astronaut-light rounded-full flex items-center justify-center">
                <Plane className="w-6 h-6 text-astronaut-base" />
              </div>
              <div>
                <h3 className="font-bold text-lg text-astronaut-dark">
                  {firstSegment?.departure?.iataCode} → {lastSegment?.arrival?.iataCode}
                </h3>
                <p className="text-sm text-gray-500">
                  {firstSegment?.carrierCode} {firstSegment?.number}
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4 mb-4">
            <div className="flex items-center space-x-2">
              <Calendar className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Salida</p>
                <p className="text-sm font-medium">{formatDateTime(firstSegment?.departure?.at)}</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Clock className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Duración</p>
                <p className="text-sm font-medium">{formatDuration(flight.itineraries?.[0]?.duration)}</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Users className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Asientos</p>
                <p className="text-sm font-medium">{flight.numberOfBookableSeats || 'N/A'}</p>
              </div>
            </div>
          </div>

          {/* Segments */}
          <div className="flex items-center space-x-2 text-xs text-gray-500">
            <span>{segments.length > 1 ? `${segments.length} escalas` : 'Directo'}</span>
          </div>
        </div>

        {/* Price & Action */}
        <div className="flex flex-col items-end space-y-3 min-w-[180px]">
          <div className="text-right">
            <p className="text-sm text-gray-500">Precio total</p>
            <p className="text-3xl font-bold text-cosmic-base">
              {flight.price?.total || 'N/A'}
              <span className="text-lg text-gray-500 ml-1">{flight.price?.currency}</span>
            </p>
          </div>

          <button
            onClick={(e) => {
              e.stopPropagation();
              onSelect(flight);
            }}
            className={`px-6 py-3 rounded-lg font-medium transition-all duration-300 ${
              isSelected
                ? 'bg-cosmic-base text-white'
                : 'bg-flame-base text-white hover:bg-flame-dark'
            }`}
          >
            {isSelected ? 'Seleccionado ✓' : 'Seleccionar'}
          </button>
        </div>
      </div>
    </div>
  );
}