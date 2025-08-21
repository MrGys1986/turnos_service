package com.uteq.turnos.turnos_service.service;

import com.uteq.turnos.turnos_service.dto.TurnoAcceptRequest;
import com.uteq.turnos.turnos_service.dto.TurnoCancelRequest;
import com.uteq.turnos.turnos_service.dto.TurnoRequest;
import com.uteq.turnos.turnos_service.dto.TurnoView;
import com.uteq.turnos.turnos_service.integrations.CatalogClient;
import com.uteq.turnos.turnos_service.model.EstadoTurno;
import com.uteq.turnos.turnos_service.model.Turno;
import com.uteq.turnos.turnos_service.repo.TurnoRepo;
import com.uteq.turnos.turnos_service.ws.Notifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TurnoService {

  private final TurnoRepo repo;
  private final DisponibilidadService dispSvc;
  private final BloqueoService bloqSvc;
  private final Notifier notifier;
  private final CatalogClient catalog;

  public TurnoService(
      TurnoRepo repo,
      DisponibilidadService dispSvc,
      BloqueoService bloqSvc,
      Notifier notifier,
      CatalogClient catalog
  ) {
    this.repo = repo;
    this.dispSvc = dispSvc;
    this.bloqSvc = bloqSvc;
    this.notifier = notifier;
    this.catalog = catalog;
  }

  /* ===== Listados ===== */

  /** TODOS los turnos del docente (cualquier estado). */
  public List<TurnoView> deDocenteTodos(Long docenteId) {
    return repo.findByDocenteIdOrderByCreadoEnDesc(docenteId)
               .stream().map(this::toView).toList();
  }

  /** TODOS los turnos para monitor (cualquier estado). */
  public List<TurnoView> monitorTodos() {
    return repo.findAllByOrderByCreadoEnDesc()
               .stream().map(this::toView).toList();
  }

  /* ===== Flujo ===== */

  @Transactional
  public TurnoView solicitar(TurnoRequest req, String bearer) {
    var now = LocalDateTime.now();

    if (!dispSvc.isDisponibleAhora(req.docenteId(), now)) {
      throw new IllegalStateException("El docente no está disponible en este horario.");
    }
    if (bloqSvc.getBloqueoDeHoy(req.docenteId()).isPresent()) {
      throw new IllegalStateException("El docente tiene bloqueo hoy.");
    }

    Turno t = new Turno();
    t.setAlumnoId(req.alumnoId());
    t.setMateriaId(req.materiaId());
    t.setDocenteId(req.docenteId());
    t.setTema(req.tema());
    t.setEstado(EstadoTurno.SOLICITADO);

    t = repo.save(t);
    t.setFolio("T-" + t.getId());
    t = repo.save(t);

    var view = toView(t);
    notifier.broadcastChange("TURNO_SOLICITADO", toMsg(view));
    return view;
  }

  @Transactional
  public TurnoView aceptar(Long turnoId, String bearer, TurnoAcceptRequest body) {
    Turno t = repo.findById(turnoId).orElseThrow(() -> new IllegalArgumentException("Turno no existe"));
    if (t.getEstado() != EstadoTurno.SOLICITADO) {
      throw new IllegalStateException("No se puede aceptar en estado " + t.getEstado());
    }

    // Guardar programación
    t.setLugar(body.lugar());
    t.setFecha(LocalDate.parse(body.fecha()));
    t.setHoraIni(body.horaIni());
    t.setHoraFin(body.horaFin());
    if (body.observaciones() != null && !body.observaciones().isBlank()) {
      t.setObservaciones(body.observaciones().trim());
    }

    t.setEstado(EstadoTurno.ACEPTADO);
    t = repo.save(t);

    var view = toView(t);
    notifier.broadcastChange("TURNO_ACEPTADO", toMsg(view));
    return view;
  }

  @Transactional
  public TurnoView iniciar(Long turnoId) {
    Turno t = repo.findById(turnoId).orElseThrow(() -> new IllegalArgumentException("Turno no existe"));
    if (t.getEstado() != EstadoTurno.ACEPTADO) {
      throw new IllegalStateException("Debe estar ACEPTADO para iniciar.");
    }
    t.setEstado(EstadoTurno.EN_ATENCION);
    t = repo.save(t);

    var view = toView(t);
    notifier.broadcastChange("TURNO_EN_ATENCION", toMsg(view));
    return view;
  }

  @Transactional
  public TurnoView finalizar(Long turnoId) {
    Turno t = repo.findById(turnoId).orElseThrow(() -> new IllegalArgumentException("Turno no existe"));
    if (t.getEstado() != EstadoTurno.EN_ATENCION && t.getEstado() != EstadoTurno.ACEPTADO) {
      throw new IllegalStateException("No se puede finalizar en estado " + t.getEstado());
    }
    t.setEstado(EstadoTurno.FINALIZADO);
    t = repo.save(t);

    var view = toView(t);
    notifier.broadcastChange("TURNO_FINALIZADO", toMsg(view));
    return view;
  }

  @Transactional
  public TurnoView cancelar(Long turnoId, String observaciones) {
    Turno t = repo.findById(turnoId).orElseThrow(() -> new IllegalArgumentException("Turno no existe"));
    if (t.getEstado() == EstadoTurno.FINALIZADO || t.getEstado() == EstadoTurno.CANCELADO) {
      throw new IllegalStateException("Turno ya concluido");
    }

    if (observaciones != null && !observaciones.isBlank()) {
      t.setObservaciones(observaciones.trim());
    }
    t.setEstado(EstadoTurno.CANCELADO);
    t = repo.save(t);

    var view = toView(t);
    notifier.broadcastChange("TURNO_CANCELADO", toMsg(view));
    return view;
  }

  /* ===== Helpers ===== */

  private TurnoView toView(Turno t) {
    return new TurnoView(
        t.getId(),
        t.getFolio(),
        t.getAlumnoId(),
        t.getMateriaId(),
        t.getDocenteId(),
        t.getTema(),
        t.getEstado(),
        t.getLugar(),
        t.getFecha() != null ? t.getFecha().toString() : null,
        t.getHoraIni(),
        t.getHoraFin(),
        t.getObservaciones(),
        t.getCreadoEn(),
        t.getActualizadoEn()
    );
  }

  private Map<String, Object> toMsg(TurnoView v) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", v.id());
    m.put("folio", v.folio());
    m.put("alumnoId", v.alumnoId());
    m.put("materiaId", v.materiaId());
    m.put("docenteId", v.docenteId());
    m.put("tema", v.tema());
    m.put("estado", v.estado().name());
    m.put("lugar", v.lugar());
    m.put("fecha", v.fecha());
    m.put("horaIni", v.horaIni());
    m.put("horaFin", v.horaFin());
    m.put("observaciones", v.observaciones());
    return m;
  }
}
