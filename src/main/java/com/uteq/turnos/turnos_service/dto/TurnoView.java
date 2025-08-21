package com.uteq.turnos.turnos_service.dto;

import com.uteq.turnos.turnos_service.model.EstadoTurno;
import java.time.Instant;

public record TurnoView(
  Long id, String folio,
  Long alumnoId, Long materiaId, Long docenteId,
  String tema, EstadoTurno estado,
  String lugar, String fecha, String horaIni, String horaFin,
  String observaciones,
  Instant creadoEn, Instant actualizadoEn
) {}
