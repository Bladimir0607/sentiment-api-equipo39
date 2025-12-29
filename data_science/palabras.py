import pandas as pd
def explicar_prediccion(modelo, texto_entrada, prediccion_etiqueta):
    """
    Devuelve las palabras que fueron más significativas en la decisión.
    """
    vectorizador = modelo.named_steps['tfidf']
    clasificador = modelo.named_steps['clf']
    top_palabras = vectorizador.get_feature_names_out()

    #Transformar este texto
    tfidf_vector = vectorizador.transform([texto_entrada])
    
    #Obtener índices de las palabras que existen en este texto 
    coordenadas = tfidf_vector.nonzero()
    indices = coordenadas[1] # Los índices de las columnas (palabras)
    
    datos = []
    
    #Impacto de cada palabra
    for idx in indices:
        palabra = top_palabras[idx]
        val_tfidf = tfidf_vector[0, idx]
        coeficiente = clasificador.coef_[0][idx]
        
        # El impacto de cuanto influye esa palabra hacia positivo o negativo
        impacto = val_tfidf * coeficiente
        datos.append({'palabra': palabra, 'impacto': impacto})
        
    datos_impacto = pd.DataFrame(datos)
    
    if datos_impacto.empty:
        return []

    #Filtrar según la predicción
    if prediccion_etiqueta == "Positivo":
        #Palabras con mayor impacto positivo 
        palabras = datos_impacto.sort_values(by='impacto', ascending=False).head(3)
    else:
        #Palabras con mayor impacto negativo
        palabras = datos_impacto.sort_values(by='impacto', ascending=True).head(3)
    
    #Retornar lista
    return palabras['palabra'].tolist()