import React from 'react';
import { FaPlane, FaClock, FaUsers, FaTicketAlt } from 'react-icons/fa';

/**
 * FlightCard - Tarjeta de vuelo mejorada con diseño profesional
 * @param {Object} flight - Datos del vuelo de Amadeus
 * @param {Function} onReserve - Callback al hacer click en reservar
 */
export default function FlightCard({ flight, onReserve }) {
  // Extraer información del vuelo
  const firstSegment = flight.itineraries?.[0]?.segments?.[0];
  const lastSegment = flight.itineraries?.[0]?.segments?.slice(-1)[0];
  const returnFlight = flight.itineraries?.[1];
  
  const origin = firstSegment?.departure?.iataCode;
  const destination = lastSegment?.arrival?.iataCode;
  const departureTime = firstSegment?.departure?.at;
  const arrivalTime = lastSegment?.arrival?.at;
  const duration = flight.itineraries?.[0]?.duration;
  const airline = firstSegment?.carrierCode;
  const price = flight.price?.total;
  const currency = flight.price?.currency;
  const seats = flight.numberOfBookableSeats;

  // Formatear hora
  const formatTime = (datetime) => {
    if (!datetime) return 'N/A';
    const date = new Date(datetime);
    return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  };

  // Formatear fecha
  const formatDate = (datetime) => {
    if (!datetime) return 'N/A';
    const date = new Date(datetime);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short' });
  };

  // Formatear duración (de PT2H30M a "2h 30m")
  const formatDuration = (dur) => {
    if (!dur) return 'N/A';
    const hours = dur.match(/(\d+)H/)?.[1] || '0';
    const minutes = dur.match(/(\d+)M/)?.[1] || '0';
    return `${hours}h ${minutes}m`;
  };

  // Calcular número de escalas
  const stops = (flight.itineraries?.[0]?.segments?.length || 1) - 1;

  return (
    <div className="bg-white rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden border border-gray-200 hover:border-blue-400">
      {/* Header con precio */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-500 px-6 py-4 flex justify-between items-center">
        <div className="flex items-center space-x-3">
          <FaPlane className="text-white text-2xl" />
          <div>
            <h3 className="text-white font-bold text-xl">
              {origin} → {destination}
            </h3>
            <p className="text-blue-100 text-sm">
              {stops === 0 ? 'Directo' : `${stops} escala${stops > 1 ? 's' : ''}`}
            </p>
          </div>
        </div>
        <div className="text-right">
          <p className="text-white text-3xl font-bold">
            {currency} {parseFloat(price).toLocaleString('es-ES', { minimumFractionDigits: 2 })}
          </p>
          <p className="text-blue-100 text-sm">por persona</p>
        </div>
      </div>

      {/* Contenido principal */}
      <div className="p-6">
        {/* VUELO DE IDA */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-3">
            <span className="text-gray-500 text-sm font-medium">VUELO DE IDA</span>
            <span className="text-gray-600 text-sm">{formatDate(departureTime)}</span>
          </div>
          
          <div className="flex items-center justify-between">
            {/* Salida */}
            <div className="text-center">
              <p className="text-3xl font-bold text-gray-800">{formatTime(departureTime)}</p>
              <p className="text-gray-600 font-medium">{origin}</p>
            </div>

            {/* Duración */}
            <div className="flex-1 px-6">
              <div className="flex items-center justify-center mb-2">
                <FaClock className="text-gray-400 mr-2" />
                <span className="text-gray-600 text-sm font-medium">
                  {formatDuration(duration)}
                </span>
              </div>
              <div className="relative">
                <div className="h-0.5 bg-gray-300 w-full"></div>
                <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white px-2">
                  <FaPlane className="text-blue-500 transform rotate-90" />
                </div>
              </div>
            </div>

            {/* Llegada */}
            <div className="text-center">
              <p className="text-3xl font-bold text-gray-800">{formatTime(arrivalTime)}</p>
              <p className="text-gray-600 font-medium">{destination}</p>
            </div>
          </div>
        </div>

        {/* VUELO DE REGRESO (si existe) */}
        {returnFlight && (
          <div className="mb-6 pt-6 border-t border-gray-200">
            <div className="flex items-center justify-between mb-3">
              <span className="text-gray-500 text-sm font-medium">VUELO DE REGRESO</span>
              <span className="text-gray-600 text-sm">
                {formatDate(returnFlight.segments?.[0]?.departure?.at)}
              </span>
            </div>
            
            <div className="flex items-center justify-between">
              <div className="text-center">
                <p className="text-3xl font-bold text-gray-800">
                  {formatTime(returnFlight.segments?.[0]?.departure?.at)}
                </p>
                <p className="text-gray-600 font-medium">{destination}</p>
              </div>

              <div className="flex-1 px-6">
                <div className="flex items-center justify-center mb-2">
                  <FaClock className="text-gray-400 mr-2" />
                  <span className="text-gray-600 text-sm font-medium">
                    {formatDuration(returnFlight.duration)}
                  </span>
                </div>
                <div className="relative">
                  <div className="h-0.5 bg-gray-300 w-full"></div>
                  <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white px-2">
                    <FaPlane className="text-blue-500 transform -rotate-90" />
                  </div>
                </div>
              </div>

              <div className="text-center">
                <p className="text-3xl font-bold text-gray-800">
                  {formatTime(returnFlight.segments?.slice(-1)[0]?.arrival?.at)}
                </p>
                <p className="text-gray-600 font-medium">{origin}</p>
              </div>
            </div>
          </div>
        )}

        {/* Información adicional */}
        <div className="flex items-center justify-between py-4 px-4 bg-gray-50 rounded-lg mb-4">
          <div className="flex items-center space-x-2">
            <FaTicketAlt className="text-blue-500" />
            <span className="text-sm text-gray-700">
              <span className="font-semibold">{airline}</span> Airlines
            </span>
          </div>
          
          <div className="flex items-center space-x-2">
            <FaUsers className="text-blue-500" />
            <span className="text-sm text-gray-700">
              <span className="font-semibold">{seats}</span> asientos disponibles
            </span>
          </div>
        </div>

        {/* Detalles de segmentos */}
        <details className="mb-4">
          <summary className="cursor-pointer text-blue-600 hover:text-blue-700 text-sm font-medium mb-2">
            Ver detalles del vuelo
          </summary>
          <div className="bg-gray-50 rounded-lg p-4 space-y-2">
            {flight.itineraries?.[0]?.segments?.map((segment, index) => (
              <div key={index} className="text-sm text-gray-600 flex justify-between items-center py-2 border-b last:border-b-0">
                <div>
                  <span className="font-semibold">{segment.carrierCode} {segment.number}</span>
                  <span className="mx-2">•</span>
                  <span>{segment.departure.iataCode} → {segment.arrival.iataCode}</span>
                </div>
                <span className="text-gray-500">
                  {formatTime(segment.departure.at)} - {formatTime(segment.arrival.at)}
                </span>
              </div>
            ))}
          </div>
        </details>

        {/* Botón de reserva */}
        <button
          onClick={() => onReserve(flight)}
          className="w-full bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-blue-600 text-white font-bold py-4 px-6 rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl flex items-center justify-center space-x-2"
        >
          <FaTicketAlt className="text-xl" />
          <span className="text-lg">Reservar este vuelo</span>
        </button>
      </div>
    </div>
  );
}