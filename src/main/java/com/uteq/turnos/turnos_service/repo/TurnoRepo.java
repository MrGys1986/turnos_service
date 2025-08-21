package com.uteq.turnos.turnos_service.repo;

import com.uteq.turnos.turnos_service.model.EstadoTurno;
import com.uteq.turnos.turnos_service.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoRepo extends JpaRepository<Turno, Long> {
  // Activos (ya los tienes)
  List<Turno> findByDocenteIdAndEstadoInOrderByCreadoEnAsc(Long docenteId, List<EstadoTurno> estados);
  List<Turno> findByEstadoInOrderByCreadoEnAsc(List<EstadoTurno> estados);

  // TODOS los turnos del docente (cualquier estado)
  List<Turno> findByDocenteIdOrderByCreadoEnDesc(Long docenteId);

  // TODOS los turnos (monitor)
  List<Turno> findAllByOrderByCreadoEnDesc();


  List<Turno> findByAlumnoIdOrderByCreadoEnDesc(Long alumnoId);

}
