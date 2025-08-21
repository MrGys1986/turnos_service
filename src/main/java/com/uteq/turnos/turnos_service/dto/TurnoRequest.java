package com.uteq.turnos.turnos_service.dto;

import jakarta.validation.constraints.NotNull;

public record TurnoRequest(
  @NotNull Long alumnoId,
  @NotNull Long materiaId,
  @NotNull Long docenteId,
  String tema
) {}
