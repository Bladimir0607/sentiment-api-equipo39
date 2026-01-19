package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health check del servicio")
public class HealthController {

    @Operation(
            summary = "Verificar estado del servicio"
    )
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(Constants.HEALTH_OK);
    }
}
