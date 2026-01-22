import React, { createContext, useContext, useState, useEffect } from "react";
import { getFullDictionary } from "../api/i18nApi";
import i18n from "../i18n"; 

const LanguageContext = createContext();

export const LanguageProvider = ({ children }) => { 
    const [lang, setLang] = useState(localStorage.getItem('lang') || 'es');
    const [texts, setTexts] = useState({});
    const [loading, setLoading] = useState(true);

    const loadTranslations = async (targetLang) => {
        setLoading(true);
        try {
            const data = await getFullDictionary(targetLang);
            if (data) setTexts(data);
        } catch (error) {
            console.error("Error cargando traducciones del backend", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadTranslations(lang);
        i18n.changeLanguage(lang); // Sincroniza i18next
    }, [lang]);

    const changeLanguage = (newLang) => {
        setLang(newLang);
        localStorage.setItem('lang', newLang);
    };

    const t = (key) => {
        // Prioridad 1: i18next (Interfaz local)
        if (i18n.exists(key)) return i18n.t(key);
        
        // Prioridad 2: Backend (Mensajes del sistema)
        if (texts[key]) return texts[key];

        // Fallback
        return key.split('.').pop().toUpperCase();
    };

    return (
        <LanguageContext.Provider value={{ lang, changeLanguage, t, loading }}>
            {children}
        </LanguageContext.Provider>
    );
};

export const useI18n = () => useContext(LanguageContext);