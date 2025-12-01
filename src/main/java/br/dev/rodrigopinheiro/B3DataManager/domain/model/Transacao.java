package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain Model - Transacao
 * 
 * Representa o histórico imutável de operações financeiras.
 * Separado de {@link Posicao} (estado atual) seguindo padrão Event
 * Sourcing/CQRS.
 * 
 * <h3>Princípios:</h3>
 * <ul>
 * <li><b>Imutabilidade:</b> Transações não devem ser alteradas após
 * criação</li>
 * <li><b>Auditoria:</b> Mantém histórico completo para compliance</li>
 * <li><b>Separação:</b> Histórico (Transacao) vs Estado Atual (Posicao)</li>
 * </ul>
 * 
 * <h3>Tipos de Transação:</h3>
 * <ul>
 * <li><b>ENTRADA:</b> Compra de ativos</li>
 * <li><b>VENDA:</b> Venda de ativos</li>
 * <li><b>LUCRO_DIVIDENDO:</b> Recebimento de dividendos</li>
 * <li><b>LUCRO_JUROS:</b> Recebimento de juros (renda fixa)</li>
 * </ul>
 * 
 * <h3>Cálculos Automáticos:</h3>
 * 
 * <pre>{@code
 * valorTotal = quantidade × precoUnitario
 * valorLiquido = valorTotal - taxas
 * }</pre>
 * 
 * <p>
 * <b>Importante:</b> Os setters {@link #setQuantidade},
 * {@link #setPrecoUnitario}
 * e {@link #setTaxas} recalculam automaticamente os valores derivados.
 * </p>
 * 
 * @author Rodrigo Pinheiro
 * @see Posicao
 * @see Portfolio
 * @see TipoTransacao
 */
public class Transacao {
    private Long id;

    /**
     * Data da operação
     */
    private LocalDate dataOperacao;

    /**
     * Tipo da transação (COMPRA, VENDA, RENDIMENTO, etc.)
     */
    private TipoTransacao tipoTransacao;

    /**
     * Tipo da movimentação (CREDITO, DEBITO, etc.)
     */
    private TipoMovimentacao tipoMovimentacao;

    /**
     * Quantidade de ativos na transação
     */
    private BigDecimal quantidade;

    /**
     * Preço unitário da transação
     */
    private BigDecimal precoUnitario;

    /**
     * Valor total da transação (quantidade * precoUnitario)
     */
    private BigDecimal valorTotal;

    /**
     * Taxas e custos da transação
     */
    private BigDecimal taxas;

    /**
     * Valor líquido da transação (valorTotal - taxas)
     */
    private BigDecimal valorLiquido;

    /**
     * Observações sobre a transação
     */
    private String observacoes;

    /**
     * Referência ao ativo financeiro (objeto completo)
     */
    private AtivoFinanceiro ativoFinanceiro;

    /**
     * Referência à instituição (objeto completo)
     */
    private Instituicao instituicao;

    /**
     * Referência ao portfolio (objeto completo)
     */
    private Portfolio portfolio;

    /**
     * Referência à operação original (objeto completo)
     */
    private Operacao operacao;

    /**
     * Flag de controle para soft delete
     */
    private Boolean deletado = false;

    public Transacao() {
        this.deletado = false;
        this.taxas = BigDecimal.ZERO;
    }

    public Transacao(LocalDate dataOperacao, TipoTransacao tipoTransacao, TipoMovimentacao tipoMovimentacao,
            BigDecimal quantidade, BigDecimal precoUnitario, AtivoFinanceiro ativoFinanceiro,
            Portfolio portfolio, Instituicao instituicao) {
        this.dataOperacao = dataOperacao;
        this.tipoTransacao = tipoTransacao;
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.ativoFinanceiro = ativoFinanceiro;
        this.portfolio = portfolio;
        this.instituicao = instituicao;
        this.deletado = false;
        this.taxas = BigDecimal.ZERO;

        // Calcula valores derivados
        calcularValores();
    }

    // Métodos de negócio

    /**
     * Calcula valores derivados da transação
     */
    private void calcularValores() {
        if (quantidade != null && precoUnitario != null) {
            this.valorTotal = quantidade.multiply(precoUnitario);
            this.valorLiquido = valorTotal.subtract(taxas != null ? taxas : BigDecimal.ZERO);
        }
    }

    /**
     * Verifica se é uma transação de compra
     */
    public boolean isCompra() {
        return TipoTransacao.ENTRADA.equals(tipoTransacao);
    }

    /**
     * Verifica se é uma transação de venda
     */
    public boolean isVenda() {
        return TipoTransacao.VENDA.equals(tipoTransacao);
    }

    /**
     * Verifica se é uma transação de rendimento
     */
    public boolean isRendimento() {
        return TipoTransacao.LUCRO_DIVIDENDO.equals(tipoTransacao) ||
                TipoTransacao.LUCRO_JUROS.equals(tipoTransacao) ||
                TipoTransacao.LUCRO_RENDIMENTO.equals(tipoTransacao);
    }

    /**
     * Adiciona taxas à transação e recalcula valores
     */
    public void adicionarTaxas(BigDecimal taxas) {
        this.taxas = taxas != null ? taxas : BigDecimal.ZERO;
        calcularValores();
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(LocalDate dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Define a quantidade de ativos na transação.
     * <p>
     * <b>Importante:</b> Este método recalcula automaticamente os valores
     * derivados (valorTotal e valorLiquido).
     * </p>
     * 
     * @param quantidade Quantidade de ativos (deve ser maior que zero)
     * @see #calcularValores()
     */
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
        calcularValores();
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * Define o preço unitário da transação.
     * <p>
     * <b>⚠️ Importante:</b> Este método recalcula automaticamente os valores
     * derivados (valorTotal e valorLiquido) através de {@link #calcularValores()}.
     * </p>
     * 
     * @param precoUnitario Preço por unidade do ativo
     * @see #calcularValores()
     * @see #getValorTotal()
     * @see #getValorLiquido()
     */
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
        calcularValores();
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getTaxas() {
        return taxas;
    }

    /**
     * Define as taxas da transação (corretagem, custódia, etc).
     * <p>
     * <b>⚠️ Importante:</b> Este método recalcula automaticamente o valor
     * líquido através de {@link #calcularValores()}.
     * </p>
     * 
     * @param taxas Valor total de taxas e custos
     * @see #calcularValores()
     * @see #adicionarTaxas(BigDecimal)
     * @see #getValorLiquido()
     */
    public void setTaxas(BigDecimal taxas) {
        this.taxas = taxas;
        calcularValores();
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public AtivoFinanceiro getAtivoFinanceiro() {
        return ativoFinanceiro;
    }

    public void setAtivoFinanceiro(AtivoFinanceiro ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
}
