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
@Table(name = "ativo_financeiro")
public class AtivoFinanceiroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    // Agora, o ativo pertence ao Portfolio
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<TransacaoEntity> transacoes;

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<RendaVariavelEntity> rendaVariaveis = new ArrayList<>();

    @OneToMany(mappedBy = "ativoFinanceiro")
    @ToString.Exclude // Evita loops
    private List<RendaFixaEntity> rendaFixas;

    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;


    /**
     * Adiciona uma renda (fixa ou variável) ao ativo financeiro.
     * Além de inserir na coleção, garante que a associação com este AtivoFinanceiro seja definida.
     *
     * @param renda A renda a ser adicionada.
     */
    public void adicionarRenda(RendaEntity renda) {
        if (renda == null) {
            throw new IllegalArgumentException("Renda não pode ser nula.");
        }
        // Define explicitamente o AtivoFinanceiro na renda
        renda.setAtivoFinanceiro(this);
        if (renda instanceof RendaFixaEntity) {
            if (rendaFixas == null) {
                rendaFixas = new ArrayList<>();
            }
            rendaFixas.add((RendaFixaEntity) renda);
        } else if (renda instanceof RendaVariavelEntity) {
            if (rendaVariaveis == null) {
                rendaVariaveis = new ArrayList<>();
            }
            rendaVariaveis.add((RendaVariavelEntity) renda);
        }
    }

    /**
     * Adiciona uma transacao  ao ativo financeiro.
     * Além de inserir na coleção, garante que a associação com este AtivoFinanceiro seja definida.
     *
     * @param transacao A renda a ser adicionada.
     */
    public void adicionarTransacoes(TransacaoEntity transacao) {
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
        AtivoFinanceiroEntity that = (AtivoFinanceiroEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}