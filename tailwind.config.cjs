/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        darkBg: '#0a0510',
        neonPurple: '#a855f7',
        neonGreen: '#10b981',
        scarlet: '#ff2400', // El verde escarlata/rojo vibrante para contrastes
      },
      backgroundImage: {
        'main-gradient': 'radial-gradient(circle at top left, rgba(168, 85, 247, 0.15), transparent), radial-gradient(circle at bottom right, rgba(16, 185, 129, 0.1), transparent)',
      }
    },
  },
  plugins: [],
}