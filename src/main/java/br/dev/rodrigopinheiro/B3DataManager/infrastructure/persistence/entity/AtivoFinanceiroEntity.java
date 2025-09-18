package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Infrastructure Entity - AtivoFinanceiroEntity (SINGLE_TABLE Strategy)
 * 
 * Classe abstrata base para hierarquia de ativos financeiros.
 * Implementa padrão SINGLE_TABLE para máxima performance.
 * 
 * Subclasses:
 * - AtivoRendaFixaEntity: CDBs, Tesouro Direto, LCI/LCA, etc.
 * - AtivoRendaVariavelEntity: Ações, FIIs, ETFs, BDRs, etc.
 * 
 * Características:
 * - SINGLE_TABLE: Uma tabela para todos os tipos
 * - Discriminator: Campo tipo_ativo identifica o tipo
 * - Performance: Consultas sem JOINs
 * - Flexibilidade: Campos específicos nullable
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ativo_financeiro", indexes = {
    @Index(name = "idx_ativo_codigo", columnList = "codigo"),
    @Index(name = "idx_ativo_tipo", columnList = "tipo_ativo"),
    @Index(name = "idx_ativo_portfolio", columnList = "portfolio_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_ativo", discriminatorType = DiscriminatorType.STRING)
public abstract class AtivoFinanceiroEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Código do ativo (ticker para ações/FIIs, código para RF)
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    /**
     * Nome completo do ativo
     */
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;


    /**
     * Referência ao portfolio (agregado raiz)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    /**
     * Transações relacionadas a este ativo
     */
    @OneToMany(mappedBy = "ativoFinanceiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita loops
    private List<TransacaoEntity> transacoes = new ArrayList<>();

    /**
     * Posições relacionadas a este ativo
     */
    @OneToMany(mappedBy = "ativoFinanceiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita loops
    private List<PosicaoEntity> posicoes = new ArrayList<>();

    /**
     * Indica se o ativo foi deletado (soft delete)
     */
    @Column(name = "deletado", nullable = false)
    private boolean deletado = false;

    // Métodos abstratos para polimorfismo
    
    /**
     * Retorna o tipo geral do ativo (RENDA_FIXA ou RENDA_VARIAVEL)
     */
    public abstract TipoAtivo getTipoAtivo();
    
    /**
     * Retorna uma descrição completa do ativo com informações específicas
     */
    public abstract String getDescricaoCompleta();
    
    // Métodos de negócio comuns
    
    /**
     * Verifica se é um ativo de renda variável
     */
    public boolean isRendaVariavel() {
        return TipoAtivo.RENDA_VARIAVEL.equals(getTipoAtivo());
    }
    
    /**
     * Verifica se é um ativo de renda fixa
     */
    public boolean isRendaFixa() {
        return TipoAtivo.RENDA_FIXA.equals(getTipoAtivo());
    }
    
    /**
     * Adiciona uma transação ao ativo financeiro
     */
    public void adicionarTransacao(TransacaoEntity transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        transacao.setAtivoFinanceiro(this);
        if (transacoes == null) {
            transacoes = new ArrayList<>();
        }
        transacoes.add(transacao);
    }
    
    /**
     * Adiciona uma posição ao ativo financeiro
     */
    public void adicionarPosicao(PosicaoEntity posicao) {
        if (posicao == null) {
            throw new IllegalArgumentException("Posição não pode ser nula.");
        }
        posicao.setAtivoFinanceiro(this);
        if (posicoes == null) {
            posicoes = new ArrayList<>();
        }
        posicoes.add(posicao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AtivoFinanceiroEntity that = (AtivoFinanceiroEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}