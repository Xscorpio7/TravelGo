import { useState } from 'react';
import { CreditCard, Smartphone, Building, DollarSign } from 'lucide-react';

export default function PaymentForm({ onSubmit, loading }) {
  const [paymentMethod, setPaymentMethod] = useState('Tarjeta');
  const [formData, setFormData] = useState({
    // Datos del pasajero
    primerNombre: '',
    primerApellido: '',
    email: '',
    telefono: '',
    documento: '',
    
    // Datos de pago - Tarjeta
    numeroTarjeta: '',
    nombreTitular: '',
    fechaExpiracion: '',
    cvv: '',
    
    // Datos de pago - PSE
    banco: '',
    tipoPersona: 'natural',
    tipoDocumento: 'CC',
    numeroDocumento: '',
    
    // Datos de pago - Nequi
    numeroNequi: '',
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Limpiar error del campo
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    // Validar datos del pasajero
    if (!formData.primerNombre.trim()) newErrors.primerNombre = 'Nombre requerido';
    if (!formData.primerApellido.trim()) newErrors.primerApellido = 'Apellido requerido';
    if (!formData.email.trim() || !/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email válido requerido';
    }
    if (!formData.telefono.trim()) newErrors.telefono = 'Teléfono requerido';
    if (!formData.documento.trim()) newErrors.documento = 'Documento requerido';

    // Validar según método de pago
    if (paymentMethod === 'Tarjeta') {
      if (!formData.numeroTarjeta.trim() || formData.numeroTarjeta.replace(/\s/g, '').length !== 16) {
        newErrors.numeroTarjeta = 'Número de tarjeta inválido';
      }
      if (!formData.nombreTitular.trim()) newErrors.nombreTitular = 'Nombre del titular requerido';
      if (!formData.fechaExpiracion.trim() || !/^\d{2}\/\d{2}$/.test(formData.fechaExpiracion)) {
        newErrors.fechaExpiracion = 'Formato: MM/AA';
      }
      if (!formData.cvv.trim() || formData.cvv.length < 3) {
        newErrors.cvv = 'CVV inválido';
      }
    } else if (paymentMethod === 'PSE') {
      if (!formData.banco) newErrors.banco = 'Selecciona un banco';
      if (!formData.numeroDocumento.trim()) newErrors.numeroDocumento = 'Número de documento requerido';
    } else if (paymentMethod === 'Nequi') {
      if (!formData.numeroNequi.trim() || formData.numeroNequi.length !== 10) {
        newErrors.numeroNequi = 'Número de celular inválido';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      onSubmit({
        ...formData,
        metodoPago: paymentMethod,
      });
    }
  };

  // Formatear número de tarjeta
  const formatCardNumber = (value) => {
    const cleaned = value.replace(/\s/g, '');
    const formatted = cleaned.match(/.{1,4}/g)?.join(' ') || cleaned;
    return formatted;
  };

  const paymentMethods = [
    { id: 'Tarjeta', name: 'Tarjeta de Crédito/Débito', icon: CreditCard },
    { id: 'Nequi', name: 'Nequi', icon: Smartphone },
    { id: 'PSE', name: 'PSE', icon: Building },
  ];

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Datos del Pasajero */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <h3 className="text-xl font-bold text-astronaut-dark mb-4">
          Datos del Pasajero Principal
        </h3>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Primer Nombre *
            </label>
            <input
              type="text"
              name="primerNombre"
              value={formData.primerNombre}
              onChange={handleChange}
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                errors.primerNombre ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
              }`}
              placeholder="Juan"
            />
            {errors.primerNombre && (
              <p className="text-red-500 text-sm mt-1">{errors.primerNombre}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Primer Apellido *
            </label>
            <input
              type="text"
              name="primerApellido"
              value={formData.primerApellido}
              onChange={handleChange}
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                errors.primerApellido ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
              }`}
              placeholder="Pérez"
            />
            {errors.primerApellido && (
              <p className="text-red-500 text-sm mt-1">{errors.primerApellido}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email *
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                errors.email ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
              }`}
              placeholder="juan@ejemplo.com"
            />
            {errors.email && (
              <p className="text-red-500 text-sm mt-1">{errors.email}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Teléfono *
            </label>
            <input
              type="tel"
              name="telefono"
              value={formData.telefono}
              onChange={handleChange}
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                errors.telefono ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
              }`}
              placeholder="3001234567"
            />
            {errors.telefono && (
              <p className="text-red-500 text-sm mt-1">{errors.telefono}</p>
            )}
          </div>

          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Documento de Identidad *
            </label>
            <input
              type="text"
              name="documento"
              value={formData.documento}
              onChange={handleChange}
              className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                errors.documento ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
              }`}
              placeholder="1234567890"
            />
            {errors.documento && (
              <p className="text-red-500 text-sm mt-1">{errors.documento}</p>
            )}
          </div>
        </div>
      </div>

      {/* Método de Pago */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <h3 className="text-xl font-bold text-astronaut-dark mb-4">
          Método de Pago
        </h3>

        {/* Selector de método */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          {paymentMethods.map(({ id, name, icon: Icon }) => (
            <button
              key={id}
              type="button"
              onClick={() => setPaymentMethod(id)}
              className={`flex items-center justify-center space-x-2 p-4 border-2 rounded-lg transition-all ${
                paymentMethod === id
                  ? 'border-cosmic-base bg-cosmic-light text-cosmic-dark'
                  : 'border-gray-200 hover:border-cosmic-base'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span className="font-medium">{name}</span>
            </button>
          ))}
        </div>

        {/* Formulario según método */}
        {paymentMethod === 'Tarjeta' && (
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Número de Tarjeta *
              </label>
              <input
                type="text"
                name="numeroTarjeta"
                value={formData.numeroTarjeta}
                onChange={(e) => {
                  const formatted = formatCardNumber(e.target.value);
                  if (formatted.replace(/\s/g, '').length <= 16) {
                    handleChange({ target: { name: 'numeroTarjeta', value: formatted } });
                  }
                }}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                  errors.numeroTarjeta ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                }`}
                placeholder="1234 5678 9012 3456"
                maxLength="19"
              />
              {errors.numeroTarjeta && (
                <p className="text-red-500 text-sm mt-1">{errors.numeroTarjeta}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre del Titular *
              </label>
              <input
                type="text"
                name="nombreTitular"
                value={formData.nombreTitular}
                onChange={handleChange}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                  errors.nombreTitular ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                }`}
                placeholder="JUAN PEREZ"
                style={{ textTransform: 'uppercase' }}
              />
              {errors.nombreTitular && (
                <p className="text-red-500 text-sm mt-1">{errors.nombreTitular}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha de Expiración *
                </label>
                <input
                  type="text"
                  name="fechaExpiracion"
                  value={formData.fechaExpiracion}
                  onChange={(e) => {
                    let value = e.target.value.replace(/\D/g, '');
                    if (value.length >= 2) {
                      value = value.slice(0, 2) + '/' + value.slice(2, 4);
                    }
                    handleChange({ target: { name: 'fechaExpiracion', value } });
                  }}
                  className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                    errors.fechaExpiracion ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                  }`}
                  placeholder="MM/AA"
                  maxLength="5"
                />
                {errors.fechaExpiracion && (
                  <p className="text-red-500 text-sm mt-1">{errors.fechaExpiracion}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  CVV *
                </label>
                <input
                  type="text"
                  name="cvv"
                  value={formData.cvv}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, '');
                    if (value.length <= 4) {
                      handleChange({ target: { name: 'cvv', value } });
                    }
                  }}
                  className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                    errors.cvv ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                  }`}
                  placeholder="123"
                  maxLength="4"
                />
                {errors.cvv && (
                  <p className="text-red-500 text-sm mt-1">{errors.cvv}</p>
                )}
              </div>
            </div>
          </div>
        )}

        {paymentMethod === 'PSE' && (
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Selecciona tu Banco *
              </label>
              <select
                name="banco"
                value={formData.banco}
                onChange={handleChange}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                  errors.banco ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                }`}
              >
                <option value="">Selecciona...</option>
                <option value="bancolombia">Bancolombia</option>
                <option value="davivienda">Davivienda</option>
                <option value="bbva">BBVA</option>
                <option value="banco_bogota">Banco de Bogotá</option>
                <option value="banco_occidente">Banco de Occidente</option>
              </select>
              {errors.banco && (
                <p className="text-red-500 text-sm mt-1">{errors.banco}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tipo de Persona
              </label>
              <select
                name="tipoPersona"
                value={formData.tipoPersona}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
              >
                <option value="natural">Natural</option>
                <option value="juridica">Jurídica</option>
              </select>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tipo de Documento
                </label>
                <select
                  name="tipoDocumento"
                  value={formData.tipoDocumento}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-cosmic-base"
                >
                  <option value="CC">Cédula de Ciudadanía</option>
                  <option value="CE">Cédula de Extranjería</option>
                  <option value="NIT">NIT</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Número de Documento *
                </label>
                <input
                  type="text"
                  name="numeroDocumento"
                  value={formData.numeroDocumento}
                  onChange={handleChange}
                  className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                    errors.numeroDocumento ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                  }`}
                  placeholder="1234567890"
                />
                {errors.numeroDocumento && (
                  <p className="text-red-500 text-sm mt-1">{errors.numeroDocumento}</p>
                )}
              </div>
            </div>
          </div>
        )}

        {paymentMethod === 'Nequi' && (
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Número de Celular Nequi *
              </label>
              <input
                type="tel"
                name="numeroNequi"
                value={formData.numeroNequi}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, '');
                  if (value.length <= 10) {
                    handleChange({ target: { name: 'numeroNequi', value } });
                  }
                }}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                  errors.numeroNequi ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-cosmic-base'
                }`}
                placeholder="3001234567"
                maxLength="10"
              />
              {errors.numeroNequi && (
                <p className="text-red-500 text-sm mt-1">{errors.numeroNequi}</p>
              )}
            </div>

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <p className="text-sm text-blue-800">
                📱 Recibirás una notificación push en tu app Nequi para autorizar el pago.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Términos y condiciones */}
      <div className="bg-white rounded-xl shadow-md p-6">
        <label className="flex items-start space-x-3 cursor-pointer">
          <input
            type="checkbox"
            required
            className="mt-1 w-5 h-5 text-cosmic-base focus:ring-cosmic-base border-gray-300 rounded"
          />
          <span className="text-sm text-gray-600">
            Acepto los{' '}
            <a href="#" className="text-cosmic-base hover:underline">
              términos y condiciones
            </a>{' '}
            y la{' '}
            <a href="#" className="text-cosmic-base hover:underline">
              política de privacidad
            </a>{' '}
            de TravelGo
          </span>
        </label>
      </div>

      {/* Botón de pago */}
      <button
        type="submit"
        disabled={loading}
        className="w-full bg-flame-base hover:bg-flame-dark text-white font-bold py-4 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
      >
        {loading ? (
          <>
            <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
            </svg>
            <span>Procesando pago...</span>
          </>
        ) : (
          <>
            <DollarSign className="w-5 h-5" />
            <span>Pagar Ahora</span>
          </>
        )}
      </button>
    </form>
  );
}