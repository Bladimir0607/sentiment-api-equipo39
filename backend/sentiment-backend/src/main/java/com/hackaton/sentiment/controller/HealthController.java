package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para verificación del estado de salud (health check) del servicio.
 *
 * Este controlador proporciona un endpoint simple para monitorear la disponibilidad
 * y estado operativo del microservicio, comúnmente utilizado por sistemas de orquestación
 * de contenedores, balanceadores de carga y herramientas de monitoreo.
 *
 * El endpoint no requiere autenticación y retorna un mensaje constante que indica
 * el correcto funcionamiento del servicio.
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@RestController
@Tag(name = "Health", description = "Health check del servicio")
public class HealthController {

    /**
     * Verifica el estado operativo del servicio.
     *
     * Este endpoint es utilizado por sistemas externos para determinar si el
     * servicio está disponible y funcionando correctamente. Retorna un código
     * HTTP 200 junto con un mensaje de confirmación cuando el servicio está
     * operativo.
     *
     * @return ResponseEntity con código HTTP 200 y mensaje de estado saludable
     */
    @Operation(summary = "Verificar estado del servicio")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(Constants.HEALTH_OK);
    }
}
