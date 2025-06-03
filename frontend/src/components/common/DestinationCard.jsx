//props tarjetas de destinos
const DestinationCard = (props) => {
  const { title, description, img, price, link } = props;
  return (
    <div className="grid">
      <TitleCard
        Title={title}
        description={description}
        img={img}
        price={price}
        link={link}
      />
    </div>
  );
};
//estilos tarjetas de destino 
const TitleCard = ({ title, description, img, price, link }) => {
return (
<section>
      <div className="destination-card bg-white rounded-xl overflow-hidden shadow-md hover:shadow-lg">
        <div className="relative h-48 overflow-hidden">
          <img
            src={img}
            alt={title}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
          <div className="absolute bottom-4 left-4 text-white">
            <h3 className="font-bold text-xl">{title}</h3>
            <p className="text-sm">{price}</p>
          </div>
          <div class="p-4">
            <p class="text-gray-600 mb-4">
              {description}
            </p>
            <a
              href={link}
              class="text-cosmic-base font-medium hover:text-cosmic-dark flex items-center"
            >Ver más <i class="fas fa-arrow-right ml-2"></i>
            </a>
          </div>
        </div>
      </div>

    
</section>

);
}
export default DestinationCard;
