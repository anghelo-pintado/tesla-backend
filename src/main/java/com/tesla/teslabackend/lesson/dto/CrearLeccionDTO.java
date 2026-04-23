package com.tesla.gamification.lesson.dto;

public record CrearLeccionDTO(
        Integer idSemana,
        String nombre,
        String descripcion,
        Integer orden
) {}