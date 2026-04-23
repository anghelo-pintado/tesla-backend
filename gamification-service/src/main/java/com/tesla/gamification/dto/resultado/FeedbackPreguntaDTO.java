package com.tesla.gamification.dto.resultado;

public record FeedbackPreguntaDTO(
        Integer idPregunta,
        boolean correcta,
        Integer idAlternativaCorrecta,
        String solucionTexto,
        String solucionImagenUrl
) {}