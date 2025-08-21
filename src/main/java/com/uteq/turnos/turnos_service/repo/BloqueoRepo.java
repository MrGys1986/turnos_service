package com.uteq.turnos.turnos_service.repo;

import com.uteq.turnos.turnos_service.model.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface BloqueoRepo extends JpaRepository<Bloqueo, Long> {
  Optional<Bloqueo> findByDocenteIdAndFecha(Long docenteId, LocalDate fecha);
}
