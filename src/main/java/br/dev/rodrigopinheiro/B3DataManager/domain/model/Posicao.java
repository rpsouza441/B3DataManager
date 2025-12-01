package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Domain Model - Posicao
 * 
 * Representa o estado atual de um ativo financeiro no portfolio.
 * Separado do histórico de transações para otimizar consultas e cálculos.
 * 
 * <h3>Regras de Negócio:</h3>
 * <ul>
 * <li><b>Preço Médio:</b> Calculado por média ponderada nas compras. Não muda
 * em vendas.</li>
 * <li><b>Valor Investido:</b> Custo de aquisição total. Ajustado
 * proporcionalmente em vendas.</li>
 * <li><b>Valor Atual:</b> Quantidade atual × preço de mercado atualizado.</li>
 * <li><b>Lucro Não Realizado:</b> Diferença entre valor atual e valor
 * investido.</li>
 * </ul>
 * 
 * <h3>Comportamento em Transações:</h3>
 * <ul>
 * <li><b>Compra:</b> Recalcula preço médio ponderado e aumenta valor
 * investido.</li>
 * <li><b>Venda:</b> Remove proporção do custo (não usa preço de venda). Preço
 * médio inalterado.</li>
 * <li><b>Venda Total:</b> Zera quantidade, preço médio e valor investido.</li>
 * </ul>
 * 
 * <h3>Exemplo:</h3>
 * 
 * <pre>{@code
 * Posicao pos = new Posicao(ativo, portfolio);
 * // Compra 100 ações a R$ 10
 * pos.atualizarComTransacao(new BigDecimal("100"), new BigDecimal("10"), true);
 * // precoMedio = R$ 10, valorInvestido = R$ 1.000
 * 
 * // Vende 30 ações a R$ 15
 * pos.atualizarComTransacao(new BigDecimal("30"), new BigDecimal("15"), false);
 * // quantidadeAtual = 70
 * // precoMedio = R$ 10 (não muda!)
 * // valorInvestido = R$ 700 (removeu 30% de R$ 1.000)
 * }</pre>
 */
public class Posicao {

    private Long id;

    /**
     * Quantidade atual do ativo (resultado de todas as transações)
     */
    private BigDecimal quantidadeAtual;

    /**
     * Preço médio de aquisição (ponderado pelas compras)
     */
    private BigDecimal precoMedio;

    /**
     * Valor atual da posição (quantidade * preço atual de mercado)
     */
    private BigDecimal valorAtual;

    /**
     * Percentual que esta posição representa no portfolio total
     * PRÉ-CALCULADO para performance (evita cálculo em tempo real)
     */
    private BigDecimal percentualPortfolio;

    /**
     * Data da última atualização da posição
     */
    private LocalDate dataUltimaAtualizacao;

    /**
     * Lucro/prejuízo não realizado (valor atual - valor investido)
     */
    private BigDecimal lucroNaoRealizado;

    /**
     * Percentual de ganho/perda da posição ((valor atual - valor investido) / valor
     * investido * 100)
     */
    private BigDecimal percentualGanho;

    /**
     * Valor total investido na posição (soma das compras - vendas)
     */
    private BigDecimal valorInvestido;

    /**
     * Referência ao ativo financeiro (objeto completo)
     */
    private AtivoFinanceiro ativoFinanceiro;

    /**
     * Referência ao portfolio (objeto completo)
     */
    private Portfolio portfolio;

    /**
     * Flag de controle para soft delete
     */
    private Boolean ativo = true;

    // Construtores
    public Posicao() {
    }

    public Posicao(AtivoFinanceiro ativoFinanceiro, Portfolio portfolio) {
        this.ativoFinanceiro = ativoFinanceiro;
        this.portfolio = portfolio;
        this.quantidadeAtual = BigDecimal.ZERO;
        this.precoMedio = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
        this.percentualPortfolio = BigDecimal.ZERO;
        this.lucroNaoRealizado = BigDecimal.ZERO;
        this.percentualGanho = BigDecimal.ZERO;
        this.valorInvestido = BigDecimal.ZERO;
        this.dataUltimaAtualizacao = LocalDate.now();
        this.ativo = true;
    }

