import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaMapMarkedAlt } from "react-icons/fa";

function SearchCard() {
  const navigate = useNavigate();

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
      
      // Construir parámetros de búsqueda
      const queryParams = new URLSearchParams({
        origin: formData.origin,
        destination: formData.destination,
        departure: formData.departureDate,
        adults: formData.adults,
        max: 10
      });
      
      // Agregar returnDate solo si existe y no está vacío
      if (formData.returnDate && formData.returnDate.trim() !== "") {
        queryParams.append('returnDate', formData.returnDate);
      }
      
      const response = await fetch(`http://localhost:9090/flights/search?${queryParams}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        }
      });
      
      // Verificar si la respuesta es válida antes de parsear JSON
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

  // Nueva función para manejar reserva
  const handleReservation = (flight) => {
    console.log("Iniciando reserva para vuelo:", flight);
    
    // Navegar a la página de booking con los datos del vuelo
    navigate('/booking', {
      state: {
        flight: flight,
        origin: formData.origin,
        destination: formData.destination,
        departureDate: formData.departureDate,
        returnDate: formData.returnDate,
        adults: formData.adults,
      }
    });
  };

  return (
    <div>
      {/* HERO SECTION */}
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

      {/* SEARCH CARD SECTION */}
      <section id="search-section" className="container mx-auto px-4 -mt-16 mb-5">
        <div className="search-card p-6 md:p-8 bg-white rounded-lg shadow-lg">
          <h2 className="text-2xl font-bold text-astronaut-dark mb-6">Encuentra tu viaje perfecto</h2>
          
          {error && (
            <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-lg">
              {error}
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* ORIGEN */}
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

            {/* DESTINO */}
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

            {/* FECHA DE SALIDA */}
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
                  min={new Date().toISOString().split('T')[0]}
                  className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                />
              </div>
            </div>

            {/* FECHA DE REGRESO (OPCIONAL) */}
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

            {/* PERSONAS */}
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

            {/* BOTÓN BUSCAR */}
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

      {/* SECCIÓN DE RESULTADOS */}
      <section className="container mx-auto px-4 mt-10 mb-16">
        <h3 className="text-2xl font-bold mb-6 text-astronaut-dark">
          {results.length > 0 ? `${results.length} vuelos encontrados` : 'Resultados:'}
        </h3>
        
        {/* LOADING STATE */}
        {loading && (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-cosmic-base"></div>
            <p className="mt-4 text-gray-600 text-lg">Buscando los mejores vuelos para ti...</p>
          </div>
        )}
        
        {/* ERROR STATE */}
        {error && !loading && (
          <div className="p-6 bg-red-100 border border-red-300 text-red-700 rounded-lg flex items-start">
            <i className="fas fa-exclamation-circle text-2xl mr-3 mt-1"></i>
            <div>
              <p className="font-semibold text-lg mb-1">Error en la búsqueda</p>
              <p>{error}</p>
            </div>
          </div>
        )}
        
        {/* RESULTS STATE */}
        {results.length > 0 && !loading ? (
          <div className="grid gap-6">
            {results.map((flight, index) => (
              <div 
                key={flight.id || index} 
                className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 border border-gray-200 hover:border-cosmic-base"
              >
                <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
                  {/* FLIGHT INFO */}
                  <div className="flex-1 w-full">
                    <div className="flex items-center justify-between mb-4">
                      <div className="flex items-center space-x-3">
                        <div className="w-12 h-12 bg-astronaut-light rounded-full flex items-center justify-center">
                          <i className="fas fa-plane text-astronaut-base text-xl"></i>
                        </div>
                        <div>
                          <h4 className="text-xl font-bold text-astronaut-dark">
                            {flight.itineraries?.[0]?.segments?.[0]?.departure?.iataCode || 'N/A'} 
                            <i className="fas fa-arrow-right mx-2 text-cosmic-base"></i>
                            {flight.itineraries?.[0]?.segments?.slice(-1)[0]?.arrival?.iataCode || 'N/A'}
                          </h4>
                          <p className="text-sm text-gray-500">
                            {flight.itineraries?.[0]?.segments?.[0]?.carrierCode || ''} {flight.itineraries?.[0]?.segments?.[0]?.number || ''}
                          </p>
                        </div>
                      </div>
                    </div>

                    {/* FLIGHT DETAILS GRID */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                      {/* DEPARTURE TIME */}
                      <div className="flex items-center space-x-2">
                        <i className="fas fa-calendar text-cosmic-base"></i>
                        <div>
                          <p className="text-xs text-gray-500">Salida</p>
                          <p className="text-sm font-medium">
                            {flight.itineraries?.[0]?.segments?.[0]?.departure?.at 
                              ? new Date(flight.itineraries[0].segments[0].departure.at).toLocaleString('es-ES', {
                                  day: '2-digit',
                                  month: 'short',
                                  hour: '2-digit',
                                  minute: '2-digit'
                                })
                              : 'N/A'}
                          </p>
                        </div>
                      </div>

                      {/* DURATION */}
                      <div className="flex items-center space-x-2">
                        <i className="fas fa-clock text-cosmic-base"></i>
                        <div>
                          <p className="text-xs text-gray-500">Duración</p>
                          <p className="text-sm font-medium">
                            {flight.itineraries?.[0]?.duration 
                              ? flight.itineraries[0].duration.replace('PT', '').replace('H', 'h ').replace('M', 'm')
                              : 'N/A'}
                          </p>
                        </div>
                      </div>

                      {/* SEATS */}
                      <div className="flex items-center space-x-2">
                        <i className="fas fa-users text-cosmic-base"></i>
                        <div>
                          <p className="text-xs text-gray-500">Asientos</p>
                          <p className="text-sm font-medium">
                            {flight.numberOfBookableSeats || 'Consultar'}
                          </p>
                        </div>
                      </div>
                    </div>

                    {/* SEGMENTS INFO */}
                    {flight.itineraries?.[0]?.segments && (
                      <div className="border-t pt-3">
                        <h5 className="font-medium text-sm text-gray-700 mb-2">
                          <i className="fas fa-info-circle text-cosmic-base mr-2"></i>
                          Detalles del viaje:
                        </h5>
                        <div className="space-y-1">
                          {flight.itineraries[0].segments.map((segment, segIndex) => (
                            <div key={segIndex} className="text-sm text-gray-600 flex items-center">
                              <span className="bg-astronaut-light text-astronaut-dark px-2 py-1 rounded font-medium mr-2">
                                {segment.carrierCode} {segment.number}
                              </span>
                              <span>
                                {segment.departure?.at 
                                  ? new Date(segment.departure.at).toLocaleTimeString('es-ES', {
                                      hour: '2-digit',
                                      minute: '2-digit'
                                    })
                                  : 'N/A'}
                              </span>
                              <i className="fas fa-long-arrow-alt-right mx-2 text-cosmic-base"></i>
                              <span>
                                {segment.arrival?.at 
                                  ? new Date(segment.arrival.at).toLocaleTimeString('es-ES', {
                                      hour: '2-digit',
                                      minute: '2-digit'
                                    })
                                  : 'N/A'}
                              </span>
                            </div>
                          ))}
                        </div>
                        <p className="text-xs text-gray-500 mt-2">
                          {flight.itineraries[0].segments.length > 1 
                            ? `${flight.itineraries[0].segments.length - 1} escala(s)` 
                            : 'Vuelo directo'}
                        </p>
                      </div>
                    )}
                  </div>

                  {/* PRICE & ACTION */}
                  <div className="flex flex-col items-end space-y-3 min-w-[200px] lg:min-w-[220px]">
                    <div className="text-right">
                      <p className="text-sm text-gray-500 mb-1">Precio total</p>
                      <p className="text-3xl font-bold text-cosmic-base">
                        {flight.price?.total 
                          ? `${parseFloat(flight.price.total).toFixed(2)}`
                          : 'N/A'}
                      </p>
                      <p className="text-lg text-gray-500">
                        {flight.price?.currency || 'USD'}
                      </p>
                    </div>

                    <button
                      onClick={() => handleReservation(flight)}
                      className="w-full bg-flame-base hover:bg-flame-dark text-white font-semibold py-3 px-6 rounded-lg transition-all duration-300 transform hover:scale-105 shadow-md hover:shadow-lg flex items-center justify-center space-x-2"
                    >
                      <i className="fas fa-ticket-alt"></i>
                      <span>Reservar Ahora</span>
                    </button>

                    {/* ADDITIONAL INFO */}
                    {flight.numberOfBookableSeats && flight.numberOfBookableSeats <= 5 && (
                      <div className="w-full bg-red-50 border border-red-200 text-red-700 text-xs px-3 py-2 rounded text-center">
                        <i className="fas fa-exclamation-triangle mr-1"></i>
                        ¡Solo {flight.numberOfBookableSeats} asientos disponibles!
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : !loading && !error && (
          <div className="text-center py-12 bg-gray-50 rounded-lg">
            <i className="fas fa-search text-6xl text-gray-300 mb-4"></i>
            <p className="text-gray-500 text-lg">
              No hay resultados aún. Realiza una búsqueda para ver vuelos disponibles.
            </p>
          </div>
        )}
      </section>
    </div>
  );
}

export default SearchCard;