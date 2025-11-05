import { Building2, MapPin, Star } from 'lucide-react';

export default function HotelCard({ hotel, onSelect, isSelected }) {
  return (
    <div
      className={`bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 cursor-pointer border-2 ${
        isSelected ? 'border-cosmic-base ring-2 ring-cosmic-light' : 'border-transparent'
      }`}
      onClick={() => onSelect(hotel)}
    >
      <div className="flex flex-col lg:flex-row gap-4">
        {/* Hotel Image Placeholder */}
        <div className="w-full lg:w-48 h-48 bg-gradient-to-br from-cosmic-light to-astronaut-light rounded-lg flex items-center justify-center">
          <Building2 className="w-16 h-16 text-cosmic-base" />
        </div>

        {/* Hotel Info */}
        <div className="flex-1">
          <div className="flex items-start justify-between mb-3">
            <div>
              <h3 className="font-bold text-xl text-astronaut-dark mb-1">
                {hotel.name || 'Hotel'}
              </h3>
              <div className="flex items-center space-x-2 text-sm text-gray-500">
                <MapPin className="w-4 h-4" />
                <span>{hotel.iataCode || 'N/A'}</span>
              </div>
            </div>

            {/* Rating */}
            <div className="flex items-center space-x-1">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  className="w-4 h-4 fill-current text-yellow-400"
                />
              ))}
            </div>
          </div>

          <p className="text-sm text-gray-600 mb-4">
            {hotel.address?.cityName || 'Ciudad'}, {hotel.address?.countryCode || 'País'}
          </p>

          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-500">
              Hotel ID: {hotel.hotelId?.substring(0, 12)}...
            </div>

            <button
              onClick={(e) => {
                e.stopPropagation();
                onSelect(hotel);
              }}
              className={`px-6 py-2 rounded-lg font-medium transition-all duration-300 ${
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
    </div>
  );
}