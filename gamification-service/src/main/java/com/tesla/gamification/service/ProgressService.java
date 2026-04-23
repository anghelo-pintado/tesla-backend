package com.tesla.gamification.service;

import com.tesla.gamification.entity.ProgresoLecciones;
import com.tesla.gamification.repository.ProgresoLeccionesRepository;
import com.tesla.gamification.repository.IntentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private ProgresoLeccionesRepository progresoRepository;

    @Autowired
    private IntentoRepository intentoRepository;

    @Transactional(readOnly = true)
    public Map<Long, Boolean> obtenerMapaLeccionesCompletadas(Long usuarioId) {
        List<ProgresoLecciones> progresos = progresoRepository.findByUsuarioId(usuarioId);

        return progresos.stream()
                .collect(Collectors.toMap(
                        ProgresoLecciones::getLeccionId,
                        ProgresoLecciones::getCompletada,
                        (existente, reemplazo) -> existente
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> obtenerMapaExpGanada(Long usuarioId) {
        List<Object[]> resultados = intentoRepository.findPuntajesByUsuario(usuarioId);

        return resultados.stream().collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Integer) row[1]) * 30,
                (existente, reemplazo) -> existente
        ));
    }
}