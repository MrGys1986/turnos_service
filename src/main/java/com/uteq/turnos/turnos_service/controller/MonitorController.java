package com.uteq.turnos.turnos_service.controller;

import com.uteq.turnos.turnos_service.dto.TurnoView;
import com.uteq.turnos.turnos_service.service.TurnoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

  private final TurnoService turnos;

  public MonitorController(TurnoService turnos) {
    this.turnos = turnos;
  }

  @GetMapping("/turnos")
  @PreAuthorize("isAuthenticated()") // alumnos, docentes y admin
  public List<TurnoView> all() {
    return turnos.monitorTodos();
  }
}
