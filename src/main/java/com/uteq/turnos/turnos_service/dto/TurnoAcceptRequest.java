package com.uteq.turnos.turnos_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TurnoAcceptRequest(
  @NotBlank String lugar,
  @NotBlank String fecha,                 // "YYYY-MM-DD"
  @NotBlank @Pattern(regexp="^\\d{2}:\\d{2}$") String horaIni, // "HH:MM"
  @NotBlank @Pattern(regexp="^\\d{2}:\\d{2}$") String horaFin,
  String observaciones
) {}
