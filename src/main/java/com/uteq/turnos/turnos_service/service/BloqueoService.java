package com.uteq.turnos.turnos_service.service;

import com.uteq.turnos.turnos_service.dto.BloqueoRequest;
import com.uteq.turnos.turnos_service.model.Bloqueo;
import com.uteq.turnos.turnos_service.repo.BloqueoRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BloqueoService {
  private final BloqueoRepo repo;
  public BloqueoService(BloqueoRepo r){ this.repo=r; }

  public Bloqueo crear(Long docenteId, BloqueoRequest req){
    var b = Bloqueo.builder()
        .docenteId(docenteId)
        .fecha(LocalDate.parse(req.fecha()))
        .motivo(req.motivo())
        .build();
    return repo.save(b);
  }

  public Optional<Bloqueo> getBloqueoDeHoy(Long docenteId){
    return repo.findByDocenteIdAndFecha(docenteId, LocalDate.now());
  }
}
