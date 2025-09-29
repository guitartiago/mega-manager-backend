package com.megamanager.usuario.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.megamanager.usuario.adapter.persistence.entity.UsuarioEntity;
import com.megamanager.usuario.adapter.persistence.mapper.UsuarioMapper;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository repository;

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        UsuarioEntity salvo = repository.save(entity);
        return UsuarioMapper.toDomain(salvo);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return repository.findByUsername(username).map(UsuarioMapper::toDomain);
    }

	@Override
	public List<Usuario> buscarTodos() {
		return repository.findAll()
				.stream()
				.map(UsuarioMapper::toDomain)
				.toList();
	}
    
}
