package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Domain Model - Portfolio (Opção 1)
 * 
 * Agregado raiz que gerencia:
 * - Transacoes (histórico completo)
 * - Posicoes (estado atual dos ativos)
 * - AtivoFinanceiro (catálogo de ativos)
 * 
 * Princípios da Opção 1:
 * - Separação clara entre histórico e estado atual
 * - Cálculos consolidados e otimizados
 * - POJO puro sem dependências externas
 */
public class Portfolio {
    private Long id;
    
    /**
     * Referência ao usuário (ID apenas para manter pureza do domain)
     */
    private Long usuarioId;
    
    /**
     * Catálogo de ativos financeiros do portfolio
     */
    private Set<AtivoFinanceiro> ativosFinanceiro = new HashSet<>();
    
    /**
     * Histórico completo de transações (Opção 1)
     */
    private List<Transacao> transacoes = new ArrayList<>();
    
    /**
     * Posições atuais dos ativos (Opção 1)
     * Estado atual calculado a partir das transações
     */
    private List<Posicao> posicoes = new ArrayList<>();
    
    /**
     * Saldo total do portfolio (soma de todas as posições)
     */
    private BigDecimal saldoTotal;
    
    /**
     * Saldo aplicado (valor investido)
     */
    private BigDecimal saldoAplicado;
    
    /**
     * Lucro realizado com vendas
     */
    private BigDecimal lucroVenda;
    
    /**
     * Lucro com rendimentos (dividendos, juros, etc.)
     */
    private BigDecimal lucroRendimento;
    
    /**
     * Lucro não realizado (valorização/desvalorização)
     */
    private BigDecimal lucroNaoRealizado;
    
    /**
     * Flag de controle para soft delete
     */
    private Boolean deletado = false;

    public Portfolio() {
        this.ativosFinanceiro = new HashSet<>();
        this.transacoes = new ArrayList<>();
        this.posicoes = new ArrayList<>();
        this.saldoTotal = BigDecimal.ZERO;
        this.saldoAplicado = BigDecimal.ZERO;
        this.lucroVenda = BigDecimal.ZERO;
        this.lucroRendimento = BigDecimal.ZERO;
        this.lucroNaoRealizado = BigDecimal.ZERO;
        this.deletado = false;
    }

    public Portfolio(Long usuarioId) {
        this();
        this.usuarioId = usuarioId;
    }
    
    // Métodos de negócio da Opção 1
    
    /**
     * Adiciona uma nova transação e atualiza a posição correspondente
     */
    public void adicionarTransacao(Transacao transacao) {
        if (transacao == null) return;
        
        this.transacoes.add(transacao);
        atualizarPosicao(transacao);
        recalcularSaldos();
    }
    
    /**
     * Atualiza ou cria uma posição baseada na transação
     */
    private void atualizarPosicao(Transacao transacao) {
        Posicao posicao = encontrarPosicao(transacao.getAtivoFinanceiro());
        
        if (posicao == null) {
            posicao = new Posicao(transacao.getAtivoFinanceiro(), this);
            this.posicoes.add(posicao);
        }
        
        posicao.atualizarComTransacao(
            transacao.getQuantidade(),
            transacao.getPrecoUnitario(),
            transacao.isCompra()
        );
    }
    
    /**
     * Encontra uma posição pelo ativo financeiro
     */
    private Posicao encontrarPosicao(AtivoFinanceiro ativoFinanceiro) {
        return posicoes.stream()
            .filter(p -> p.getAtivoFinanceiro().equals(ativoFinanceiro))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Recalcula todos os saldos do portfolio
     */
    public void recalcularSaldos() {
        this.saldoTotal = posicoes.stream()
            .filter(Posicao::isPosicaoAtiva)
            .map(Posicao::getValorAtual)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        this.saldoAplicado = posicoes.stream()
            .filter(Posicao::isPosicaoAtiva)
            .map(Posicao::getValorInvestido)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        this.lucroNaoRealizado = this.saldoTotal.subtract(this.saldoAplicado);
    }
    
    /**
     * Atualiza os percentuais de todas as posições
     */
    public void atualizarPercentuais() {
        if (saldoTotal.compareTo(BigDecimal.ZERO) > 0) {
            posicoes.forEach(posicao -> {
                if (posicao.isPosicaoAtiva()) {
                    BigDecimal percentual = posicao.getValorAtual()
                        .divide(saldoTotal, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                    posicao.setPercentualPortfolio(percentual);
                }
            });
        }
    }
    
    /**
     * Retorna posições ativas (quantidade > 0)
     */
    public List<Posicao> getPosicoesAtivas() {
        return posicoes.stream()
            .filter(Posicao::isPosicaoAtiva)
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters e Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Set<AtivoFinanceiro> getAtivosFinanceiro() {
        return ativosFinanceiro;
    }

    public void setAtivosFinanceiro(Set<AtivoFinanceiro> ativosFinanceiro) {
        this.ativosFinanceiro = ativosFinanceiro != null ? ativosFinanceiro : new HashSet<>();
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes != null ? transacoes : new ArrayList<>();
    }

    public List<Posicao> getPosicoes() {
        return posicoes;
    }

    public void setPosicoes(List<Posicao> posicoes) {
        this.posicoes = posicoes != null ? posicoes : new ArrayList<>();
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public BigDecimal getSaldoAplicado() {
        return saldoAplicado;
    }

    public void setSaldoAplicado(BigDecimal saldoAplicado) {
        this.saldoAplicado = saldoAplicado;
    }

    public BigDecimal getLucroVenda() {
        return lucroVenda;
    }

    public void setLucroVenda(BigDecimal lucroVenda) {
        this.lucroVenda = lucroVenda;
    }

    public BigDecimal getLucroRendimento() {
        return lucroRendimento;
    }

    public void setLucroRendimento(BigDecimal lucroRendimento) {
        this.lucroRendimento = lucroRendimento;
    }

    public BigDecimal getLucroNaoRealizado() {
        return lucroNaoRealizado;
    }

    public void setLucroNaoRealizado(BigDecimal lucroNaoRealizado) {
        this.lucroNaoRealizado = lucroNaoRealizado;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
}


