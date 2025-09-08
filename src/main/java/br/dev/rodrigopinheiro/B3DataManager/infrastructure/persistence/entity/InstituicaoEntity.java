package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "instituicao")
public class InstituicaoEntity {

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