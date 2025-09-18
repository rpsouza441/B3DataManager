package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa uma instituição financeira no sistema B3DataManager.
 * 
 * Centraliza todas as informações relacionadas a instituições, incluindo:
 * - Dados básicos da instituição (nome)
 * - Relacionamentos com usuários proprietários
 * - Transações associadas à instituição
 * - Controle de acesso e permissões
 * 
 * Características:
 * - Relacionamento many-to-many com usuários
 * - Relacionamento one-to-many com transações
 * - Auditoria automática via AuditableEntity
 * - Soft delete para manter integridade referencial
 * - Métodos de conveniência para associações
 * - Prevenção de loops em toString
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "instituicao")
public class InstituicaoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "instituicoes")
    @ToString.Exclude // Evita loops
    private List<UsuarioEntity> usuarios;

    @OneToMany(mappedBy = "instituicao")
    @ToString.Exclude // Evita loops
    private List<TransacaoEntity> transacoes;

    public void associarUsuario(UsuarioEntity usuario) {
        // Define explicitamente o AtivoFinanceiro na renda
        if (this.usuarios == null) {
            usuarios = new ArrayList<UsuarioEntity>();
        }
        usuarios.add(usuario);

    }

    public void adicionarTransacoes(TransacaoEntity transacao) {

        // Define explicitamente o AtivoFinanceiro na renda
        transacao.setInstituicao(this);

        if (transacoes == null) {
            transacoes = new ArrayList<>();
        }
        transacoes.add(transacao);

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        InstituicaoEntity that = (InstituicaoEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}