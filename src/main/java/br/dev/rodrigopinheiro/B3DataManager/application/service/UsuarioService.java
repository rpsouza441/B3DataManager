package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.Roles;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.*;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UsuarioService {

    private final MessageSource messageSource;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(MessageSource messageSource, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.messageSource = messageSource;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> list(Pageable pageable, Specification<Usuario> filter) {
        return usuarioRepository.findAll(filter, pageable);
    }

    public void criarUsuariosPadrao() {
        criarUsuarioPadrao("rps", "d1n8o", "rps@example.com", Set.of(Roles.USER));
        criarUsuarioPadrao("admin", "admin123", "admin@example.com", Set.of(Roles.USER));
    }

    private void criarUsuarioPadrao(String username, String senha, String email, Set<Roles> roles) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(senha));
            usuario.setEmail(email);
            usuario.setRoles(roles);
            usuario.setDeletado(false);

            usuarioRepository.save(usuario);
        }
    }

    /**
     * Registra um novo usuário após validações.
     */
    public void registerUser(String username, String email, String password, Locale locale) {
        log.info("Iniciando registro de novo usuário: {}", username);

        validateUsername(username, locale);
        validateEmail(email, locale);
        validatePassword(password, locale);

        // Verificar se o usuário ou email já existem
        if (usuarioRepository.existsByUsername(username)) {
            log.warn("Falha ao registrar: nome de usuário '{}' já está em uso.", username);
            throw new UsernameAlreadyExistsException(username, messageSource);
        }
        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Falha ao registrar: email '{}' já está em uso.", email);
            throw new EmailAlreadyExistsException(email, messageSource);
        }

        // Criar o novo usuário
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));

        // Garantir que o usuário tenha pelo menos o papel USER
        Set<Roles> roles = new HashSet<>();
        roles.add(Roles.USER);
        usuario.setRoles(roles);

        Usuario savedUser = usuarioRepository.save(usuario);
        log.info("Usuário '{}' registrado com sucesso. ID: {}", username, savedUser.getId());
    }

    /**
     * Busca um usuário pelo ID.
     */
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException( id, messageSource));
        Hibernate.initialize(usuario.getInstituicoes());
        return usuario;
    }


    /**
     * Marca um usuário como deletado pelo ID.
     *
     * @param id     ID do usuário a ser deletado.
     * @param locale Locale atual para mensagens internacionalizadas.
     * @throws UsuarioNotFoundException Se o usuário com o ID fornecido não for encontrado.
     */
    public void deleteById(Long id, Locale locale) {
        log.info("Marcando o usuário como deletado com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de excluir um usuário não encontrado com ID: {}", id);
                    throw new UsuarioNotFoundException(id, messageSource);
                });

        usuario.setDeletado(true);
        usuarioRepository.save(usuario);
        log.info("Usuário com ID {} foi marcado como deletado com sucesso.", id);
    }


    /**
     * Valida o nome de usuário.
     */
    private void validateUsername(String username, Locale locale) {
        if (username == null || username.isBlank()) {
            log.error("Nome de usuário inválido: vazio ou nulo.");
            throw new InvalidUsernameException( "username.empty", messageSource);
        }
        if (username.length() < 3 || username.length() > 50) {
            log.error("Nome de usuário '{}' inválido: deve ter entre 3 e 50 caracteres.", username);
            throw new InvalidUsernameException("username.invalid_length", messageSource);
        }
        if (!Pattern.matches("^[a-zA-Z0-9_.-]+$", username)) {
            log.error("Nome de usuário '{}' contém caracteres inválidos.", username);
            throw new InvalidUsernameException( "username.invalid_chars", messageSource);
        }
    }

    /**
     * Valida o email.
     */
    private void validateEmail(String email, Locale locale) {
        if (email == null || email.isBlank()) {
            log.error("Email inválido: vazio ou nulo.");
            throw new InvalidEmailException( "email.empty", messageSource);
        }
        if (!Pattern.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email)) {
            log.error("Email '{}' é inválido.", email);
            throw new InvalidEmailException( "email.invalid", messageSource);
        }
    }

    /**
     * Valida a senha.
     */
    private void validatePassword(String password, Locale locale) {
        if (password == null || password.isBlank()) {
            log.error("Senha inválida: vazia ou nula.");
            throw new InvalidPasswordException( "password.empty", messageSource);
        }
        if (password.length() < 8) {
            log.error("Senha inválida: deve ter pelo menos 8 caracteres.");
            throw new InvalidPasswordException( "password.too_short", messageSource);
        }
        if (!Pattern.matches(".*\\d.*", password)) {
            log.error("Senha inválida: deve conter pelo menos um número.");
            throw new InvalidPasswordException( "password.missing_number", messageSource);
        }
        if (!Pattern.matches(".*[A-Z].*", password)) {
            log.error("Senha inválida: deve conter pelo menos uma letra maiúscula.");
            throw new InvalidPasswordException( "password.missing_uppercase", messageSource);
        }
        if (!Pattern.matches(".*[@#$%^&+=!].*", password)) {
            log.error("Senha inválida: deve conter pelo menos um caractere especial.");
            throw new InvalidPasswordException( "password.missing_special", messageSource);
        }
    }
}
