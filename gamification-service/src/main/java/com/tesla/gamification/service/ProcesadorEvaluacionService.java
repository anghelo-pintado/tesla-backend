package com.tesla.gamification.service;

import com.tesla.gamification.config.RabbitMQConfig;
import com.tesla.gamification.dto.EvaluacionCompletadaEvent;
import com.tesla.gamification.entity.Intento;
import com.tesla.gamification.entity.ProgresoLecciones;
import com.tesla.gamification.entity.ProgresoLeccionesId;
import com.tesla.gamification.repository.IntentoRepository;
import com.tesla.gamification.repository.ProgresoLeccionesRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    // ¡Agregamos Redis!
    @Autowired
    private RankingRedisService rankingRedisService;

    /**
     * @RabbitListener hace que este método se ejecute AUTOMÁTICAMENTE
     * en cuanto llega un mensaje JSON a RabbitMQ.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EVALUACIONES)
    @Transactional
    public void procesarPuntosDeEvaluacion(EvaluacionCompletadaEvent evento) {

        System.out.println("📩 [RabbitMQ] Mensaje recibido para Usuario ID: " + evento.usuarioId());

        // 1. Calcular y otorgar Experiencia (Solo si es el primer intento)
        int expGanada = 0;
        if (evento.esPrimerIntento()) {
            expGanada = evento.respuestasCorrectas() * 30; // 30 EXP por respuesta correcta

            if (expGanada > 0) {
                // A. Actualiza la tabla vieja y el estado de la mascota
                estadisticaService.actualizarProgreso(evento.usuarioId(), expGanada);

                // B. ¡Sube los puntos a la RAM (Redis) al instante!
                rankingRedisService.sumarExperiencia(evento.usuarioId(), expGanada);

                System.out.println("🏆 " + expGanada + " EXP agregada a Redis para el Usuario: " + evento.usuarioId());
            }
        }

        // 2. Registrar el Intento en Postgres
        Intento intento = new Intento();
        intento.setUsuarioId(evento.usuarioId());
        intento.setLeccionId(evento.leccionId());
        intento.setPuntaje(evento.respuestasCorrectas());
        intento.setIsPrimerIntento(evento.esPrimerIntento());
        intentoRepository.save(intento);

        // 3. Actualizar el Progreso General de la Lección en Postgres
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