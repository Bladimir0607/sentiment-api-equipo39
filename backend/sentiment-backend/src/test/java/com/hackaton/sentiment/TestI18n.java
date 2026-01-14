package com.hackaton.sentiment;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Locale;

public class TestI18n {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestConfig.class);

        MessageSource messageSource = context.getBean(MessageSource.class);

        // Probar español
        String es = messageSource.getMessage(
                "error.text.required", null, new Locale("es")
        );
        System.out.println("ES: " + es);  // Debe mostrar: "El texto no puede estar vacío"

        // Probar inglés
        String en = messageSource.getMessage(
                "error.text.required", null, Locale.ENGLISH
        );
        System.out.println("EN: " + en);  // Debe mostrar: "Text cannot be empty"

        context.close();
    }
}