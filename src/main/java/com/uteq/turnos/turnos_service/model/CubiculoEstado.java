package com.uteq.turnos.turnos_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity @Table(name="cubiculo_estado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CubiculoEstado {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="cubiculo_id", nullable=false, unique=true) private Long cubiculoId;
  @Column(nullable=false) private Boolean disponible;
  @UpdateTimestamp @Column(name="ultimo_cambio", nullable=false) private Instant ultimoCambio;
}
