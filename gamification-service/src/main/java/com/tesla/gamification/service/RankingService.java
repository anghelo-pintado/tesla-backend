package com.tesla.gamification.service;

import com.tesla.gamification.dto.RankingItemDTO;
import com.tesla.gamification.dto.UsuarioInfoDTO;
import com.tesla.gamification.entity.EstadisticasAlumno;
import com.tesla.gamification.repository.EstadisticasAlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private EstadisticasAlumnoRepository estadisticasRepository;

    // Usamos RestTemplate para llamar a otros microservicios (O puedes usar OpenFeign)
    @Autowired
    private RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public List<RankingItemDTO> obtenerRanking(Long idUsuarioLogueado) {

        // 1. Obtienes tus estadísticas puras (Solo IDs y Puntos)
        List<EstadisticasAlumno> listaOrdenada = estadisticasRepository.findAllByOrderByExpSemanalDesc();

        // 2. Extraes solo los IDs para preguntar por ellos
        List<Long> idsUsuarios = listaOrdenada.stream()
                .map(EstadisticasAlumno::getUsuarioId)
                .collect(Collectors.toList());

        // 3. ¡LA MAGIA! Llamas al Auth Service para pedirle los nombres de esos IDs
        // Supongamos que el Auth Service corre en el puerto 8081 y tiene este endpoint:
        String authServiceUrl = "http://localhost:8081/api/v1/users/nombres?ids=" +
                idsUsuarios.stream().map(String::valueOf).collect(Collectors.joining(","));

        UsuarioInfoDTO[] usuariosArray = restTemplate.getForObject(authServiceUrl, UsuarioInfoDTO[].class);

        // Convertimos el array a un Mapa para buscar rapidísimo (ID -> Datos del Usuario)
        Map<Long, UsuarioInfoDTO> mapaUsuarios = List.of(usuariosArray).stream()
                .collect(Collectors.toMap(UsuarioInfoDTO::idUsuario, u -> u));

        // 4. AHORA SÍ: Reconstruimos tu lógica exacta para el Frontend
        List<RankingItemDTO> rankingDTOs = new ArrayList<>();
        int posicion = 1;

        for (EstadisticasAlumno stats : listaOrdenada) {

            // Buscamos los datos que nos prestó el Auth Service
            UsuarioInfoDTO infoDelaOtraAPI = mapaUsuarios.getOrDefault(stats.getUsuarioId(),
                    new UsuarioInfoDTO(stats.getUsuarioId(), "Usuario", "Desconocido"));

            String nombre = infoDelaOtraAPI.nombre();
            String apellido = infoDelaOtraAPI.apellido();
            String nombreCompleto = (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");

            // Tu lógica exacta para la inicial
            String inicial = (nombre != null && !nombre.isEmpty())
                    ? nombre.substring(0, 1).toUpperCase()
                    : "?";

            int rankAnt = stats.getRankingAnterior() != null ? stats.getRankingAnterior() : posicion;
            int tendencia = rankAnt - posicion;

            boolean esYo = idUsuarioLogueado != null && stats.getUsuarioId().equals(idUsuarioLogueado);

            Integer expParaRanking = stats.getExpSemanal() != null ? stats.getExpSemanal() : 0;

            RankingItemDTO dto = new RankingItemDTO(
                    stats.getUsuarioId(),
                    nombreCompleto.trim(),
                    inicial,
                    expParaRanking,
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