    // Métodos de negócio

    /**
     * Atualiza a posição com base em uma nova transação
     */
    public void atualizarComTransacao(BigDecimal quantidade, BigDecimal precoUnitario, boolean isCompra) {
        if (isCompra) {
            adicionarQuantidade(quantidade, precoUnitario);
        } else {
            removerQuantidade(quantidade, precoUnitario);
        }
        this.dataUltimaAtualizacao = LocalDate.now();
    }

    /**
     * Adiciona quantidade à posição (compra)
     */
    private void adicionarQuantidade(BigDecimal quantidade, BigDecimal precoUnitario) {
        BigDecimal valorAnterior = this.quantidadeAtual.multiply(this.precoMedio);
        BigDecimal valorNovo = quantidade.multiply(precoUnitario);
        BigDecimal quantidadeNova = this.quantidadeAtual.add(quantidade);

        if (quantidadeNova.compareTo(BigDecimal.ZERO) > 0) {
            this.precoMedio = valorAnterior.add(valorNovo).divide(quantidadeNova, 2, RoundingMode.HALF_UP);
        }

        this.quantidadeAtual = quantidadeNova;
        this.valorInvestido = this.valorInvestido.add(valorNovo);
    }

    /**
     * Remove quantidade da posição (venda)
     */
    private void removerQuantidade(BigDecimal quantidade, BigDecimal precoUnitario) {
        if (this.quantidadeAtual.compareTo(quantidade) < 0) {
            throw new IllegalArgumentException("Quantidade insuficiente. Disponível: " + this.quantidadeAtual);
        }
        BigDecimal proporcao = quantidade.divide(this.quantidadeAtual, 8, RoundingMode.HALF_UP);
        BigDecimal valorRemovido = this.valorInvestido.multiply(proporcao);
        BigDecimal quantidadeNova = this.quantidadeAtual.subtract(quantidade);

        if (quantidadeNova.compareTo(BigDecimal.ZERO) <= 0) {
            this.quantidadeAtual = BigDecimal.ZERO;
            this.valorInvestido = BigDecimal.ZERO;
            this.precoMedio = BigDecimal.ZERO;
        } else {
            this.quantidadeAtual = quantidadeNova;
            this.valorInvestido = this.valorInvestido.subtract(valorRemovido);
        }

    }

    /**
     * Atualiza o valor atual com base no preço de mercado
     */
    public void atualizarValorAtual(BigDecimal precoMercado) {
        this.valorAtual = this.quantidadeAtual.multiply(precoMercado);
        this.lucroNaoRealizado = this.valorAtual.subtract(this.valorInvestido);
    }

    /**
     * Verifica se a posição está ativa (quantidade > 0)
     */
    public boolean isPosicaoAtiva() {
        return this.quantidadeAtual.compareTo(BigDecimal.ZERO) > 0;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }

    public void setPrecoMedio(BigDecimal precoMedio) {
        this.precoMedio = precoMedio;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal getPercentualPortfolio() {
        return percentualPortfolio;
    }

    public void setPercentualPortfolio(BigDecimal percentualPortfolio) {
        this.percentualPortfolio = percentualPortfolio;
    }

    public LocalDate getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDate dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public BigDecimal getLucroNaoRealizado() {
        return lucroNaoRealizado;
    }

    public void setLucroNaoRealizado(BigDecimal lucroNaoRealizado) {
        this.lucroNaoRealizado = lucroNaoRealizado;
    }

    public BigDecimal getPercentualGanho() {
        return percentualGanho;
    }

    public void setPercentualGanho(BigDecimal percentualGanho) {
        this.percentualGanho = percentualGanho;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public AtivoFinanceiro getAtivoFinanceiro() {
        return ativoFinanceiro;
    }

    public void setAtivoFinanceiro(AtivoFinanceiro ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}