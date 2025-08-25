package br.dev.rodrigopinheiro.B3DataManager.domain.entity;

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
@Table(name = "ativo_financeiro")
public class AtivoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    // Agora, o ativo pertence ao Portfolio
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<Transacao> transacoes;

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<RendaVariavel> rendaVariaveis = new ArrayList<>();

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<RendaFixa> rendaFixas;

    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;


    /**
     * Adiciona uma renda (fixa ou variável) ao ativo financeiro.
     * Além de inserir na coleção, garante que a associação com este AtivoFinanceiro seja definida.
     *
     * @param renda A renda a ser adicionada.
     */
    public void adicionarRenda(Renda renda) {
        if (renda == null) {
            throw new IllegalArgumentException("Renda não pode ser nula.");
        }
        // Define explicitamente o AtivoFinanceiro na renda
        renda.setAtivoFinanceiro(this);
        if (renda instanceof RendaFixa) {
            if (rendaFixas == null) {
                rendaFixas = new ArrayList<>();
            }
            rendaFixas.add((RendaFixa) renda);
        } else if (renda instanceof RendaVariavel) {
            if (rendaVariaveis == null) {
                rendaVariaveis = new ArrayList<>();
            }
            rendaVariaveis.add((RendaVariavel) renda);
        }
    }

    /**
     * Adiciona uma transacao  ao ativo financeiro.
     * Além de inserir na coleção, garante que a associação com este AtivoFinanceiro seja definida.
     *
     * @param transacao A renda a ser adicionada.
     */
    public void adicionarTransacoes(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transacao não pode ser nula.");
        }
        // Define explicitamente o AtivoFinanceiro na renda
        transacao.setAtivoFinanceiro(this);

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
        AtivoFinanceiro that = (AtivoFinanceiro) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}