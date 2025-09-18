package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
     * Referência ao usuário (objeto completo para manter consistência do domain)
     */
    private Usuario usuario;
    
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

    public Portfolio(Usuario usuario) {
        this();
        this.usuario = usuario;
    }
    
    // Métodos de negócio da Opção 1
    
    /**
     * Adiciona um ativo financeiro ao catálogo do portfolio
     */
    public void adicionarAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null) return;
        
        // Validar campos obrigatórios do ativo
        ativo.validarCamposObrigatorios();
        
        // Associar ao portfolio
        ativo.setPortfolio(this);
        
        this.ativosFinanceiro.add(ativo);
    }
    
    /**
     * Remove um ativo financeiro do portfolio (soft delete)
     */
    public void removerAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null) return;
        
        ativo.setDeletado(true);
        // Não remove da coleção para manter histórico
    }
    
    /**
     * Busca um ativo financeiro pelo código
     */
    public AtivoFinanceiro buscarAtivoPorCodigo(String codigo) {
        return ativosFinanceiro.stream()
            .filter(ativo -> !Boolean.TRUE.equals(ativo.getDeletado()))
            .filter(ativo -> codigo.equals(ativo.getCodigo()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Retorna apenas ativos ativos (não deletados)
     */
    public Set<AtivoFinanceiro> getAtivosAtivos() {
        return ativosFinanceiro.stream()
            .filter(ativo -> !Boolean.TRUE.equals(ativo.getDeletado()))
            .collect(java.util.stream.Collectors.toSet());
    }
    
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
                        .divide(saldoTotal, 4, RoundingMode.HALF_UP)
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
    
    /**
     * Retorna apenas ativos de renda fixa
     */
    public Set<AtivoRendaFixa> getAtivosRendaFixa() {
        return ativosFinanceiro.stream()
            .filter(ativo -> !Boolean.TRUE.equals(ativo.getDeletado()))
            .filter(ativo -> ativo instanceof AtivoRendaFixa)
            .map(ativo -> (AtivoRendaFixa) ativo)
            .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Retorna apenas ativos de renda variável
     */
    public Set<AtivoRendaVariavel> getAtivosRendaVariavel() {
        return ativosFinanceiro.stream()
            .filter(ativo -> !Boolean.TRUE.equals(ativo.getDeletado()))
            .filter(ativo -> ativo instanceof AtivoRendaVariavel)
            .map(ativo -> (AtivoRendaVariavel) ativo)
            .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Calcula o valor total investido em renda fixa
     */
    public BigDecimal getValorRendaFixa() {
        return posicoes.stream()
            .filter(Posicao::isPosicaoAtiva)
            .filter(posicao -> posicao.getAtivoFinanceiro().isRendaFixa())
            .map(Posicao::getValorAtual)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calcula o valor total investido em renda variável
     */
    public BigDecimal getValorRendaVariavel() {
        return posicoes.stream()
            .filter(Posicao::isPosicaoAtiva)
            .filter(posicao -> posicao.getAtivoFinanceiro().isRendaVariavel())
            .map(Posicao::getValorAtual)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calcula o percentual de alocação em renda fixa
     */
    public BigDecimal getPercentualRendaFixa() {
        if (saldoTotal == null || saldoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return getValorRendaFixa()
            .divide(saldoTotal, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Calcula o percentual de alocação em renda variável
     */
    public BigDecimal getPercentualRendaVariavel() {
        if (saldoTotal == null || saldoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return getValorRendaVariavel()
            .divide(saldoTotal, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    // Getters e Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método de conveniência para obter o ID do usuário
     */
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
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


