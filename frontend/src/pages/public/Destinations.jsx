import DestinationCard from "../../components/common/DestinationCard";
import paris from "../../assets/paris.jpg";
import tokyo from "../../assets/tokyo.jpg";
import newyork from "../../assets/newyork.webp";
import bali from "../../assets/bali.jpg";
function Destinations (){
   <section className="container mx-auto px-4 mb-16 bg-">
    <div className="text-center mb-12">
      <h2 className="section-title text-3xl font-bold mb-2">
        Destinos populares
      </h2>
      <p className="text-gray-600 max-w-2xl mx-auto">
        Explora nuestros destinos más buscados y vive experiencias inolvidables
      </p>
    </div>

    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
//caracteristicas de las tarejetas de destinios
        <DestinationCard
            title="París"
            description="Descubre la ciudad del amor con sus icónicos monumentos y cultura vibrante."
            img={paris}
            price="$1200"
            link="/destinations/paris"
        />
        <DestinationCard
            title="Tokio"
            description="Sumérgete en la modernidad y tradición de la capital japonesa."
            img={tokyo}
            price="$1500"
            link="/destinations/tokyo"
        />
        <DestinationCard
            title="Nueva York"
            description="Explora la ciudad que nunca duerme con su energía única y atracciones icónicas."
            img={newyork}
            price="$1300"
            link="/destinations/newyork"
        />
        <DestinationCard
            title="Roma"
            description="Viaja a través de la historia en la capital italiana llena de arte y cultura."
            img={bali}
            price="$1100"
            link="/destinations/bali"
        />
    </div>
    <div className="text-center mt-10">
      <a
        href="#"
        className="btn-secondary px-6 py-2 rounded-lg font-medium inline-block"
        target="_blank"
      >
        Ver todos los destinos
      </a>
    </div>
    </section>
}
export default Destinations;

