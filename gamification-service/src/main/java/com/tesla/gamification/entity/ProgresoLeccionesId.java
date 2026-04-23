package com.tesla.gamification.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProgresoLeccionesId implements Serializable {

    private Long usuarioId;
    private Long leccionId;

    // Constructor vacío (Obligatorio para JPA)
    public ProgresoLeccionesId() {
    }

    // Constructor con parámetros
    public ProgresoLeccionesId(Long usuarioId, Long leccionId) {
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
    }

    // --- Getters y Setters ---

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLeccionId() {
        return leccionId;
    }

    public void setLeccionId(Long leccionId) {
        this.leccionId = leccionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgresoLeccionesId that = (ProgresoLeccionesId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
                Objects.equals(leccionId, that.leccionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, leccionId);
    }
}