// src/main/java/com/uteq/turnos/turnos_service/controller/HorariosController.java
package com.uteq.turnos.turnos_service.controller;

import com.uteq.turnos.turnos_service.dto.DisponibilidadRequest;
import com.uteq.turnos.turnos_service.model.Disponibilidad;
import com.uteq.turnos.turnos_service.service.DisponibilidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/horarios")
public class HorariosController {

  private final DisponibilidadService svc;

  public HorariosController(DisponibilidadService svc) {
    this.svc = svc;
  }

  /** Listar por docenteId */
  @GetMapping
  @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
  public List<Disponibilidad> list(@RequestParam Long docenteId) {
    return svc.listByDocente(docenteId);
  }

  /** Crear */
  @PostMapping(consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
  public Disponibilidad create(@Valid @RequestBody DisponibilidadRequest req) {
    return svc.create(req);
  }

  /** Actualizar completo (o “PUT-like”) */
  @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
  public Disponibilidad update(@PathVariable Long id, @Valid @RequestBody DisponibilidadRequest req) {
    return svc.update(id, req);
  }

  /** Eliminar */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
  public void delete(@PathVariable Long id) {
    svc.delete(id);
  }

  /* ==== Handlers de errores para ver CLEARLY por qué 400 ==== */

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, Object> onValidation(MethodArgumentNotValidException ex) {
    var first = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
    String msg = first != null ? (first.getField() + ": " + first.getDefaultMessage()) : "Datos inválidos";
    return Map.of("error", "Bad Request", "message", msg);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public Map<String, Object> onIllegalArg(IllegalArgumentException ex) {
    return Map.of("error", "Bad Request", "message", ex.getMessage());
  }
}
