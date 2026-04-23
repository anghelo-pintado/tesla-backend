package com.tesla.gamification.course.dto;

public record CursoDTO(
        Integer idCurso,
        String nombre,
        String descripcion,
        Boolean isHabilitado
) {
}
