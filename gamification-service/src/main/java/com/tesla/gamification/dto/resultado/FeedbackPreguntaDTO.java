package com.tesla.gamification.progress.dto.resultado;

public record FeedbackPreguntaDTO(
        Integer idPregunta,
        boolean correcta,
        Integer idAlternativaCorrecta,
        String solucionTexto,
        String solucionImagenUrl
) {}