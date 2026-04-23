package com.tesla.gamification.repository;

import com.tesla.gamification.entity.EstadisticasAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadisticasAlumnoRepository extends JpaRepository<EstadisticasAlumno, Long> {

    // Ya no podemos hacer JOIN FETCH con usuario.
    @Query("SELECT e FROM EstadisticasAlumno e ORDER BY e.expSemanal DESC")
    List<EstadisticasAlumno> findAllByOrderByExpSemanalDesc();

    @Modifying
    @Query("UPDATE EstadisticasAlumno e SET e.expSemanal = 0")
    void reiniciarExperienciaSemanal();
}