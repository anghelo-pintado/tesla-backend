package com.tesla.gamification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "estadisticas_alumno")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasAlumno {

    @Id
    @Column(name = "id_usuario") // Ahora el ID es directamente el id del usuario
    private Long usuarioId;

    @Column(name = "racha_actual")
    private Integer rachaActual;

    @Column(name = "exp_total")
    private Integer expTotal;

    @Column(name = "exp_semanal")
    @Builder.Default
    private Integer expSemanal = 0;

    @Column(name = "estado_mascota")
    private String estadoMascota;

    @Column(name = "ultima_fecha_mision")
    private LocalDate ultimaFechaMision;

    @Column(name = "ranking_anterior")
    private Integer rankingAnterior;

    public void ganarExperiencia(int puntos) {
        if (puntos > 0) {
            this.expTotal = (this.expTotal == null ? 0 : this.expTotal) + puntos;
            this.expSemanal = (this.expSemanal == null ? 0 : this.expSemanal) + puntos;
        }
    }

    public void verificarYReiniciarRacha() {
        if (this.ultimaFechaMision != null) {
            LocalDate hoy = LocalDate.now();
            long diasDiferencia = ChronoUnit.DAYS.between(this.ultimaFechaMision, hoy);

            if (diasDiferencia > 1) {
                this.rachaActual = 0;
            }
        }
    }

    public void calcularEstado() {
        int exp = (this.expTotal != null) ? this.expTotal : 0;
        if (exp < 1250) this.estadoMascota = "Huevo";
        else if (exp < 2500) this.estadoMascota = "Agrietándose";
        else if (exp < 3750) this.estadoMascota = "Naciendo";
        else this.estadoMascota = "Completamente Crecido";
    }
}