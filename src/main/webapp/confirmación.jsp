<%-- 
    Document   : confirmación
    Created on : 9/05/2025, 8:40:34 p. m.
    Author     : Alejo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro Exitoso | Travel Go</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        @media (prefers-color-scheme: dark) {
            .dark\:bg-cosmic-dark {
                background-color: #361c34;
            }
            .dark\:text-light {
                color: #f2f6fc;
            }
            .dark\:border-cosmic-dark {
                border-color: #804b7e;
            }
            .dark\:bg-flamepea-dark {
                background-color: #3f1610;
            }
            .dark\:hover\:bg-flamepea-dark:hover {
                background-color: #dd5c49;
            }
            .dark\:shadow-cosmic {
                box-shadow: 0 4px 6px -1px rgba(128, 75, 126, 0.2), 0 2px 4px -1px rgba(128, 75, 126, 0.12);
            }
        }
        
        .success-icon {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }
        
        .success-icon::before {
            content: "";
            position: absolute;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            opacity: 0.2;
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0% {
                transform: scale(0.95);
                opacity: 0.2;
            }
            70% {
                transform: scale(1.1);
                opacity: 0.1;
            }
            100% {
                transform: scale(0.95);
                opacity: 0.2;
            }
        }
    </style>
</head>
<body class="min-h-screen bg-gradient-to-br from-[#f2f6fc] to-[#5b8bd6] dark:bg-cosmic-dark dark:text-light font-['Roboto'] transition-colors duration-300">
    <div class="container mx-auto px-4 py-16 flex flex-col items-center justify-center min-h-screen">
        <div class="w-full max-w-md bg-white dark:bg-[#212b4a] rounded-xl shadow-xl p-8 md:p-10 transition-all duration-300 transform hover:scale-[1.01]">
            <!-- Success Icon with Animation -->
            <div class="flex justify-center mb-6">
                <div class="success-icon bg-[#b97cb9] dark:bg-[#804b7e] text-white" style="box-shadow: 0 0 0 10px rgba(185, 124, 185, 0.2);">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                </div>
            </div>
            
            <!-- Success Message -->
            <h1 class="text-3xl md:text-4xl font-bold text-center text-[#361c34] dark:text-[#f2f6fc] mb-4">
                ¡Tu cuenta ${nombreUsuario} ha sido creada exitosamente!
            </h1>
            
            <p class="text-lg text-center text-gray-600 dark:text-gray-300 mb-8">
                Gracias por registrarte en Travel Go. Ya puedes iniciar sesión y comenzar a planear tu próxima aventura.
            </p>
            
            <!-- Action Buttons -->
            <div class="flex flex-col space-y-4">
                <a href="./src/login.html" class="bg-[#dd5c49] hover:bg-[#c04a38] dark:hover:bg-[#dd5c49] text-white font-bold py-3 px-6 rounded-lg text-center transition-colors duration-300 shadow-md hover:shadow-lg transform hover:-translate-y-1">
                    Ir a iniciar sesión
                </a>
                
                <a href="./src/principal.html" class="text-[#5b8bd6] dark:text-[#b97cb9] hover:text-[#361c34] dark:hover:text-[#f2f6fc] font-medium text-center transition-colors duration-300">
                    Volver al Inicio
                </a>
            </div>
            
            <!-- Decorative Elements -->
            <div class="mt-10 flex justify-center space-x-2">
                <div class="w-3 h-3 rounded-full bg-[#b97cb9] dark:bg-[#dd5c49] opacity-70"></div>
                <div class="w-3 h-3 rounded-full bg-[#dd5c49] dark:bg-[#b97cb9] opacity-70"></div>
                <div class="w-3 h-3 rounded-full bg-[#5b8bd6] dark:bg-[#804b7e] opacity-70"></div>
            </div>
        </div>
        
        <!-- Footer -->
        <p class="mt-8 text-sm text-black-800 dark:text-gray-400 text-center">
            © 2025 Travel Go.
        </p>
    </div>
</body>
</html>