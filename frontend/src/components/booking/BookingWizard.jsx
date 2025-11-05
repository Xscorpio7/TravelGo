import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, Plane, Hotel, Car, CreditCard } from 'lucide-react';

export default function BookingWizard({ currentStep, onStepChange }) {
  const steps = [
    { id: 1, name: 'Vuelo', icon: Plane },
    { id: 2, name: 'Hotel', icon: Hotel },
    { id: 3, name: 'Transporte', icon: Car },
    { id: 4, name: 'Pago', icon: CreditCard },
  ];

  return (
    <div className="w-full py-8 bg-white shadow-lg rounded-xl mb-6">
      <div className="max-w-4xl mx-auto px-4">
        <div className="flex items-center justify-between">
          {steps.map((step, index) => {
            const Icon = step.icon;
            const isActive = currentStep === step.id;
            const isCompleted = currentStep > step.id;
            
            return (
              <div key={step.id} className="flex items-center flex-1">
                <div className="flex flex-col items-center flex-1">
                  {/* Circle */}
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center transition-all duration-300 ${
                      isCompleted
                        ? 'bg-cosmic-base text-white'
                        : isActive
                        ? 'bg-flame-base text-white ring-4 ring-flame-light'
                        : 'bg-gray-200 text-gray-400'
                    }`}
                  >
                    {isCompleted ? (
                      <Check className="w-6 h-6" />
                    ) : (
                      <Icon className="w-6 h-6" />
                    )}
                  </div>
                  
                  {/* Label */}
                  <span
                    className={`mt-2 text-sm font-medium ${
                      isActive ? 'text-flame-base' : isCompleted ? 'text-cosmic-base' : 'text-gray-400'
                    }`}
                  >
                    {step.name}
                  </span>
                </div>
                
                {/* Line connector */}
                {index < steps.length - 1 && (
                  <div
                    className={`h-1 flex-1 mx-2 transition-all duration-300 ${
                      isCompleted ? 'bg-cosmic-base' : 'bg-gray-200'
                    }`}
                  />
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}