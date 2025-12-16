import React, { useState, useEffect } from "react";
import logo from "../../assets/logo_TravelGo2.png";
import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState("");
  const [userInitials, setUserInitials] = useState("");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  

  useEffect(() => {
    // Verificar si hay sesión activa
    const token = localStorage.getItem("token");
    const primerNombre = localStorage.getItem("primerNombre");
    const primerApellido = localStorage.getItem("primerApellido");

    if (token && primerNombre && primerApellido) {
      setIsLoggedIn(true);
      setUserName(`${primerNombre} ${primerApellido}`);
      
      // Generar iniciales
      const iniciales = `${primerNombre.charAt(0)}${primerApellido.charAt(0)}`.toUpperCase();
      setUserInitials(iniciales);
    } else {
      setIsLoggedIn(false);
    }

      const handleStorageChange = () => {
  const token = localStorage.getItem("token");
  const primerNombre = localStorage.getItem("primerNombre");
  const primerApellido = localStorage.getItem("primerApellido");

  if (token && primerNombre && primerApellido) {
    setIsLoggedIn(true);
    setUserName(`${primerNombre} ${primerApellido}`);
    const iniciales = `${primerNombre.charAt(0)}${primerApellido.charAt(0)}`.toUpperCase();
    setUserInitials(iniciales);
  } else {
    setIsLoggedIn(false);
    setUserName("");
    setUserInitials("");
  }
};

// Listener para cambios en localStorage
window.addEventListener('storage', handleStorageChange);

// Cleanup
return () => {
  window.removeEventListener('storage', handleStorageChange);
};

  }, []);

  const handleProfileClick = () => {
    navigate("/UserProfile");
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  return (
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3">
        <div className="flex justify-between items-center">
          <div className="flex items-center">
            <Link to="/">
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
            
            {/* Mostrar botón de perfil si hay sesión, sino mostrar login */}
            {isLoggedIn ? (
              <button
                onClick={handleProfileClick}
                className="flex items-center space-x-2 px-4 py-2 rounded-lg bg-gradient-to-r from-cosmic-base to-astronaut-base hover:from-cosmic-dark hover:to-astronaut-dark text-white font-medium transition-all duration-300 transform hover:scale-105 shadow-md hover:shadow-lg"
                title={`Perfil de ${userName}`}
              >
                {/* Avatar con iniciales */}
                <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center text-cosmic-dark font-bold text-sm">
                  {userInitials}
                </div>
                <span className="hidden lg:inline">{userName.split(' ')[0]}</span>
              </button>
            ) : (
              <Link
                to="/login"
                className="btn-primary px-4 py-2 rounded-lg font-medium"
              >
                Iniciar sesión
              </Link>
            )}
          </nav>

          <button
            onClick={toggleMobileMenu}
            className="md:hidden text-astronaut-dark focus:outline-none"
          >
            <i className={`fas ${mobileMenuOpen ? 'fa-times' : 'fa-bars'} text-2xl`}></i>
          </button>
        </div>
        
        {/* Menú móvil */}
        <div className={`mobile-menu md:hidden ${mobileMenuOpen ? 'open' : ''}`}>
          <div className="pt-4 pb-2 space-y-3">
            {/* Mostrar info de sesión en móvil si está logueado */}
            {isLoggedIn && (
              <div className="px-3 py-3 mb-2 bg-gradient-to-r from-cosmic-light to-astronaut-light rounded-md">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-full bg-gradient-to-br from-cosmic-base to-astronaut-base flex items-center justify-center text-white font-bold shadow-md">
                    {userInitials}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-semibold text-astronaut-dark">{userName}</p>
                    <p className="text-xs text-gray-600">Sesión activa</p>
                  </div>
                </div>
              </div>
            )}

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
            
            {/* Botón de perfil o login en móvil */}
            {isLoggedIn ? (
              <button
                onClick={handleProfileClick}
                className="w-full text-left px-3 py-2 rounded-md bg-gradient-to-r from-cosmic-base to-astronaut-base text-white font-medium hover:from-cosmic-dark hover:to-astronaut-dark transition-all"
              >
                👤 Mi Perfil
              </button>
            ) : (
              <Link
                to="/login"
                className="block px-3 py-2 rounded-md btn-primary text-center"
              >
                Iniciar sesión
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
export default Navbar;