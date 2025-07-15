export default function Error404(){
    return (
        <div className="h-screen bg-gradient-to-br from-[#391e37] to-[#b97cb9] dark:bg-cosmic-dark dark:text-light">
        <div className="container mx-auto px-4 py-8 flex flex-col items-center justify-center min-h-screen">
            <h1 className="text-6xl font-bold text-[#361c34] dark:text-[#f2f6fc] mb-4">404</h1>
            <p className="text-xl text-[#361c34] dark:text-[#f2f6fc] mb-8">Página no encontrada</p>
            <a href="/" className="text-lg text-[#b97cb9] hover:underline">Volver al inicio</a>
        </div>
        </div>
    );
}