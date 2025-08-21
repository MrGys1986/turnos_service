package com.uteq.turnos.turnos_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="bloqueos",
  uniqueConstraints=@UniqueConstraint(name="uq_doc_fecha", columnNames={"docente_id","fecha"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bloqueo {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="docente_id", nullable=false) private Long docenteId;
  @Column(nullable=false) private java.time.LocalDate fecha;
  private String motivo;
}
