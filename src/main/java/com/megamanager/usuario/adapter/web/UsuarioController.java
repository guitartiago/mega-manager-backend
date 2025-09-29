package com.megamanager.usuario.adapter.web;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.usuario.adapter.web.dto.UsuarioRequestDTO;
import com.megamanager.usuario.adapter.web.dto.UsuarioResponseDTO;
import com.megamanager.usuario.adapter.web.mapper.UsuarioDtoMapper;
import com.megamanager.usuario.application.port.in.AlterarPerfisUsuarioUseCase;
import com.megamanager.usuario.application.port.in.AtivarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.AtualizarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.CriarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.InativarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.ListarUsuariosUseCase;
import com.megamanager.usuario.domain.PerfilUsuario;
import com.megamanager.usuario.domain.Usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final AtivarUsuarioUseCase ativarUsuarioUseCase;
    private final InativarUsuarioUseCase inativarUsuarioUseCase;
    private final AlterarPerfisUsuarioUseCase alterarPerfisUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = Usuario.criar(dto.username(), dto.senha(), dto.perfis());
        Usuario salvo = criarUsuarioUseCase.executar(usuario);
        return ResponseEntity.ok(UsuarioDtoMapper.toResponse(salvo));
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<Usuario> usuarios = listarUsuariosUseCase.executar();
        List<UsuarioResponseDTO> dtoList = usuarios.stream()
            .map(UsuarioDtoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = Usuario.reconstruir(id, dto.username(), dto.senha(), true, dto.perfis());
        Usuario atualizado = atualizarUsuarioUseCase.executar(usuario);
        return ResponseEntity.ok(UsuarioDtoMapper.toResponse(atualizado));
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponseDTO> ativar(@PathVariable Long id) {
        Usuario ativado = ativarUsuarioUseCase.executar(id);
        return ResponseEntity.ok(UsuarioDtoMapper.toResponse(ativado));
    }

    @PutMapping("/{id}/inativar")
    public ResponseEntity<UsuarioResponseDTO> inativar(@PathVariable Long id) {
        Usuario inativado = inativarUsuarioUseCase.executar(id);
        return ResponseEntity.ok(UsuarioDtoMapper.toResponse(inativado));
    }

    @PutMapping("/{id}/perfis")
    public ResponseEntity<Void> alterarPerfis(@PathVariable Long id,
                                              @RequestBody Set<String> novosPerfis) {
        var perfisEnum = novosPerfis.stream()
                .map(String::toUpperCase)
                .map(PerfilUsuario::valueOf)
                .collect(java.util.stream.Collectors.toSet());

        alterarPerfisUsuarioUseCase.executar(id, perfisEnum);
        return ResponseEntity.noContent().build();
    }
}
