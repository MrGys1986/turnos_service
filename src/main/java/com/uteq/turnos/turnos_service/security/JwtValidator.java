// src/main/java/com/uteq/turnos/turnos_service/security/JwtValidator.java
package com.uteq.turnos.turnos_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class JwtValidator {

  private final JwtKeyProvider keyProvider;
  private final String expectedIssuer;

  public JwtValidator(
      JwtKeyProvider keyProvider,
      @Value("${security.jwt.issuer}") String expectedIssuer) {
    this.keyProvider = keyProvider;
    this.expectedIssuer = expectedIssuer;
  }

  public Claims parseAndValidate(String token) {
    PublicKey key = keyProvider.getPublicKey();

    Jws<Claims> jws = Jwts.parserBuilder()
        .requireIssuer(expectedIssuer)
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);

    return jws.getBody();
  }

// dentro de JwtValidator
public String readSubject(Claims claims) {
  String email = claims.get("email", String.class);
  return (email != null && !email.isBlank()) ? email : claims.getSubject();
}

public java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority>
readAuthorities(Claims claims) {
  Object raw = claims.get("roles");
  if (raw == null) raw = claims.get("authorities");
  if (raw == null) raw = claims.get("scope");
  if (raw == null) raw = claims.get("scopes");

  java.util.List<String> roles = new java.util.ArrayList<>();
  if (raw instanceof java.util.Collection<?> col) {
    for (Object o : col) if (o != null) roles.add(o.toString());
  } else if (raw instanceof String s) {
    for (String p : s.split("[,\\s]+")) if (!p.isBlank()) roles.add(p.trim());
  }

  java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> out = new java.util.ArrayList<>();
  for (String r : roles) {
    String role = r.trim();
    if (role.isEmpty()) continue;
    if (!role.toUpperCase().startsWith("ROLE_")) role = "ROLE_" + role.toUpperCase();
    else role = role.toUpperCase();
    out.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
  }
  return out;
}

}
