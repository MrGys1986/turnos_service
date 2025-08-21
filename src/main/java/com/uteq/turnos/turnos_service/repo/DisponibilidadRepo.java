// src/main/java/com/uteq/turnos/turnos_service/repo/DisponibilidadRepo.java
package com.uteq.turnos.turnos_service.repo;

import com.uteq.turnos.turnos_service.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepo extends JpaRepository<Disponibilidad, Long> {
  List<Disponibilidad> findByDocenteIdOrderByDiaSemanaAscHoraIniAsc(Long docenteId);
  List<Disponibilidad> findByDocenteIdAndDiaSemana(Long docenteId, Integer diaSemana);
  List<Disponibilidad> findByDocenteIdAndDiaSemanaAndActivoTrue(Long docenteId, Integer diaSemana);
}
