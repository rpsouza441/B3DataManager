package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;


import br.dev.rodrigopinheiro.B3DataManager.domain.enums.Roles;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.StatusConta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidade que representa um usuário do sistema B3DataManager.
 * 
 * Centraliza todas as informações relacionadas ao usuário, incluindo:
 * - Dados de autenticação (username, password, email)
 * - Perfis e permissões (roles)
 * - Relacionamentos com instituições financeiras
 * - Portfolio pessoal de investimentos
 * - Status da conta e controle de acesso
 * 
 * Características:
 * - Validação robusta de dados com Bean Validation
 * - Relacionamentos bidirecionais com outras entidades
 * - Auditoria automática via AuditableEntity
 * - Soft delete para manter integridade
 * - Controle de status da conta
 * - Rastreamento de último acesso
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "usuario")
public class UsuarioEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "O nome de usuário não pode estar vazio.")
    @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "A senha não pode estar vazia.")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
    private String password;

    @Column(nullable = false, unique = true)
    @Email(message = "O email deve ser válido.")
    @NotBlank(message = "O email não pode estar vazio.")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;

    @ManyToMany
    @ToString.Exclude // Evita loops
    @JoinTable(
            name = "usuario_instituicao",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "instituicao_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "instituicao_id"})

    )
    private Set<InstituicaoEntity> instituicoes;


    // O Portfolio é o agregado que reúne ativos e transações
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, optional = true)
    @ToString.Exclude
    private PortfolioEntity portfolio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta", nullable = false)
    private StatusConta statusConta = StatusConta.ATIVA;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    public void associarInstituicao(InstituicaoEntity instituicao) {
            // Define explicitamente o AtivoFinanceiro na renda
            if (this.instituicoes == null) {
                    instituicoes = new HashSet<>();
                }
        instituicoes.add(instituicao);

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UsuarioEntity usuario = (UsuarioEntity) o;
        return getId() != null && Objects.equals(getId(), usuario.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}