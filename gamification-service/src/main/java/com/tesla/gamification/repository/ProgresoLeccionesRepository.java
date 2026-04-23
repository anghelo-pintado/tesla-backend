package com.tesla.gamification.repository;

import com.tesla.gamification.entity.ProgresoLecciones;
import com.tesla.gamification.entity.ProgresoLeccionesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgresoLeccionesRepository extends JpaRepository<ProgresoLecciones, ProgresoLeccionesId> {

    // Método autogenerado por Spring Data JPA (sin necesidad de @Query)
    List<ProgresoLecciones> findByUsuarioId(Long usuarioId);
}