package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portfolio")
public class PortfolioEntity extends AuditableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associação direta com o usuário
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    // Agrega os ativos financeiros do usuário
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<AtivoFinanceiroEntity> ativosFinanceiro = new HashSet<>();

    // Agrega as transações realizadas no portfolio
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TransacaoEntity> transacoes = new ArrayList<>();

    @Column(name = "saldo_total")
    private BigDecimal saldoTotal;

    @Column(name = "saldo_aplicado")
    private BigDecimal saldoAplicado;

    @Column(name = "lucro_venda")
    private BigDecimal lucroVenda;

    @Column(name = "lucro_rendimento")
    private BigDecimal lucroRendimento;

    @Column(name = "lucro_nao_realizado")
    private BigDecimal lucroNaoRealizado;

    /**
     * Adiciona um ativo financeiro ao portfolio, evitando duplicações.
     * Caso o ativo já exista, a operação é ignorada e um aviso pode ser registrado.
     *
     * @param ativo O ativo financeiro a ser adicionado.
     */
    public void adicionarAtivoFinanceiro(AtivoFinanceiroEntity ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }
        if (this.ativosFinanceiro.contains(ativo)) {
            // Opção: ignorar a inserção ou atualizar o registro conforme a regra de negócio.
            log.info("Ativo já existe no portfolio. Operação ignorada.");
            return;
        }
        this.ativosFinanceiro.add(ativo);
        ativo.setPortfolio(this);
    }

    /**
     * Remove um ativo financeiro do portfolio e desfaz o relacionamento.
     *
     * @param ativo O ativo financeiro a ser removido.
     */
    public void removerAtivoFinanceiro(AtivoFinanceiroEntity ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }
        ativosFinanceiro.remove(ativo);
        ativo.setPortfolio(null);
    }

    /**
     * Adiciona uma transação ao portfolio.
     * NOTA: Cálculos de saldo devem ser feitos na camada de domínio/aplicação.
     *
     * @param transacao A transação a ser adicionada.
     */
    public void adicionarTransacao(TransacaoEntity transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        transacao.setPortfolio(this);
        this.transacoes.add(transacao);
    }

    /**
     * Remove uma transação do portfolio.
     * NOTA: Recálculos de saldo devem ser feitos na camada de domínio/aplicação.
     *
     * @param transacao A transação a ser removida.
     */
    public void removerTransacao(TransacaoEntity transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        if (this.transacoes.remove(transacao)) {
            transacao.setPortfolio(null);
        }
    }

    // REMOVIDO: Métodos de cálculo de saldo movidos para a camada de domínio/aplicação
    // conforme arquitetura hexagonal. Entidades de infraestrutura devem ser apenas
    // estruturas de dados para persistência.


    // REMOVIDO: recalcularSaldos() - Lógica de negócio movida para camada de domínio


    // REMOVIDO: calcularImpactoTransacao() - Lógica de negócio movida para camada de domínio


    /**
     * Verifica se o portfolio contém o ativo financeiro informado.
     *
     * @param ativo O ativo financeiro a ser verificado.
     * @return true se o ativo estiver presente; false caso contrário.
     */
    public boolean possuiAtivo(AtivoFinanceiroEntity ativo) {
        return ativo != null && this.ativosFinanceiro.contains(ativo);
    }

    /**
     * Busca os ativos de renda fixa deste portfolio.
     * 
     * @return Lista de ativos de renda fixa.
     */
    public List<AtivoFinanceiroEntity> buscarAtivosRendaFixa() {
        return ativosFinanceiro.stream()
                .filter(Objects::nonNull)
                .filter(AtivoFinanceiroEntity::isRendaFixa)
                .collect(Collectors.toList());
    }

    /**
     * Busca os ativos de renda variável deste portfolio.
     * 
     * @return Lista de ativos de renda variável.
     */
    public List<AtivoFinanceiroEntity> buscarAtivosRendaVariavel() {
        return ativosFinanceiro.stream()
                .filter(Objects::nonNull)
                .filter(AtivoFinanceiroEntity::isRendaVariavel)
                .collect(Collectors.toList());
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PortfolioEntity portfolio = (PortfolioEntity) o;
        return getId() != null && Objects.equals(getId(), portfolio.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}