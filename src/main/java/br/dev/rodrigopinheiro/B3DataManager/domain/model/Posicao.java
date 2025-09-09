package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain Model - Posicao
 * Representa o estado atual de um ativo financeiro no portfolio.
 * Separado do histórico de transações para otimizar consultas e cálculos.
 * 
 * Princípios da Opção 1:
 * - Estado atual vs histórico (Transacao)
 * - Cálculos pré-computados (percentualPortfolio)
 * - POJO puro sem dependências externas
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
            removerQuantidade(quantidade);
        }
        this.dataUltimaAtualizacao = LocalDate.now();
    }
    
    /**
     * Adiciona quantidade à posição (compra)
     */
    private void adicionarQuantidade(BigDecimal quantidade, BigDecimal precoUnitario) {
        BigDecimal valorAnterior = this.quantidadeAtual.multiply(this.precoMedio);
        BigDecimal valorNovo = quantidade.multiply(precoUnitario);
        BigDecimal quantidadeTotal = this.quantidadeAtual.add(quantidade);
        
        if (quantidadeTotal.compareTo(BigDecimal.ZERO) > 0) {
            this.precoMedio = valorAnterior.add(valorNovo).divide(quantidadeTotal, 2, java.math.RoundingMode.HALF_UP);
        }
        
        this.quantidadeAtual = quantidadeTotal;
        this.valorInvestido = this.valorInvestido.add(valorNovo);
    }
    
    /**
     * Remove quantidade da posição (venda)
     */
    private void removerQuantidade(BigDecimal quantidade) {
        this.quantidadeAtual = this.quantidadeAtual.subtract(quantidade);
        
        // Se zerou a posição, zera o preço médio
        if (this.quantidadeAtual.compareTo(BigDecimal.ZERO) <= 0) {
            this.quantidadeAtual = BigDecimal.ZERO;
            this.precoMedio = BigDecimal.ZERO;
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