package com.tesla.gamification.controller;

import com.tesla.gamification.entity.EstadisticasAlumno;
import com.tesla.gamification.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
public class EstadisticaController {

    @Autowired
    private EstadisticaService service;

    // CAMBIO AQUÍ: Integer a Long
    @GetMapping("/{idUsuario}")
    public ResponseEntity<EstadisticasAlumno> getStats(@PathVariable Long idUsuario) {
        System.out.println(">>> ENTRO A STATS <<<");
        return ResponseEntity.ok(service.obtenerPorId(idUsuario));
    }

    // CAMBIO AQUÍ: Integer a Long
    @PostMapping("/{idUsuario}/mision-completa")
    public ResponseEntity<EstadisticasAlumno> completarMision(
            @PathVariable Long idUsuario,
            @RequestBody Map<String, Integer> payload) {

        int expGanada = payload.get("exp");
        return ResponseEntity.ok(service.actualizarProgreso(idUsuario, expGanada));
    }
}