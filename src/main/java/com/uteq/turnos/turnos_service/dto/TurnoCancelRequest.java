package com.uteq.turnos.turnos_service.dto;

import jakarta.validation.constraints.NotBlank;

public record TurnoCancelRequest(
  @NotBlank String observaciones
) {}
