package com.tesla.gamification.course.dto;

public record CrearCursoDTO(
        String nombre,
        String descripcion,
        boolean isHabilitado
) {}