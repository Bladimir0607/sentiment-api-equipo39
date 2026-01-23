import axios from "axios";

const API_URL = 'http://localhost:8080/api/i18n';

export const getFullDictionary = async (lang) => {
    try {
        const response = await axios.get(`${API_URL}/translations/${lang}`);
        return response.data; // Devuelve el mapa { "button.analyze": "Analizar", ... }
    } catch (error) {
        console.error("Error fetching translations", error);
    }
};