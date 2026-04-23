package com.tesla.gamification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "historial_ranking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId; // Desacoplado de Usuario

    @Column(name = "exp_obtenida", nullable = false)
    private Integer expObtenida;

    @Column(name = "posicion", nullable = false)
    private Integer posicion;

    @Column(name = "fecha_fin_semana", nullable = false)
    private LocalDate fechaFinSemana;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;
}