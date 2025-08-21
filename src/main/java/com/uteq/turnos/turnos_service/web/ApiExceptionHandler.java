package com.uteq.turnos.turnos_service.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
  public ResponseEntity<?> handleBadRequest(RuntimeException ex) {
    return ResponseEntity.badRequest().body(body(400, "Bad Request", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getAllErrors().stream()
        .findFirst().map(e -> e.getDefaultMessage()).orElse("Datos inválidos");
    return ResponseEntity.badRequest().body(body(400, "Bad Request", msg));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleOther(Exception ex) {
    // si quieres ver el error en logs:
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(500, "Internal Server Error", "Ocurrió un error inesperado"));
  }

  private Map<String,Object> body(int status, String error, String message) {
    return Map.of(
      "timestamp", Instant.now().toString(),
      "status", status,
      "error", error,
      "message", message
    );
  }
  
}
