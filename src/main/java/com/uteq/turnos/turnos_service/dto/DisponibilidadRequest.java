// src/main/java/com/uteq/turnos/turnos_service/dto/DisponibilidadRequest.java
package com.uteq.turnos.turnos_service.dto;

import jakarta.validation.constraints.*;

public class DisponibilidadRequest {

  @NotNull(message = "docenteId es requerido")
  private Long docenteId;

  /** 1=Lunes … 7=Domingo */
  @NotNull(message = "diaSemana es requerido")
  @Min(value = 1, message = "diaSemana debe ser 1..7")
  @Max(value = 7, message = "diaSemana debe ser 1..7")
  private Integer diaSemana;

  /** Formato HH:mm (24h) */
  @NotBlank(message = "horaIni es requerida")
  @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "horaIni debe ser HH:mm")
  private String horaIni;

  /** Formato HH:mm (24h) */
  @NotBlank(message = "horaFin es requerida")
  @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "horaFin debe ser HH:mm")
  private String horaFin;

  @NotNull(message = "activo es requerido")
  private Boolean activo;

  // Getters/Setters — así evitamos el error de método .docenteId() no encontrado
  public Long getDocenteId() { return docenteId; }
  public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }

  public Integer getDiaSemana() { return diaSemana; }
  public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }

  public String getHoraIni() { return horaIni; }
  public void setHoraIni(String horaIni) { this.horaIni = horaIni; }

  public String getHoraFin() { return horaFin; }
  public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

  public Boolean getActivo() { return activo; }
  public void setActivo(Boolean activo) { this.activo = activo; }
}
