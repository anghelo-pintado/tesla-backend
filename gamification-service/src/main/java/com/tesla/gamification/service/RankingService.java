package com.tesla.gamification.service;

import com.tesla.gamification.dto.RankingItemDTO;
import com.tesla.gamification.dto.UsuarioInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RankingService {

    // ¡ADÍOS POSTGRES, HOLA REDIS!
    @Autowired
    private RankingRedisService rankingRedisService;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public List<RankingItemDTO> obtenerRanking(Long idUsuarioLogueado) {

        // 1. Obtenemos el Top 100 directo desde la memoria RAM (¡Ultrarrápido!)
        Set<ZSetOperations.TypedTuple<String>> rankingRedis = rankingRedisService.obtenerTop100();

        if (rankingRedis == null || rankingRedis.isEmpty()) {
            return new ArrayList<>(); // Si el ranking está vacío, devolvemos lista vacía
        }

        // 2. Extraemos los IDs para preguntar por sus nombres
        List<Long> idsUsuarios = rankingRedis.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .collect(Collectors.toList());

        // 3. Llamamos al Auth Service para pedir los nombres (comunicación entre microservicios)
        String authServiceUrl = "http://localhost:8081/api/v1/users/nombres?ids=" +
                idsUsuarios.stream().map(String::valueOf).collect(Collectors.joining(","));

        UsuarioInfoDTO[] usuariosArray = new UsuarioInfoDTO[0];
        try {
            usuariosArray = restTemplate.getForObject(authServiceUrl, UsuarioInfoDTO[].class);
        } catch (Exception e) {
            System.out.println("No se pudo contactar al Auth Service, se usarán valores por defecto.");
        }

        Map<Long, UsuarioInfoDTO> mapaUsuarios = List.of(usuariosArray).stream()
                .collect(Collectors.toMap(UsuarioInfoDTO::idUsuario, u -> u));

        // 4. Armamos la respuesta para el Frontend
        List<RankingItemDTO> rankingDTOs = new ArrayList<>();
        int posicion = 1;

        for (ZSetOperations.TypedTuple<String> tuple : rankingRedis) {

            Long idUsuario = Long.parseLong(tuple.getValue());
            int experiencia = tuple.getScore() != null ? tuple.getScore().intValue() : 0;

            UsuarioInfoDTO infoDelaOtraAPI = mapaUsuarios.getOrDefault(idUsuario,
                    new UsuarioInfoDTO(idUsuario, "Usuario", "#" + idUsuario));

            String nombre = infoDelaOtraAPI.nombre();
            String apellido = infoDelaOtraAPI.apellido();
            String nombreCompleto = (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");

            String inicial = (nombre != null && !nombre.isEmpty())
                    ? nombre.substring(0, 1).toUpperCase()
                    : "?";

            boolean esYo = idUsuarioLogueado != null && idUsuario.equals(idUsuarioLogueado);

            // La tendencia la dejamos en 0 por ahora para simplificar el despliegue
            int tendencia = 0;

            RankingItemDTO dto = new RankingItemDTO(
                    idUsuario,
                    nombreCompleto.trim(),
                    inicial,
                    experiencia,
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