package com.tesla.gamification.course.service;

import com.tesla.gamification.course.dto.SemanaDTO;
import com.tesla.gamification.course.dto.ViewLeccionDTO;
import com.tesla.gamification.course.dto.ViewSemanaDTO;
import com.tesla.gamification.course.entity.Semana;
import com.tesla.gamification.course.entity.Curso;
import com.tesla.gamification.course.repository.CursoRepository;
import com.tesla.gamification.course.repository.SemanaRepository;
import com.tesla.gamification.course.dto.CrearSemanaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeekService {

    @Autowired private SemanaRepository semanaRepository;
    @Autowired private CursoRepository cursoRepository;

    @Transactional
    public Semana crearSemana(Integer cursoId, CrearSemanaDTO dto) {

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Semana semana = new Semana();
        semana.setCurso(curso);
        semana.setNroSemana(dto.nroSemana());
        semana.setIsBloqueada(false);

        return semanaRepository.save(semana);
    }

    @Transactional(readOnly = true)
    public List<ViewSemanaDTO> obtenerSemanas(Integer cursoId) {

        if (!cursoRepository.existsById(cursoId)) {
            throw new RuntimeException("Curso no encontrado");
        }
        return semanaRepository
                .findByCursoIdCurso(cursoId)   // ← sin filtro por bloqueada
                .stream()
                .map(s -> new ViewSemanaDTO(
                        s.getIdSemana(),
                        s.getNroSemana(),
                        s.getIsBloqueada(),
                        s.getLecciones().stream()
                                .map(l -> new ViewLeccionDTO(
                                        l.getIdLeccion(),
                                        l.getNombre(),
                                        l.getOrden()
                                )).toList()
                )).toList();
    }
    @Transactional
    public void eliminarSemana(Integer idSemana) {
        Semana semana = semanaRepository.findById(idSemana).orElseThrow(() -> new RuntimeException("Semana no encontrada"));
        semanaRepository.delete(semana);
    }
}