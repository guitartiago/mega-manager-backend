package com.megamanager.usuario.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.megamanager.usuario.application.port.in.AlterarPerfisUsuarioUseCase;
import com.megamanager.usuario.application.port.in.AtivarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.AtualizarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.CriarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.InativarUsuarioUseCase;
import com.megamanager.usuario.application.port.in.ListarUsuariosUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.application.usecase.AlterarPerfisUsuarioService;
import com.megamanager.usuario.application.usecase.AtivarUsuarioService;
import com.megamanager.usuario.application.usecase.AtualizarUsuarioService;
import com.megamanager.usuario.application.usecase.CriarUsuarioService;
import com.megamanager.usuario.application.usecase.InativarUsuarioService;
import com.megamanager.usuario.application.usecase.ListarUsuariosService;

@Configuration
public class UsuarioUseCaseConfig {

    @Bean
    public CriarUsuarioService criarUsuarioService(UsuarioRepository repository) {
        return new CriarUsuarioService(repository);
    }

    @Bean
    public AtualizarUsuarioService atualizarUsuarioService(UsuarioRepository repository) {
        return new AtualizarUsuarioService(repository);
    }

    @Bean
    public AtivarUsuarioService ativarUsuarioService(UsuarioRepository repository) {
        return new AtivarUsuarioService(repository);
    }

    @Bean
    public InativarUsuarioService inativarUsuarioService(UsuarioRepository repository) {
        return new InativarUsuarioService(repository);
    }
    
    @Bean
    public ListarUsuariosService listarUsuariosService(UsuarioRepository repository) {
    	return new ListarUsuariosService(repository);    	
    }

    @Bean
    public AlterarPerfisUsuarioService alterarPerfisUsuarioService(UsuarioRepository repository) {
        return new AlterarPerfisUsuarioService(repository);
    }
    
    @Bean
    public CriarUsuarioUseCase criarUsuarioUseCase(CriarUsuarioService service) {
    	return service;
    }
    
    @Bean
    public AtualizarUsuarioUseCase atualizarUsuarioUseCase(AtualizarUsuarioService service) {
    	return service;
    }
    
    @Bean
    public AtivarUsuarioUseCase ativarUsuarioUseCase(AtivarUsuarioService service) {
    	return service;
    }
    
    @Bean
    public InativarUsuarioUseCase inativarUsuarioUseCase(InativarUsuarioService service) {
    	return service;
    }
    
    @Bean
    public ListarUsuariosUseCase listarUsuariosUseCase(ListarUsuariosService service) {
    	return service;
    }
    
    @Bean
    public AlterarPerfisUsuarioUseCase alterarPerfisUsuarioUseCase(AlterarPerfisUsuarioService service) {
        return service;
    }
}
