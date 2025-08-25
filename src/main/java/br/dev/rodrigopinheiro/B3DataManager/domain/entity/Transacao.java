package br.dev.rodrigopinheiro.B3DataManager.domain.entity;


import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "data")
    private LocalDate data;

    @Column(name = "entrada_saida")
    private String entradaSaida; // Entrada ou Saída

    @Column(name = "quantidade")
    private double quantidade;

    @Column(name = "preco_unitario")
    private BigDecimal precoUnitario;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "preco_medio")
    private BigDecimal precoMedio;

    @Column(name = "tipo_transacao")
    private String tipoTransacao;

    @Column(name = "tipo_movimentacao")
    private String tipoMovimentacao;

    @Column(name = "deletado")
    private Boolean deletado = false;

    @ManyToOne
    @JoinColumn(name = "ativo_financeiro_id")
    private AtivoFinanceiro ativoFinanceiro;

    @ManyToOne
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;

    @ManyToOne
    @JoinColumn(name = "darf_id")
    private Darf darf;

    @ManyToOne
    @JoinColumn(name = "operacao_id")
    private Operacao operacao;

    // Associação com o Portfolio (agregado raiz financeiro)
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao.name();
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao.name();
    }



    /**
     * Associa um AtivoFinanceiro à Transação.
     *
     * Este métod garante que o ativo financeiro não seja nulo e
     * pode conter validações adicionais caso haja a necessidade de
     * impor invariantes, como verificar se a transação já possui um ativo associado.
     *
     * @param ativoFinanceiro o ativo financeiro a ser associado
     * @throws IllegalArgumentException se o ativo financeiro for nulo
     * @throws IllegalStateException se já houver um ativo financeiro associado (opcional)
     */
    public void associarAtivoFinanceiro(AtivoFinanceiro ativoFinanceiro) {
        if (ativoFinanceiro == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }

        // Se desejarmos evitar a substituição de um ativo já associado,
        // podemos descomentar a validação abaixo:
        // if (this.ativoFinanceiro != null && !this.ativoFinanceiro.equals(ativoFinanceiro)) {
        //     throw new IllegalStateException("Transação já possui um ativo financeiro associado.");
        // }

        this.ativoFinanceiro = ativoFinanceiro;
    }

    /**
     * Associa uma Instituição à Transação.
     *
     * Este métod garante que a instituição não seja nula, evitando que a transação seja associada a um
     * objeto inválido. Caso a instituição já tenha sido definida e houver necessidade de evitar a sua substituição,
     * pode-se adicionar uma validação extra (por exemplo, lançando uma exceção se já houver uma instituição associada).
     *
     * @param instituicao a instituição a ser associada à transação
     * @throws IllegalArgumentException se a instituição for nula
     */
    public void associarInstituicao(Instituicao instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }

        // Se desejarmos evitar a substituição da instituição já associada, podemos descomentar o trecho abaixo:
        // if (this.instituicao != null && !this.instituicao.equals(instituicao)) {
        //     throw new IllegalStateException("Transação já possui uma instituição associada.");
        // }

        this.instituicao = instituicao;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Transacao transacao = (Transacao) o;
        return getId() != null && Objects.equals(getId(), transacao.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
