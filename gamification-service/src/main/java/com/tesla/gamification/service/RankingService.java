package com.tesla.gamification.progress.service;

import com.tesla.gamification.progress.dto.RankingItemDTO;
import com.tesla.gamification.progress.entity.EstadisticasAlumno;
import com.tesla.gamification.progress.repository.EstadisticasAlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {

    @Autowired
    private EstadisticasAlumnoRepository estadisticasRepository;

    @Transactional(readOnly = true)
    public List<RankingItemDTO> obtenerRanking(Integer idUsuarioLogueado) {

        List<EstadisticasAlumno> listaOrdenada = estadisticasRepository.findAllByOrderByExpSemanalDesc();
        List<RankingItemDTO> rankingDTOs = new ArrayList<>();

        int posicion = 1;

        for (EstadisticasAlumno stats : listaOrdenada) {

            String nombre = stats.getUsuario().getNombre();
            String apellido = stats.getUsuario().getApellido();
            String nombreCompleto = (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");

            String inicial = (nombre != null && !nombre.isEmpty())
                    ? nombre.substring(0, 1).toUpperCase()
                    : "?";

            int rankAnt = stats.getRankingAnterior() != null ? stats.getRankingAnterior() : posicion;
            int tendencia = rankAnt - posicion;

            boolean esYo = stats.getUsuario().getIdUsuario().equals(idUsuarioLogueado);

            Integer expParaRanking = stats.getExpSemanal() != null ? stats.getExpSemanal() : 0;

            RankingItemDTO dto = new RankingItemDTO(
                    stats.getUsuario().getIdUsuario(),
                    nombreCompleto.trim(),
                    inicial,
                    expParaRanking, // <- Ahora envía la EXP Semanal
                    posicion,
                    tendencia,
                    esYo
            );

            rankingDTOs.add(dto);
            posicion++;
        }

        return rankingDTOs;
    }
}