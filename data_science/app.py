from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from palabras import explicar_prediccion
from typing import List
import joblib
import numpy as np
import os

# Definición de la app
app = FastAPI(title="Sentiment Analysis API")

# Cargar el modelo

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "models", "sentiment_model_1.2.joblib")

if not os.path.exists(MODEL_PATH):
    raise Exception(f"No se encontró el archivo del modelo en: {MODEL_PATH}")

modelo = joblib.load(MODEL_PATH)

class SentimentRequest(BaseModel):
    text: str

class SentimentResponse(BaseModel):
    prevision: str
    probabilidad: float
    
class SentimentResponseExplain(BaseModel):
    prevision: str
    probabilidad: float
    palabras_clave: List[str]

@app.post("/sentiment", response_model=SentimentResponse)
def predict_sentiment(request: SentimentRequest):
    texto = request.text.strip()

    if len(texto) < 10:
        raise HTTPException(
            status_code=400,
            detail="El texto es demasiado corto para analizar sentimiento."
        )

    pred = modelo.predict([texto])[0]
    proba = modelo.predict_proba([texto])[0]

    probabilidad_max = float(np.max(proba))

    sentimiento = "Positivo" if pred == 1 else "Negativo"

    return {
        "prevision": sentimiento,
        "probabilidad": round(probabilidad_max, 3)
    }


@app.post("/sentiment-explain", response_model=SentimentResponseExplain)
def predict_sentiment_explain(request: SentimentRequest):
    texto = request.text.strip()

    if len(texto) < 10:
        raise HTTPException(
            status_code=400,
            detail="El texto es demasiado corto para analizar sentimiento."
        )

    proba = modelo.predict_proba([texto])[0]
    prob_positivo = proba[1]
    
    UMBRAL = 0.65 #El mejor obtenido en el notebook

    if prob_positivo > UMBRAL:
        sentimiento = "Positivo"
        confianza_final = prob_positivo 
    else:
        sentimiento = "Negativo"
        confianza_final = proba[0]

    palabras_clave = explicar_prediccion(modelo, texto, sentimiento)

    return {
        "prevision": sentimiento,
        "probabilidad": round(float(confianza_final), 3),
        "palabras_clave": palabras_clave
    }