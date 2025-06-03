import TestimonialsCard from '../../components/common/TestimonialsCard'
import Maria from "../../assets/MariaGonzalez.jpeg"
import Carlos from "../../assets/CarlosMartinez.jpg"
import Laura from "../../assets/LauraSanchez.jpg"

//Seccion donde se ingresa los datos de las tarjetas de los testimonios
function Testimonials() {
    return(
<section class="bg-section py-16 mb-16">
        <div class="container mx-auto px-4">
            <div class="text-center mb-12">
                <h2 class="section-title text-3xl font-bold mb-2">Lo que dicen nuestros viajeros</h2>
                <p class="text-gray-600 max-w-2xl mx-auto">Experiencias reales de nuestros clientes satisfechos</p>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <TestimonialsCard
                    name="María González"
                    description="¡El viaje a París organizado por Travel Go fue increíble. Todo estuvo perfectamente coordinado y el hotel estaba en una ubicación excelente. ¡Volveré a viajar con ellos sin duda!"
                    img={Maria}/>
                <TestimonialsCard
                    name="Carlos Martínez"
                    description="Nuestra luna de miel en Bali fue mágica gracias a Travel Go. Las excursiones incluidas eran espectaculares y el servicio al cliente resolvió todas nuestras dudas rápidamente."
                    img={Carlos}/>
                <TestimonialsCard
                    name="Laura Sánchez"
                    description="Viajar a Japón siempre fue mi sueño y Travel Go lo hizo realidad. El itinerario incluía todos los lugares que quería visitar y los guías eran muy conocedores. ¡Experiencia 10/10!"
                    img={Laura}
                />


            </div>
        </div>
    </section>
    )
};
export default Testimonials;