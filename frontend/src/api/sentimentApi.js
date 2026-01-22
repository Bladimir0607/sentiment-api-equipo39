import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
});

// Agrega el token a TODAS las peticiones automáticamente
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`; 
    }  
    return config;
}, (error) => {
    return Promise.reject(error);
});

export const analyzeText = async (text, language = 'es') => {
    const response = await api.post('/sentiment', { text }, {
        headers: { 'Accept-Language': language }
    });
    return response.data;
};

export const getStats = async () => {
    const response = await api.get('/sentiment/stats');
    return response.data;
};

export default api;