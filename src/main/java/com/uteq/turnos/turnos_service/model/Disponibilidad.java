package com.uteq.turnos.turnos_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="disponibilidades",
  uniqueConstraints=@UniqueConstraint(name="uq_doc_dia", columnNames={"docente_id","dia_semana","hora_ini","hora_fin"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Disponibilidad {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="docente_id", nullable=false) private Long docenteId;
  @Column(name="dia_semana", nullable=false) private Integer diaSemana; // 1..7
  @Column(name="hora_ini", nullable=false) private java.time.LocalTime horaIni;
  @Column(name="hora_fin", nullable=false) private java.time.LocalTime horaFin;
  @Column(nullable=false) private Boolean activo = true;
}