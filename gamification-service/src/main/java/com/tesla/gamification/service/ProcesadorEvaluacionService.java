package com.tesla.gamification.service;

import com.tesla.gamification.dto.EvaluacionCompletadaEvent;
import com.tesla.gamification.entity.Intento;
import com.tesla.gamification.entity.ProgresoLecciones;
import com.tesla.gamification.entity.ProgresoLeccionesId;
import com.tesla.gamification.repository.IntentoRepository;
import com.tesla.gamification.repository.ProgresoLeccionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcesadorEvaluacionService {

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private IntentoRepository intentoRepository;

    @Autowired
    private ProgresoLeccionesRepository progresoRepository;

    @Transactional
    public void procesarPuntosDeEvaluacion(EvaluacionCompletadaEvent evento) {

        // 1. Calcular y otorgar Experiencia (Solo si es el primer intento)
        int expGanada = 0;
        if (evento.esPrimerIntento()) {
            expGanada = evento.respuestasCorrectas() * 30; // 30 EXP por respuesta correcta
            if (expGanada > 0) {
                // Llamamos a tu otro servicio para que actualice la tabla y el estado de la mascota
                estadisticaService.actualizarProgreso(evento.usuarioId(), expGanada);
            }
        }

        // 2. Registrar el Intento
        Intento intento = new Intento();
        intento.setUsuarioId(evento.usuarioId());
        intento.setLeccionId(evento.leccionId());
        intento.setPuntaje(evento.respuestasCorrectas());
        intento.setIsPrimerIntento(evento.esPrimerIntento());
        intentoRepository.save(intento);

        // 3. Actualizar el Progreso General de la Lección
        ProgresoLeccionesId idCompuesto = new ProgresoLeccionesId();
        idCompuesto.setUsuarioId(evento.usuarioId());
        idCompuesto.setLeccionId(evento.leccionId());

        ProgresoLecciones progreso = progresoRepository.findById(idCompuesto)
                .orElse(new ProgresoLecciones());

        progreso.setUsuarioId(evento.usuarioId());
        progreso.setLeccionId(evento.leccionId());
        progreso.setCompletada(true);

        int porcentaje = evento.totalPreguntas() > 0 ? (evento.respuestasCorrectas() * 100 / evento.totalPreguntas()) : 0;
        progreso.setProgresoPorcentaje(Math.max(
                progreso.getProgresoPorcentaje() != null ? progreso.getProgresoPorcentaje() : 0,
                porcentaje
        ));

        progresoRepository.save(progreso);
    }
}