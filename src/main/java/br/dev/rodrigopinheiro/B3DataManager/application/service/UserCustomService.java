package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.application.security.CustomUserDetails;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserCustomService implements UserDetailsService {
    private final UsuarioRepository userRepository;

    @Autowired
    public UserCustomService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Retorna todos os usuários.
     */
    public List<Usuario> findAllUsers() {
        log.info("Buscando todos os usuários.");
        return userRepository.findAll();
    }

    /**
    /**
     * Busca um usuário pelo ID.
     */
    public Optional<Usuario> findUserById(Long id) {
        log.info("Buscando usuário com ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Busca um usuário pelo email.
     */
    public Optional<Usuario> findUserByEmail(String email) {
        log.info("Buscando usuário com email: {}", email);
        return userRepository.findByEmail(email.toLowerCase());
    }



    /**
     * Exclui um usuário pelo ID.
     */
    public void deleteUser(Long id) {
        log.info("Excluindo usuário com ID: {}", id);
        userRepository.deleteById(id);
        log.info("Usuário com ID: {} foi excluído com sucesso.", id);
    }

    /**
     * Carrega um usuário pelo email para autenticação.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));

        return new CustomUserDetails(user);
    }


}