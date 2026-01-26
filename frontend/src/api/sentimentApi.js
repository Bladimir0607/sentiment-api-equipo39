import axios from "axios";

const api = axios.create({
    baseURL: 'http://140.84.161.47:8080',
});

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

// Estadísticas globales (Para el ADMIN)
export const getStats = async () => {
    const response = await api.get('/sentiment/stats');
    return response.data;
};

// Historial personal (Para TODOS)
export const getHistory = async () => {
    const response = await api.get('/sentiment/my-analyses'); 
    return response.data;
};

// Análisis en LOTE (CSV)
export const analyzeBatch = async (file) => {
    const formData = new FormData();
    formData.append('file', file); 

    const response = await api.post('/sentiment/batch', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response.data;
};

export default api;