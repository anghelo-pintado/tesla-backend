package com.tesla.gamification.dto;

public record EvaluacionCompletadaEvent(
        Long usuarioId,
        Long leccionId,
        int respuestasCorrectas,
        int totalPreguntas,
        boolean esPrimerIntento
) {}