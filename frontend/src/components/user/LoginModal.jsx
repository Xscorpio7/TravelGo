import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function LoginModal({ isOpen, onClose, flight }) {
  const navigate = useNavigate();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-md w-full p-8 relative">
        {/* Botón cerrar */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
        >
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        {/* Contenido */}
        <div className="text-center mb-6">
          <div className="mb-4">
            <svg className="w-16 h-16 text-blue-600 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            Inicia sesión para continuar
          </h2>
          <p className="text-gray-600 mb-4">
            Necesitas estar autenticado para realizar una reserva
          </p>
        </div>

        {/* Información del vuelo (opcional) */}
        {flight && (
          <div className="bg-blue-50 rounded-lg p-4 mb-6">
            <p className="text-sm text-gray-700 mb-2">
              <span className="font-semibold">Vuelo seleccionado:</span>
            </p>
            <p className="text-lg font-bold text-blue-600">
              {flight.itineraries?.[0]?.segments?.[0]?.departure?.iataCode || 'N/A'} → {flight.itineraries?.[0]?.segments?.slice(-1)[0]?.arrival?.iataCode || 'N/A'}
            </p>
            <p className="text-sm text-gray-600 mt-1">
              {flight.price?.total} {flight.price?.currency}
            </p>
          </div>
        )}

        {/* Botones */}
        <div className="space-y-3">
          <button
            onClick={() => {
              onClose();
              navigate('/login');
            }}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
          >
            Iniciar Sesión
          </button>

          <button
            onClick={() => {
              onClose();
              navigate('/register');
            }}
            className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
          >
            Crear Cuenta
          </button>

          <button
            onClick={onClose}
            className="w-full bg-transparent hover:bg-gray-100 text-gray-700 font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
          >
            Cancelar
          </button>
        </div>

        <p className="text-center text-xs text-gray-500 mt-6">
          Al reservar aceptas nuestros términos y condiciones
        </p>
      </div>
    </div>
  );
}