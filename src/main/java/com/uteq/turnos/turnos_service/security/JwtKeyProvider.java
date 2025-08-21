// src/main/java/com/uteq/turnos/turnos_service/security/JwtKeyProvider.java
package com.uteq.turnos.turnos_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtKeyProvider {

  private final RestTemplate http;
  private final String publicKeyUrl;

  private volatile PublicKey cached;
  private volatile Instant fetchedAt;

  public JwtKeyProvider(RestTemplate http,
                        @Value("${security.jwt.public-key-url}") String publicKeyUrl) {
    this.http = http;
    this.publicKeyUrl = publicKeyUrl;
  }

  public synchronized PublicKey getPublicKey() {
    if (cached != null && fetchedAt != null && fetchedAt.isAfter(Instant.now().minusSeconds(600))) {
      return cached; // cache 10 min
    }
    String pem = fetchPem();
    cached = parsePublicKeyFromPem(pem);
    fetchedAt = Instant.now();
    return cached;
  }

  private String fetchPem() {
    ResponseEntity<String> resp = http.getForEntity(publicKeyUrl, String.class);
    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
      throw new IllegalStateException("No se pudo obtener la llave pública desde: " + publicKeyUrl);
    }
    return resp.getBody();
  }

  private static PublicKey parsePublicKeyFromPem(String pemText) {
    try {
      String pem = pemText
          .replace("-----BEGIN PUBLIC KEY-----", "")
          .replace("-----END PUBLIC KEY-----", "")
          .replaceAll("\\s+", "");
      byte[] der = Base64.getDecoder().decode(pem);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
      return KeyFactory.getInstance("RSA").generatePublic(spec);
    } catch (Exception e) {
      throw new IllegalStateException("Llave pública inválida", e);
    }
  }
}
