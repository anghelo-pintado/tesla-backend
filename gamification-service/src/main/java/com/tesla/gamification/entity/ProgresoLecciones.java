package com.tesla.gamification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progreso_lecciones")
@IdClass(ProgresoLeccionesId.class)
@Data
@NoArgsConstructor
public class ProgresoLecciones {

    @Id
    @Column(name = "id_usuario")
    private Long usuarioId;

    @Id
    @Column(name = "id_leccion")
    private Long leccionId;

    @Column(name = "completada")
    private Boolean completada = false;

    @Column(name = "progreso_porcentaje")
    private Integer progresoPorcentaje = 0;
}