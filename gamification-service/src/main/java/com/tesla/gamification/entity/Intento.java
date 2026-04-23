package com.tesla.gamification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "intento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intento")
    private Integer idIntento;

    @Column(name = "id_usuario")
    private Long usuarioId; // Desacoplado de Usuario

    @Column(name = "id_leccion")
    private Long leccionId; // Desacoplado de Leccion

    private Integer puntaje;

    @Column(name = "is_primer_intento")
    @Builder.Default
    private Boolean isPrimerIntento = true;

    @CreationTimestamp
    private LocalDateTime fecha;
}