package com.uteq.turnos.turnos_service.repo;

import com.uteq.turnos.turnos_service.model.CubiculoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CubiculoEstadoRepo extends JpaRepository<CubiculoEstado, Long> {
  Optional<CubiculoEstado> findByCubiculoId(Long cubiculoId);
}