import axios from "axios";

const BASE_URL = 'http://140.84.161.47:8080'; 
const API_URL = `${BASE_URL}/auth`;

export const login = async (username, password) => {
    const response = await axios.post(`${API_URL}/login`, { username, password });
    return response.data; 
};

export const register = async (userData) => {
    const response = await axios.post(`${API_URL}/register`, userData);
    return response.data;
};
