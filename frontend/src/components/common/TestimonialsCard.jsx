//props para tarjeta de testimonios
const TestimonialsCard = (props) => {
  const { name, description, img, score, } = props;
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <TitleCard
        name={name}
        description={description}
        img={img}
        score={score}
      />
    </div>
  );
};

//estilo de la tarjeta
const TitleCard = ({ name, description, img, score })=>{
<div class="testimonial-card p-6 rounded-lg">
                    <div class="flex items-center mb-4">
                        <div class="w-12 h-12 rounded-full overflow-hidden mr-4">
                            <img src={img} class="w-full h-full object-cover"/>
                        </div>
                        //Calificacion en estrellas
                        <div>
                            <h4 class="font-bold text-astronaut-dark">{name}</h4>
                            <div class="flex text-flame-base">
                                {score.map((star, index) => (
                                    <i class={`fas fa-star ${star ? 'text-yellow-500' : 'text-gray-300'}`} key={index}></i>
                                ))}
                            </div>
                        </div>
                    </div>
                    <p class="text-gray-600">{description}</p>
                </div>

};
export default TestimonialsCard;