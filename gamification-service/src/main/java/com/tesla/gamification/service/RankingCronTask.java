package com.tesla.gamification.service;

import com.tesla.gamification.entity.EstadisticasAlumno;
import com.tesla.gamification.entity.HistorialRanking;
import com.tesla.gamification.repository.EstadisticasAlumnoRepository;
import com.tesla.gamification.repository.HistorialRankingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class RankingCronTask {

    @Autowired private EstadisticasAlumnoRepository estadisticasRepository;
    @Autowired private HistorialRankingRepository historialRepository;

    @Scheduled(cron = "0 0 0 * * MON", zone = "America/Lima")
    public void reiniciarTorneoSemanal() {
        log.info("⏰ Iniciando guardado de Historial y reinicio semanal...");
        try {
            List<EstadisticasAlumno> rankingActual = estadisticasRepository.findAllByOrderByExpSemanalDesc();

            LocalDate fechaFin = LocalDate.now(ZoneId.of("America/Lima"));
            int mes = fechaFin.getMonthValue();
            int anio = fechaFin.getYear();

            int posicionGuardado = 1;
            for (EstadisticasAlumno alumno : rankingActual) {
                if (alumno.getExpSemanal() != null && alumno.getExpSemanal() > 0) {
                    HistorialRanking registro = HistorialRanking.builder()
                            .usuarioId(alumno.getUsuarioId()) // CORREGIDO AQUÍ
                            .expObtenida(alumno.getExpSemanal())
                            .posicion(posicionGuardado)
                            .fechaFinSemana(fechaFin)
                            .mes(mes)
                            .anio(anio)
                            .build();
                    historialRepository.save(registro);
                }
                posicionGuardado++;
            }

            int posicionActual = 1;
            for (EstadisticasAlumno alumno : rankingActual) {
                if (alumno.getExpSemanal() == null || alumno.getExpSemanal() == 0) {
                    alumno.setRankingAnterior(0);
                } else {
                    alumno.setRankingAnterior(posicionActual);
                }

                alumno.setExpSemanal(0);
                posicionActual++;
            }

            estadisticasRepository.saveAll(rankingActual);

            log.info("✅ Historial COMPLETO guardado, ranking anterior actualizado y torneo semanal reiniciado a 0.");

        } catch (Exception e) {
            log.error("❌ Error al reiniciar el ranking semanal: {}", e.getMessage(), e);
        }
    }
}