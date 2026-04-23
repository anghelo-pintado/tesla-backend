package com.tesla.gamification.lesson.repository;

import com.tesla.gamification.course.entity.Semana;
import com.tesla.gamification.lesson.entity.Leccion;
import com.tesla.gamification.lesson.entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeccionRepository extends JpaRepository<Leccion, Integer>{

    int countBySemana(Semana semana);

    @Query("""
SELECT l FROM Leccion l
JOIN FETCH l.preguntas p
JOIN FETCH p.alternativas
WHERE l.semana.idSemana = :idSemana
""")
    List<Leccion> findBySemanaIdConPreguntas(Integer idSemana);
}
