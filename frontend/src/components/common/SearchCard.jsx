import { useState } from "react";
import axios from "axios";
import { FaMapMarkedAlt } from "react-icons/fa";
import { useAuth } from "../hooks/useAuth";
import LoginModal from "./LoginModal";
function SearchCard() {
  const [formData, setFormData] = useState({
    origin: "",
    destination: "",
    departureDate: "",
    returnDate: "",
    adults: 1,
  });

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");


  const { isAuthenticated, user } = useAuth();

  
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [selectedFlight, setSelectedFlight] = useState(null);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    
    try {
      console.log("Enviando datos:", formData);
      
      // ⭐ CORREGIDO: URL correcta y método GET (no POST)
      const queryParams = new URLSearchParams({
        origin: formData.origin,
        destination: formData.destination,
        departure: formData.departureDate, // ⭐ Cambio: 'departure' no 'departureDate'
        adults: formData.adults,
        max: 10 // Límite de resultados
      });
      
      const response = await fetch(`http://localhost:9090/flights/search?${queryParams}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        }
      });
      
      // ⭐ Verificar si la respuesta es válida antes de parsear JSON
      let data;
      const contentType = response.headers.get("content-type");
      
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const responseText = await response.text();
        console.log("Respuesta raw:", responseText);
        
        try {
          data = JSON.parse(responseText);
        } catch (jsonError) {
          console.error("Error parseando JSON:", jsonError);
          throw new Error("Respuesta del servidor no es JSON válido");
        }
      } else {
        const text = await response.text();
        console.error("Respuesta no es JSON:", text);
        throw new Error("El servidor no devolvió JSON válido");
      }
      
      console.log("Respuesta parseada:", data);
      
      if (!response.ok) {
        throw new Error(data.error || `Error ${response.status}: ${response.statusText}`);
      }
      
      if (data.status === "SUCCESS") {
        setResults(data.data || []);
        console.log("Vuelos encontrados:", data.data);
      } else {
        throw new Error(data.error || "Error desconocido en la búsqueda");
      }
      
    } catch (error) {
      console.error("Error al buscar vuelos:", error);
      setError("Error al buscar vuelos: " + error.message);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <section className="relative h-[80vh] flex items-center justify-center text-white overflow-hidden">
        <div 
          className="absolute inset-0 bg-cover bg-center bg-no-repeat transition-opacity duration-1000"
          style={{
            backgroundImage: "url('https://images.unsplash.com/photo-1499678329028-101435549a4e?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80')"
          }}
          loading="lazy"
        />
        
        <div className="absolute inset-0 animate-pulse" />
        
        <div className="relative z-10 container mx-auto px-4 text-center">
          <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-4 animate-fade-in drop-shadow-lg">
            Descubre el mundo con{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-purple-300 to-pink-300">
              Travel Go
            </span>
          </h1>
          <p className="text-xl md:text-2xl mb-8 max-w-3xl mx-auto opacity-90 drop-shadow-md">
            Encuentra las mejores ofertas en viajes, paquetes y experiencias únicas.
          </p>
        </div>
      </section>

      <section id="search-section" className="container mx-auto px-4 -mt-16 mb-5">
        <div className="search-card p-6 md:p-8 bg-white rounded-lg shadow-lg">
          <h2 className="text-2xl font-bold text-astronaut-dark mb-6">Encuentra tu viaje perfecto</h2>
          
          {error && (
            <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">
              {error}
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div>                  
              <label htmlFor="origin" className="block text-astronaut-dark font-medium mb-2">Origen</label>
              <div className="relative">
                <i className="fas fa-plane-departure absolute left-3 top-3 text-cosmic-base"></i>
                <input 
                  type="text" 
                  id="origin" 
                  name="origin"
                  placeholder="MAD, BCN, etc." 
                  value={formData.origin} 
                  onChange={handleChange}
                  required
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                />
              </div>
            </div>

            <div>                  
              <label htmlFor="destination" className="block text-astronaut-dark font-medium mb-2">Destino</label>
              <div className="relative">
                <i className="fas fa-plane-arrival absolute left-3 top-3 text-cosmic-base"></i>
                <input 
                  type="text" 
                  id="destination"
                  name="destination"
                  placeholder="ATH, LON, etc." 
                  value={formData.destination} 
                  onChange={handleChange}
                  required
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                />
              </div>
            </div>

            <div>
              <label htmlFor="departureDate" className="block text-astronaut-dark font-medium mb-2">Fecha de salida</label>
              <div className="relative">
                <i className="fas fa-calendar absolute left-3 top-3 text-cosmic-base"></i>
                <input 
                  type="date" 
                  id="departureDate"
                  name="departureDate"
                  value={formData.departureDate} 
                  onChange={handleChange}
                  required
                  min={new Date().toISOString().split('T')[0]} // No permitir fechas pasadas
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                />
              </div>
            </div>

            <div>
              <label htmlFor="returnDate" className="block text-astronaut-dark font-medium mb-2">Fecha de regreso (opcional)</label>
              <div className="relative">
                <i className="fas fa-calendar-alt absolute left-3 top-3 text-cosmic-base"></i>
                <input 
                  type="date" 
                  id="returnDate" 
                  name="returnDate"
                  value={formData.returnDate}
                  onChange={handleChange}
                  min={formData.departureDate || new Date().toISOString().split('T')[0]}
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                />
              </div>
            </div>

            <div>
              <label htmlFor="adults" className="block text-astronaut-dark font-medium mb-2">Personas</label>
              <div className="relative">
                <i className="fas fa-user absolute left-3 top-3 text-cosmic-base"></i>
                <select 
                  id="adults" 
                  name="adults"
                  value={formData.adults}
                  onChange={handleChange}
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                >
                  <option value="1">1 persona</option>
                  <option value="2">2 personas</option>
                  <option value="3">3 personas</option>
                  <option value="4">4 personas</option>
                  <option value="5">5 personas</option>
                  <option value="6">6 personas</option>
                  <option value="7">7 personas</option>
                  <option value="8">8 personas</option>
                  <option value="9">9 personas</option>
                </select>
              </div>
            </div>

            <div className="md:col-span-2 lg:col-span-4 flex justify-center mt-4">
              <button 
                type="submit" 
                disabled={loading}
                className="btn-primary px-8 py-3 rounded-lg font-medium w-full md:w-auto disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <i className={`fas ${loading ? 'fa-spinner fa-spin' : 'fa-search'} mr-2`}></i>
                {loading ? 'Buscando...' : 'Buscar vuelos'}
              </button>
            </div>
          </form>
        </div>
      </section>

      {/* ⭐ SECCIÓN DE RESULTADOS MEJORADA */}
      <section className="container mx-auto px-4 mt-10">
        <h3 className="text-xl font-bold mb-4">Resultados:</h3>
        
        {loading && (
          <div className="text-center py-8">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-cosmic-base"></div>
            <p className="mt-2">Buscando vuelos...</p>
          </div>
        )}
        
        {error && !loading && (
          <div className="p-4 bg-red-100 text-red-700 rounded-lg">
            {error}
          </div>
        )}
        
        {results.length > 0 && !loading ? (
          <div className="grid gap-4">
            {results.map((flight, index) => (
              <div key={flight.id || index} className="p-6 border rounded-lg shadow-lg bg-white">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h4 className="text-lg font-bold text-astronaut-dark">
                      ✈️ {flight.itineraries?.[0]?.segments?.[0]?.departure?.iataCode} → {flight.itineraries?.[0]?.segments?.slice(-1)[0]?.arrival?.iataCode}
                    </h4>
                    <p className="text-gray-600">
                      Duración: {flight.itineraries?.[0]?.duration || "N/A"}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-cosmic-base">
                      {flight.price?.total ? `${flight.price.total} ${flight.price.currency}` : "Precio no disponible"}
                    </p>
                    <p className="text-sm text-gray-500">
                      {flight.numberOfBookableSeats ? `${flight.numberOfBookableSeats} asientos disponibles` : ""}
                    </p>
                  </div>
                </div>
                
                {flight.itineraries?.[0]?.segments && (
                  <div className="border-t pt-4">
                    <h5 className="font-medium mb-2">Detalles del vuelo:</h5>
                    {flight.itineraries[0].segments.map((segment, segIndex) => (
                      <div key={segIndex} className="text-sm text-gray-600 mb-1">
                        <span className="font-medium">{segment.carrierCode} {segment.number}</span>
                        {' - '}
                        {segment.departure?.at ? new Date(segment.departure.at).toLocaleString('es-ES') : 'N/A'}
                        {' → '}
                        {segment.arrival?.at ? new Date(segment.arrival.at).toLocaleString('es-ES') : 'N/A'}
                      </div>
                    ))}
                  </div>
                )}
                <button
                  onClick={() => handleReservation(flight)}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg mt-4 transition-colors"
                >
                  Reservar
                </button>

              </div>
            ))}
          </div>
        ) : !loading && !error && (
          <p className="text-gray-500">No hay resultados aún. Realiza una búsqueda para ver vuelos disponibles.</p>
        )}
      </section>
      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        flight={selectedFlight}
      />
    </div>
  );
}

export default SearchCard;