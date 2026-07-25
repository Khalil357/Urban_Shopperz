/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  safelist: [
    'bg-blue-600', 'hover:bg-blue-700', 'text-blue-600',
    'bg-red-600', 'hover:bg-red-700', 'text-red-600',
    'bg-green-600', 'hover:bg-green-700', 'text-green-600',
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
