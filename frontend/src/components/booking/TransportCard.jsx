import { Car, Clock, Users, DollarSign, MapPin } from 'lucide-react';

export default function TransportCard({ transport, onSelect, isSelected }) {
  return (
    <div
      className={`bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 cursor-pointer border-2 ${
        isSelected ? 'border-cosmic-base ring-2 ring-cosmic-light' : 'border-transparent'
      }`}
      onClick={() => onSelect(transport)}
    >
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
        {/* Transport Info */}
        <div className="flex-1">
          <div className="flex items-center space-x-3 mb-4">
            <div className="w-12 h-12 bg-astronaut-light rounded-full flex items-center justify-center">
              <Car className="w-6 h-6 text-astronaut-base" />
            </div>
            <div>
              <h3 className="font-bold text-lg text-astronaut-dark">
                {transport.vehiculoTipo || 'Transfer Privado'}
              </h3>
              <p className="text-sm text-gray-500">
                {transport.proveedor || 'Proveedor'}
              </p>
            </div>
          </div>

          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-3">
            <div className="flex items-center space-x-2">
              <MapPin className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Origen</p>
                <p className="text-sm font-medium">{transport.origen}</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <MapPin className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Destino</p>
                <p className="text-sm font-medium">{transport.destino}</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Users className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Capacidad</p>
                <p className="text-sm font-medium">{transport.capacidad || 'N/A'} personas</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Clock className="w-4 h-4 text-cosmic-base" />
              <div>
                <p className="text-xs text-gray-500">Duración</p>
                <p className="text-sm font-medium">{transport.duracionMinutos || 'N/A'} min</p>
              </div>
            </div>
          </div>

          {transport.descripcion && (
            <p className="text-sm text-gray-600 line-clamp-2">{transport.descripcion}</p>
          )}
        </div>

        {/* Price & Action */}
        <div className="flex flex-col items-end space-y-3 min-w-[180px]">
          <div className="text-right">
            <p className="text-sm text-gray-500">Precio</p>
            <p className="text-2xl font-bold text-cosmic-base">
              {transport.precio || 'N/A'}
              <span className="text-lg text-gray-500 ml-1">{transport.currency || 'USD'}</span>
            </p>
          </div>

          <button
            onClick={(e) => {
              e.stopPropagation();
              onSelect(transport);
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