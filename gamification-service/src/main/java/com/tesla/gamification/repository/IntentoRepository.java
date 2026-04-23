package com.tesla.gamification.repository;

import com.tesla.gamification.entity.Intento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntentoRepository extends JpaRepository<Intento, Integer> {

    boolean existsByUsuarioIdAndLeccionId(Long usuarioId, Long leccionId);

    // Cambiamos a los campos directos de la entidad
    @Query("SELECT i.leccionId, i.puntaje FROM Intento i WHERE i.usuarioId = :idUsuario AND i.isPrimerIntento = true")
    List<Object[]> findPuntajesByUsuario(@Param("idUsuario") Long idUsuario);
}