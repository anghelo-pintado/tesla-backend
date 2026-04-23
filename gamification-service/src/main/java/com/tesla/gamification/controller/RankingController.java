package com.tesla.gamification.controller;

import com.tesla.gamification.dto.HistorialRankingDTO;
import com.tesla.gamification.entity.EstadisticasAlumno;
import com.tesla.gamification.entity.HistorialRanking;
import com.tesla.gamification.repository.EstadisticasAlumnoRepository;
import com.tesla.gamification.repository.HistorialRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    @Autowired
    private HistorialRankingRepository historialRepository;

    @Autowired
    private EstadisticasAlumnoRepository estadisticasRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerRankingGeneral(@RequestParam(required = false) Long userId) {
        List<EstadisticasAlumno> todosLosAlumnos = estadisticasRepository.findAllByOrderByExpSemanalDesc();
        List<Map<String, Object>> respuesta = new ArrayList<>();
        int posicion = 1;

        for (EstadisticasAlumno alumno : todosLosAlumnos) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("idUsuario", alumno.getUsuarioId()); // CORREGIDO
            dto.put("posicion", posicion);
            dto.put("nombreCompleto", "Usuario #" + alumno.getUsuarioId()); // CORREGIDO

            int expSemanal = (alumno.getExpSemanal() != null) ? alumno.getExpSemanal() : 0;
            dto.put("expParaRanking", expSemanal);
            dto.put("expTotal", expSemanal);
            dto.put("experiencia", expSemanal);

            int rankAnt = (alumno.getRankingAnterior() != null) ? alumno.getRankingAnterior() : 0;
            dto.put("rankingAnterior", rankAnt);

            boolean esUsuarioActual = (userId != null && alumno.getUsuarioId().equals(userId));
            dto.put("esUsuarioActual", esUsuarioActual);

            respuesta.add(dto);
            posicion++;
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/semanal")
    public ResponseEntity<List<Map<String, Object>>> obtenerRankingActual(@RequestParam(required = false) Long userId) {
        List<EstadisticasAlumno> rankingActual = estadisticasRepository.findAllByOrderByExpSemanalDesc();
        List<Map<String, Object>> respuesta = new ArrayList<>();
        int posicion = 1;

        for (EstadisticasAlumno alumno : rankingActual) {
            if (alumno.getExpSemanal() != null) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("idUsuario", alumno.getUsuarioId()); // CORREGIDO
                dto.put("posicion", posicion);
                dto.put("nombreCompleto", "Usuario #" + alumno.getUsuarioId()); // CORREGIDO
                dto.put("expParaRanking", alumno.getExpSemanal());

                int rankAnt = (alumno.getRankingAnterior() != null) ? alumno.getRankingAnterior() : 0;
                dto.put("rankingAnterior", rankAnt);

                boolean esUsuarioActual = (userId != null && alumno.getUsuarioId().equals(userId));
                dto.put("esUsuarioActual", esUsuarioActual);

                respuesta.add(dto);
                posicion++;
            }
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialRankingDTO>> obtenerHistorial(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {

        List<HistorialRanking> historial = historialRepository.findByMesAndAnio(mes, anio);
        List<HistorialRankingDTO> dtos = historial.stream().map(h -> new HistorialRankingDTO(
                h.getIdHistorial(),
                "Usuario #" + h.getUsuarioId(), // CORREGIDO
                h.getExpObtenida(),
                h.getPosicion(),
                h.getFechaFinSemana()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/historial/fechas")
    public ResponseEntity<List<LocalDate>> obtenerFechasHistorial() {
        return ResponseEntity.ok(historialRepository.obtenerFechasDisponibles());
    }

    @GetMapping("/historial/admin")
    public ResponseEntity<List<Map<String, Object>>> obtenerRankingPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<HistorialRanking> historial = historialRepository.findByFechaFinSemanaOrderByPosicionAsc(fecha);
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (HistorialRanking h : historial) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("posicion", h.getPosicion());
            dto.put("nombreCompleto", "Usuario #" + h.getUsuarioId()); // CORREGIDO
            dto.put("expParaRanking", h.getExpObtenida());
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }
}