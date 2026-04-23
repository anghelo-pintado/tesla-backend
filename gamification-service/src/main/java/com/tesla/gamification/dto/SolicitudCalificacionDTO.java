package com.tesla.gamification.progress.dto;

import java.util.List;

public record SolicitudCalificacionDTO(
        Integer idUsuario,
        List<RespuestaAlumnoDTO> respuestas
) {}