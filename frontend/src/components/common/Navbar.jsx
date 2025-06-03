import React from "react";
import logo from "../../assets/logo_TravelGo2.png";

// estilos barra superior 
function Navbar() {
  return (
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3">
        <div className="flex justify-between items-center">
          <div className="flex items-center">
            <a href="../../pages/public/Home.jsx">
              {" "}
              <img
                src={logo}
                alt="Logo"
                className="w-[120px] h-auto pointer-events-auto"
              />
            </a>
          </div>
          //  opciones pantalla de computador
          <nav className="hidden md:flex space-x-8 items-center font-medium">
            <a href="#" className="nav-link font-medium">
              Inicio
            </a>
            <a href="#" className="nav-link font-medium">
              Destinos
            </a>
            <a href="#" className="nav-link font-medium">
              Paquetes
            </a>
            <a href="#" className="nav-link font-medium">
              Blog
            </a>
            <a href="#" className="nav-link font-medium">
              Contacto
            </a>
            <a
              href="../../pages/public/Login.jsx"
              target="_blank"
              className="btn-primary px-4 py-2 rounded-lg font-medium"
            >
              Iniciar sesión
            </a>
          </nav>
          <button
            id="mobile-menu-button"
            className="md:hidden text-astronaut-dark focus:outline-none"
          >
            <i className="fas fa-bars text-2xl"></i>
          </button>
        </div>
        //opciones pantalla celular
        <div id="mobile-menu" className="mobile-menu md:hidden">
          <div className="pt-4 pb-2 space-y-3">
            <a
              href="#"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Inicio
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Destinos
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Paquetes
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Blog
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Contacto
            </a>
            <a
              href="./login.html"
              className="block px-3 py-2 rounded-md btn-primary text-center"
            >
              Iniciar sesión
            </a>
          </div>
        </div>
      </div>
    </header>
  );
}
export default Navbar;
