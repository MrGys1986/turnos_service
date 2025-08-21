// turnos_service: src/main/java/com/uteq/turnos/turnos_service/integrations/AuthClient.java
package com.uteq.turnos.turnos_service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthClient {

  private final RestTemplate http;
  private final String authBase;

  public AuthClient(RestTemplate http, @Value("${app.auth.base-url:http://localhost:8081}") String authBase) {
    this.http = http;
    this.authBase = authBase;
  }

  public String getUserNombre(Long userId, String bearer) {
    if (userId == null) return null;
    try {
      HttpHeaders h = new HttpHeaders();
      if (bearer != null && !bearer.isBlank()) h.set("Authorization", bearer);
      var resp = http.exchange(
        authBase + "/users/" + userId,
        HttpMethod.GET,
        new HttpEntity<>(h),
        Map.class
      );
      Object nombre = resp.getBody() != null ? resp.getBody().get("nombre") : null;
      return nombre == null ? null : String.valueOf(nombre);
    } catch (Exception e) {
      return null;
    }
  }
}
