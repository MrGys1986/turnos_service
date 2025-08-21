package com.uteq.turnos.turnos_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtValidator validator;

  public JwtAuthFilter(JwtValidator validator) {
    this.validator = validator;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String auth = request.getHeader("Authorization");
    if (auth == null || !auth.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    String token = auth.substring("Bearer ".length()).trim();
    try {
      var claims = validator.parseAndValidate(token);

      String subject = validator.readSubject(claims); // email o sub
      List<SimpleGrantedAuthority> authorities = validator.readAuthorities(claims);

      var authentication =
          new UsernamePasswordAuthenticationToken(subject, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception ex) {
      // Token inválido: limpiamos contexto y seguimos para que caiga en 401
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }
}
