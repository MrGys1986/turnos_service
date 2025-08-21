package com.uteq.turnos.turnos_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "turnos")
@Getter @Setter
public class Turno {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 32)
  private String folio;

  @Column(name = "alumno_id", nullable = false)
  private Long alumnoId;

  @Column(name = "materia_id", nullable = false)
  private Long materiaId;

  @Column(name = "docente_id", nullable = false)
  private Long docenteId;

  @Column(length = 200)
  private String tema;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private EstadoTurno estado;

  // NUEVOS CAMPOS
  @Column(length = 160)
  private String lugar;

  private LocalDate fecha;

  @Column(name = "hora_ini", length = 5)
  private String horaIni; // "HH:MM"

  @Column(name = "hora_fin", length = 5)
  private String horaFin; // "HH:MM"

  @Column(columnDefinition = "TEXT")
  private String observaciones;

  @CreationTimestamp
  @Column(name = "creado_en", updatable = false)
  private Instant creadoEn;

  @UpdateTimestamp
  @Column(name = "actualizado_en")
  private Instant actualizadoEn;

  @Version
  private Long version;
}
