import { FaMapMarkedAlt } from "react-icons/fa";
function SearchCard () {
  return (
    <div>
    <section className="relative h-[80vh] flex items-center justify-center text-white overflow-hidden">
      //imagen de fondo seccion busqueda
      <div 
        className="absolute inset-0 bg-cover bg-center bg-no-repeat transition-opacity duration-1000"
        style={{
          backgroundImage: "url('https://images.unsplash.com/photo-1499678329028-101435549a4e?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80')"
        }}
        loading="lazy"
      />
      
      //Mensaje seccion busquedas
      <div className="absolute inset-0 bg-gradient-to-br from-purple-900/80 via-purple-800/70 to-indigo-900/60 animate-pulse" />
      
      
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
    //seccion busqueda de destino
    <section id="search-section" className="container mx-auto px-4 -mt-16 mb-5">
        <div className="search-card p-6 md:p-8">
            <h2 className="text-2xl font-bold text-astronaut-dark mb-6">Encuentra tu viaje perfecto</h2>
            <form className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <div>
                    <label for="destination" className="block text-astronaut-dark font-medium mb-2">Destino</label>
                    <div className="relative">
                        <i className="fas fa-user absolute left-3 top-3 text-cosmic-base"></i>
                        <input type="text" id="destination" placeholder="¿A dónde vas?" className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"/>
                    </div>
                </div>
                <div>
                    <label for="departure" class="block text-astronaut-dark font-medium mb-2">Fecha de salida</label>
                    <div className="relative">
                        <i className=" Fa calendar absolute left-3 top-3 text-cosmic-base"></i>
                        <input type="date" id="departure" class="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"/>
                    </div>
                </div>
                <div>
                    <label for="return" className="block text-astronaut-dark font-medium mb-2">Fecha de regreso</label>
                    <div className="relative">
                        <i className="fas fa-calendar-alt absolute left-3 top-3 text-cosmic-base"></i>
                        <input type="date" id="return" className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"/>
                    </div>
                </div>
                <div>
                    <label for="passengers" className="block text-astronaut-dark font-medium mb-2">Personas</label>
                    <div className="relative">
                        <i className="fas fa-user absolute left-3 top-3 text-cosmic-base"></i>
                        <select id="passengers" className="pl-10 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base">
                            <option value="1">1 persona</option>
                            <option value="2">2 personas</option>
                            <option value="3">3 personas</option>
                            <option value="4">4 personas</option>
                            <option value="5+">5+ personas</option>
                        </select>
                    </div>
                </div>
                <div className="md:col-span-2 lg:col-span-4 flex justify-center mt-4">
                    <button type="submit" className="btn-primary px-8 py-3 rounded-lg font-medium w-full md:w-auto">
                        <i className="fas fa-search mr-2"></i> Buscar viajes
                    </button>
                </div>
            </form>
        </div>
    </section>
</div>
  );
};
export default SearchCard;