package com.tesla.gamification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RankingRedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Esta es la "llave" bajo la cual guardaremos la tabla del torneo en Redis
    private static final String RANKING_KEY = "ranking:semanal";

    /**
     * Suma experiencia al usuario. Si el usuario no existe en Redis, lo crea.
     * Al usar ZSET, Redis reordena la tabla automáticamente en 1 milisegundo.
     */
    public void sumarExperiencia(Long usuarioId, double puntosGanados) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, usuarioId.toString(), puntosGanados);
    }

    /**
     * Obtiene el Top 100 de la semana, ordenado de mayor a menor puntaje.
     */
    public Set<ZSetOperations.TypedTuple<String>> obtenerTop100() {
        // reverseRangeWithScores obtiene de mayor a menor puntaje
        return redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, 0, 99);
    }

    /**
     * Limpia todo el ranking (Se usará el domingo a medianoche para reiniciar el torneo)
     */
    public void reiniciarRankingSemanal() {
        redisTemplate.delete(RANKING_KEY);
    }
}