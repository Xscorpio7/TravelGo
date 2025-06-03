/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cosmic: {
          light: '#eedfef',
          base: '#b97cb9',
          dark: '#583756',
        },
        flame: {
          light: '#f9d4cf',
          base: '#dd5c49',
          dark: '#8d3427',
        },
        astronaut: {
          light: '#e1ebf8',
          base: '#5b8bd6',
          dark: '#212b4a',
        },
      },
      fontFamily: {
        sans: ['"Open Sans"', 'sans-serif'],
      },
      boxShadow: {
        search: '0 10px 30px rgba(0, 0, 0, 0.1)',
        testimonial: '0 5px 15px rgba(0, 0, 0, 0.05)',
        blogHover: '0 10px 20px rgba(185, 124, 185, 0.2)',
      },
    },
  },
  plugins: [],
}

