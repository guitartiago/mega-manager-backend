package com.megamanager.auth.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.auth.application.port.in.AutenticarUseCase;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AutenticarUseCase autenticar;

  public AuthController(AutenticarUseCase autenticar) { this.autenticar = autenticar; }

  public record LoginRequest(String username, String password) {}
  public record LoginResponse(String token, String tokenType, long expiresAt) {}

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest req) {
    var dto = autenticar.autenticar(req.username(), req.password());
    return new LoginResponse(dto.token(), "Bearer", dto.expiresAt());
  }
}
