import React, { useEffect } from "react";
import {
  User,
  Ticket,
  Heart,
  Settings,
  HelpCircle,
  LogOut,
  Menu,
  Bell,
  Camera,
  Star,
  Edit2,
  Trash2,
} from "lucide-react";

export default function UserProfile() {
  useEffect(() => {
    document.body.style.fontFamily = "'Inter', sans-serif";
  }, []);

  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      {/* Sidebar */}
      <aside className="hidden md:flex md:flex-shrink-0">
        <div className="flex flex-col w-64 bg-white shadow-md">
          <div className="flex items-center h-16 px-4 bg-blue-600">
            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span className="ml-2 text-xl font-bold text-white">TravelGo</span>
          </div>
          <nav className="flex-1 p-4 overflow-y-auto">
            <ul className="space-y-2">
              <li className="flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md">
                <User className="w-5 h-5 mr-3" /> Perfil
              </li>
              <li className="flex items-center px-4 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-blue-50 hover:text-blue-600">
                <Ticket className="w-5 h-5 mr-3" /> viajes
              </li>
              <li className="flex items-center px-4 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-blue-50 hover:text-blue-600">
                <Heart className="w-5 h-5 mr-3" /> Favoritos
              </li>
              <li className="flex items-center px-4 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-blue-50 hover:text-blue-600">
                <Settings className="w-5 h-5 mr-3" /> configuracion
              </li>
              <li className="flex items-center px-4 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-blue-50 hover:text-blue-600">
                <HelpCircle className="w-5 h-5 mr-3" /> ayuda
              </li>
              <li className="flex items-center w-full px-4 py-2 mt-4 text-sm font-medium text-gray-600 rounded-md hover:bg-blue-50 hover:text-blue-600">
                <LogOut className="w-5 h-5 mr-3" /> Sign out
              </li>
            </ul>
          </nav>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex flex-col flex-1 overflow-hidden">
        {/* Mobile Top Nav */}
        <header className="md:hidden flex items-center justify-between px-4 py-3 bg-white border-b border-gray-200">
          <button className="p-1 text-gray-500 rounded-md hover:text-blue-600 hover:bg-gray-100">
            <Menu className="w-6 h-6" />
          </button>
          <div className="flex items-center">
            <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span className="ml-2 text-xl font-bold text-blue-600">TravelGo</span>
          </div>
          <button className="p-1 text-gray-500 rounded-md hover:text-blue-600 hover:bg-gray-100">
            <Bell className="w-6 h-6" />
          </button>
        </header>

        {/* Main Area */}
        <main className="flex-1 overflow-y-auto p-4">
          {/* You can now insert the rest of the profile structure (avatar, tabs, personal info, bookings, etc.) here as reusable React components */}
          <div className="text-gray-700 text-xl font-semibold">
            <div className="p-6 bg-white rounded-lg shadow">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-800">Información personal</h2>
        <button className="flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
          <Edit2 className="w-4 h-4 mr-2" />Editar
        </button>
      </div>
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <div>
          <label className="block text-sm font-medium text-gray-500">Nombre completo</label>
          <div className="mt-1 text-sm text-gray-900">Sarah Johnson</div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-500">Correo</label>
          <div className="mt-1 text-sm text-gray-900">sarah@example.com</div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-500">Teléfono</label>
          <div className="mt-1 text-sm text-gray-900">+1 (555) 123-4567</div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-500">Nacionalidad</label>
          <div className="mt-1 text-sm text-gray-900">Estados Unidos</div>
        </div>
      </div>
    </div>
          </div>
        </main>
      </div>
    </div>
  );
}
