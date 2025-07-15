export default function Footer() {
  return (
    <footer className="bg-astronaut-dark text-white py-8">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <div className="mb-4 md:mb-0">
            <h2 className="text-xl font-bold">Travel Go</h2>
            <p className="text-sm">© 2023 Travel Go. Todos los derechos reservados.</p>
          </div>
          <div className="flex space-x-4">
            <a href="#" className="text-white hover:text-gray-400">Política de privacidad</a>
            <a href="#" className="text-white hover:text-gray-400">Términos de servicio</a>
          </div>
        </div>
      </div>
    </footer>
  );
}