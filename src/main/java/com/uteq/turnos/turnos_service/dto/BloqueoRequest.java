package com.uteq.turnos.turnos_service.dto;

import jakarta.validation.constraints.*;

public record BloqueoRequest(
  @NotNull String fecha, // "YYYY-MM-DD"
  String motivo
) {}
