import React from "react";
import logo from "../../assets/logo_TravelGo2.png";
import { Link } from "react-router-dom";

function Navbar() {
  return (
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3">
        <div className="flex justify-between items-center">
          <div className="flex items-center">
            <Link to= "/">
              <img
                src={logo}
                alt="Logo"
                className="w-[120px] h-auto pointer-events-auto"
              />
            </Link>
          </div>
          
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
            <Link
              to="/login"
            
              className="btn-primary px-4 py-2 rounded-lg font-medium"
            >
              Iniciar sesión
            </Link>
          </nav>
          <button
            id="mobile-menu-button"
            className="md:hidden text-astronaut-dark focus:outline-none"
          >
            <i className="fas fa-bars text-2xl"></i>
          </button>
        </div>
        
        <div id="mobile-menu" className="mobile-menu md:hidden">
          <div className="pt-4 pb-2 space-y-3">
            <a
              href="#"
              target="_blank"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Inicio
            </a>
            <a
              href="#"
              target="_blank"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Destinos
            </a>
            <a
              href="#"
              target="_blank"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Paquetes
            </a>
            <a
              href="#"
              target="_blank"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Blog
            </a>
            <a
              href="#"
              target="_blank"
              className="block px-3 py-2 rounded-md text-astronaut-dark hover:bg-cosmic-light"
            >
              Contacto
            </a>
            <Link
            to="/login"
            className="block px-3 py-2 rounded-md btn-primary text-center"
            >
              Iniciar sesión
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
}
export default Navbar;
