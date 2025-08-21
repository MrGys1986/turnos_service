// src/main/java/com/uteq/turnos/turnos_service/service/DisponibilidadService.java
package com.uteq.turnos.turnos_service.service;

import com.uteq.turnos.turnos_service.dto.DisponibilidadRequest;
import com.uteq.turnos.turnos_service.model.Disponibilidad;
import com.uteq.turnos.turnos_service.repo.DisponibilidadRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadService {

  private final DisponibilidadRepo repo;

  public DisponibilidadService(DisponibilidadRepo repo) {
    this.repo = repo;
  }

  public List<Disponibilidad> listByDocente(Long docenteId) {
    return repo.findByDocenteIdOrderByDiaSemanaAscHoraIniAsc(docenteId);
  }

  @Transactional
  public Disponibilidad create(DisponibilidadRequest req) {
    var ini = parse(req.getHoraIni());
    var fin = parse(req.getHoraFin());
    if (!fin.isAfter(ini)) {
      throw new IllegalArgumentException("horaFin debe ser mayor que horaIni");
    }

    // Validación de solapes simples dentro del mismo día/docente
    var existentes = repo.findByDocenteIdAndDiaSemana(req.getDocenteId(), req.getDiaSemana());
    boolean solapa = existentes.stream().anyMatch(d ->
        overlap(ini, fin, d.getHoraIni(), d.getHoraFin())
    );
    if (solapa) {
      throw new IllegalArgumentException("El horario se solapa con uno existente");
    }

    var d = new Disponibilidad();
    d.setDocenteId(req.getDocenteId());
    d.setDiaSemana(req.getDiaSemana());
    d.setHoraIni(ini);
    d.setHoraFin(fin);
    d.setActivo(Boolean.TRUE.equals(req.getActivo()));
    return repo.save(d);
  }

  @Transactional
  public Disponibilidad update(Long id, DisponibilidadRequest req) {
    var d = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("No existe el horario"));
    var ini = parse(req.getHoraIni());
    var fin = parse(req.getHoraFin());
    if (!fin.isAfter(ini)) {
      throw new IllegalArgumentException("horaFin debe ser mayor que horaIni");
    }

    // Evitar solapes con otros registros del mismo docente/día
    var existentes = repo.findByDocenteIdAndDiaSemana(req.getDocenteId(), req.getDiaSemana());
    boolean solapa = existentes.stream()
        .filter(x -> !x.getId().equals(id))
        .anyMatch(x -> overlap(ini, fin, x.getHoraIni(), x.getHoraFin()));
    if (solapa) {
      throw new IllegalArgumentException("El horario se solapa con uno existente");
    }

    d.setDocenteId(req.getDocenteId());
    d.setDiaSemana(req.getDiaSemana());
    d.setHoraIni(ini);
    d.setHoraFin(fin);
    d.setActivo(Boolean.TRUE.equals(req.getActivo()));
    return repo.save(d);
  }

  @Transactional
  public void delete(Long id) {
    repo.deleteById(id);
  }

  /* ===== Helpers ===== */
  private static LocalTime parse(String hhmm) {
    return LocalTime.parse(hhmm); // espera "HH:mm"
  }

  private static boolean overlap(LocalTime a1, LocalTime a2, LocalTime b1, LocalTime b2) {
    // [a1,a2) interseca [b1,b2)
    return a1.isBefore(b2) && b1.isBefore(a2);
  }


  public boolean isDisponibleAhora(Long docenteId, LocalDateTime when) {
  if (docenteId == null || when == null) return false;

  // Mapeo 1..7 => Lunes..Domingo (ajusta si en tu DB es distinto)
  int diaSemana = switch (when.getDayOfWeek()) {
    case MONDAY -> 1;
    case TUESDAY -> 2;
    case WEDNESDAY -> 3;
    case THURSDAY -> 4;
    case FRIDAY -> 5;
    case SATURDAY -> 6;
    case SUNDAY -> 7;
  };

  LocalTime hhmm = when.toLocalTime();

  var slots = repo.findByDocenteIdAndDiaSemanaAndActivoTrue(docenteId, diaSemana);

  // Log de diagnóstico (te ayuda a ver qué rangos tiene)
  if (slots.isEmpty()) {
    System.out.printf("[DISP] No hay disponibilidad activa para docente %d dia=%d%n", docenteId, diaSemana);
  } else {
    slots.forEach(s -> System.out.printf(
      "[DISP] Slot activo: %s..%s (docente=%d dia=%d)%n",
      String.valueOf(s.getHoraIni()), String.valueOf(s.getHoraFin()), docenteId, diaSemana
    ));
  }

  // Cobertura: [horaIni, horaFin] (fin inclusivo)
  return slots.stream().anyMatch(d ->
      !hhmm.isBefore(d.getHoraIni()) && !hhmm.isAfter(d.getHoraFin())
  );
}

}
