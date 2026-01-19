package com.hackaton.sentiment.util;

public final class SentimentLabels {

    private SentimentLabels() {}

    // Labels CANÓNICOS (lo que se guarda en DB)
    public static final String POSITIVE = "POSITIVE";
    public static final String NEGATIVE = "NEGATIVE";
   // public static final String NEUTRAL  = "NEUTRAL";

   // Metodo útil para validar si una etiqueta es válida
   public static boolean isValidLabel(String label) {
       return POSITIVE.equals(label) || NEGATIVE.equals(label);
   }
}
