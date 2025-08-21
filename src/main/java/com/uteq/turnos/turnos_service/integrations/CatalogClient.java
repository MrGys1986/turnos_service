// src/main/java/com/uteq/turnos/turnos_service/integrations/CatalogClient.java
package com.uteq.turnos.turnos_service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogClient {
  private final RestTemplate http;
  private final String base;

  public CatalogClient(RestTemplate http, @Value("${app.catalog.base-url}") String base) {
    this.http = http; this.base = base;
  }

  private HttpEntity<Void> entity(String bearer){
    HttpHeaders h = new HttpHeaders();
    if (bearer != null && !bearer.isBlank()) h.set(HttpHeaders.AUTHORIZATION, bearer);
    return new HttpEntity<>(h);
  }

  public boolean existeAlumno(Long id, String bearer) {
    try {
      ResponseEntity<Void> r = http.exchange(base + "/alumnos/{id}", HttpMethod.GET, entity(bearer), Void.class, id);
      return r.getStatusCode().is2xxSuccessful();
    } catch (Exception e) { return false; }
  }

  public boolean existeDocente(Long id, String bearer) {
    try {
      ResponseEntity<Void> r = http.exchange(base + "/docentes/{id}", HttpMethod.GET, entity(bearer), Void.class, id);
      return r.getStatusCode().is2xxSuccessful();
    } catch (Exception e) { return false; }
  }

  public boolean existeMateria(Long id, String bearer) {
    try {
      ResponseEntity<Void> r = http.exchange(base + "/materias/{id}", HttpMethod.GET, entity(bearer), Void.class, id);
      return r.getStatusCode().is2xxSuccessful();
    } catch (Exception e) { return false; }
  }

  public Long getCubiculoIdDeDocente(Long docenteId, String bearer) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCubiculoIdDeDocente'");
  }


  
}
