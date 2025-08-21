package com.uteq.turnos.turnos_service.controller;

import com.uteq.turnos.turnos_service.dto.*;
import com.uteq.turnos.turnos_service.service.TurnoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnosController {

  private final TurnoService turnos;

  public TurnosController(TurnoService turnos) {
    this.turnos = turnos;
  }

  @PostMapping(consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasRole('ALUMNO')")
  public TurnoView solicitar(@Valid @RequestBody TurnoRequest req, HttpServletRequest http) {
    String bearer = http.getHeader("Authorization");
    return turnos.solicitar(req, bearer);
  }

  /** Listado del docente: ahora devuelve TODOS los estados. */
  @GetMapping(produces = "application/json")
  @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
  public List<TurnoView> list(@RequestParam Long docenteId) {
    return turnos.deDocenteTodos(docenteId);
  }

  @PostMapping(path = "/{id}/aceptar", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasRole('DOCENTE')")
  public TurnoView aceptar(@PathVariable Long id,
                           @Valid @RequestBody TurnoAcceptRequest body,
                           HttpServletRequest http) {
    String bearer = http.getHeader("Authorization");
    return turnos.aceptar(id, bearer, body);
  }

  @PostMapping(path = "/{id}/iniciar", produces = "application/json")
  @PreAuthorize("hasRole('DOCENTE')")
  public TurnoView iniciar(@PathVariable Long id) {
    return turnos.iniciar(id);
  }

  @PostMapping(path = "/{id}/finalizar", produces = "application/json")
  @PreAuthorize("hasRole('DOCENTE')")
  public TurnoView finalizar(@PathVariable Long id) {
    return turnos.finalizar(id);
  }

  @PostMapping(path = "/{id}/cancelar", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ALUMNO','DOCENTE')")
  public TurnoView cancelar(@PathVariable Long id, @Valid @RequestBody TurnoCancelRequest body) {
    return turnos.cancelar(id, body.observaciones());
  }
}
