package com.tesla.gamification.service;

import com.tesla.gamification.entity.EstadisticasAlumno;
import com.tesla.gamification.repository.EstadisticasAlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EstadisticaService {

    @Autowired
    private EstadisticasAlumnoRepository repository;

    @Transactional
    public EstadisticasAlumno obtenerPorId(Long usuarioId) {
        EstadisticasAlumno stats = repository.findById(usuarioId).orElseGet(() -> {
            // Ya no buscamos en UsuarioRepository. Creamos directamente el registro.
            EstadisticasAlumno nueva = EstadisticasAlumno.builder()
                    .usuarioId(usuarioId)
                    .rachaActual(0)
                    .expTotal(0)
                    .expSemanal(0)
                    .estadoMascota("Huevo")
                    .ultimaFechaMision(LocalDate.now().minusDays(1))
                    .build();

            return repository.save(nueva);
        });

        stats.verificarYReiniciarRacha();
        stats.calcularEstado();
        return repository.save(stats);
    }

    @Transactional
    public EstadisticasAlumno actualizarProgreso(Long usuarioId, int puntosExp) {
        EstadisticasAlumno stats = obtenerPorId(usuarioId);
        LocalDate hoy = LocalDate.now();
        LocalDate ultimaVez = stats.getUltimaFechaMision();

        if (ultimaVez != null) {
            long diasDiferencia = ChronoUnit.DAYS.between(ultimaVez, hoy);
            if (diasDiferencia == 1) {
                stats.setRachaActual(stats.getRachaActual() + 1);
            } else if (diasDiferencia > 1) {
                stats.setRachaActual(1);
            }
        } else {
            stats.setRachaActual(1);
        }

        stats.setExpTotal((stats.getExpTotal() != null ? stats.getExpTotal() : 0) + puntosExp);
        stats.setExpSemanal((stats.getExpSemanal() != null ? stats.getExpSemanal() : 0) + puntosExp);

        stats.setUltimaFechaMision(hoy);
        stats.calcularEstado();

        return repository.save(stats);
    }
}