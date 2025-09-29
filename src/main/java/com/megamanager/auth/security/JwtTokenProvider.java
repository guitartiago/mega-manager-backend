package com.megamanager.auth.security;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.megamanager.auth.application.port.out.TokenProvider;
import com.megamanager.usuario.domain.PerfilUsuario;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtTokenProvider implements TokenProvider {

  private final SecretKey key;          // SecretKey (HS256)
  private final int expirationMinutes;

  public JwtTokenProvider(SecretKey key, int expirationMinutes) {
	  this.key = key;
	  this.expirationMinutes = expirationMinutes;
  }

  @Override
  public String gerar(String subject, Set<PerfilUsuario> roles) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expirationMinutes * 60L);

    return Jwts.builder()
        .subject(subject)
        .claim("roles", roles)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  @Override
  public boolean valido(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public String subject(String token) {
    return Jwts.parser().verifyWith(key).build()
        .parseSignedClaims(token).getPayload().getSubject();
  }

  @Override
  public Set<String> roles(String token) {
    Object raw = Jwts.parser().verifyWith(key).build()
        .parseSignedClaims(token).getPayload().get("roles");
    if (raw == null) return Set.of();
    if (raw instanceof java.util.Collection<?> c)
        return c.stream().map(Object::toString).collect(Collectors.toSet());
    String csv = raw.toString();
    return csv.isBlank() ? Set.of()
        : Set.of(csv.split(",")).stream().map(String::trim).collect(Collectors.toSet());
  }

  @Override
  public long expiresAt(String token) {
    Date d = Jwts.parser().verifyWith(key).build()
        .parseSignedClaims(token).getPayload().getExpiration();
    return d.toInstant().getEpochSecond();
  }
}
