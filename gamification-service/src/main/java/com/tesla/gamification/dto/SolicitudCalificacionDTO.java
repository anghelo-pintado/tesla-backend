package com.tesla.gamification.dto;

import java.util.List;

public record SolicitudCalificacionDTO(
        Integer idUsuario,
        List<RespuestaAlumnoDTO> respuestas
) {}