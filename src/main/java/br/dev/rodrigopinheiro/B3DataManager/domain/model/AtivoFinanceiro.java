package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;

/**
 * Domain Model - AtivoFinanceiro (Arquitetura Corrigida)
 * 
 * Representa um ativo financeiro unificado que pode ser:
 * - Renda Variável (usando TipoAtivoFinanceiroVariavel)
 * - Renda Fixa (usando TipoAtivoFinanceiroFixa)
 * 
 * Utiliza enums tipados para classificação precisa.
 * Dados específicos vêm das operações (Transacao).
 * POJO puro sem dependências externas.
 */
public class AtivoFinanceiro {
    private Long id;
    
    /**
     * Código do ativo (ticker para ações/FIIs, código para RF)
     */
    private String codigo;
    
    /**
     * Nome completo do ativo
     */
    private String nome;
    
    /**
     * Categoria geral do ativo (RENDA_FIXA ou RENDA_VARIAVEL)
     */
    private TipoAtivo tipoAtivo;
    
    /**
     * Tipo específico para renda variável (se aplicável)
     */
    private TipoAtivoFinanceiroVariavel tipoRendaVariavel;
    
    /**
     * Tipo específico para renda fixa (se aplicável)
     */
    private TipoAtivoFinanceiroFixa tipoRendaFixa;
    

    
    /**
     * Referência ao portfolio (objeto completo)
     */
    private Portfolio portfolio;
    
    /**
     * Flag de controle para soft delete
     */
    private Boolean deletado = false;

    public AtivoFinanceiro() {
        this.deletado = false;
    }

    public AtivoFinanceiro(String codigo, String nome, TipoAtivo tipoAtivo, Portfolio portfolio) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipoAtivo = tipoAtivo;
        this.portfolio = portfolio;
        this.deletado = false;
    }

    // Construtor para Renda Variável
    public AtivoFinanceiro(String codigo, String nome, TipoAtivoFinanceiroVariavel tipoRendaVariavel, Portfolio portfolio) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipoAtivo = TipoAtivo.RENDA_VARIAVEL;
        this.tipoRendaVariavel = tipoRendaVariavel;
        this.portfolio = portfolio;
        this.ativo = true;
    }

    // Construtor para Renda Fixa
    public AtivoFinanceiro(String codigo, String nome, TipoAtivoFinanceiroFixa tipoRendaFixa, Portfolio portfolio) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipoAtivo = TipoAtivo.RENDA_FIXA;
        this.tipoRendaFixa = tipoRendaFixa;
        this.portfolio = portfolio;
        this.ativo = true;
    }

    // Métodos de negócio
    
    /**
     * Verifica se é um ativo de renda variável
     */
    public boolean isRendaVariavel() {
        return TipoAtivo.RENDA_VARIAVEL.equals(tipoAtivo);
    }
    
    /**
     * Verifica se é um ativo de renda fixa
     */
    public boolean isRendaFixa() {
        return TipoAtivo.RENDA_FIXA.equals(tipoAtivo);
    }
    

    
    /**
     * Obtém o tipo específico como string (para compatibilidade)
     */
    public String getTipoEspecifico() {
        if (tipoRendaVariavel != null) {
            return tipoRendaVariavel.name();
        }
        if (tipoRendaFixa != null) {
            return tipoRendaFixa.name();
        }
        return null;
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }

    public void setTipoAtivo(TipoAtivo tipoAtivo) {
        this.tipoAtivo = tipoAtivo;
    }

    public TipoAtivoFinanceiroVariavel getTipoRendaVariavel() {
        return tipoRendaVariavel;
    }

    public void setTipoRendaVariavel(TipoAtivoFinanceiroVariavel tipoRendaVariavel) {
        this.tipoRendaVariavel = tipoRendaVariavel;
        if (tipoRendaVariavel != null) {
            this.tipoAtivo = TipoAtivo.RENDA_VARIAVEL;
            this.tipoRendaFixa = null;
        }
    }

    public TipoAtivoFinanceiroFixa getTipoRendaFixa() {
        return tipoRendaFixa;
    }

    public void setTipoRendaFixa(TipoAtivoFinanceiroFixa tipoRendaFixa) {
        this.tipoRendaFixa = tipoRendaFixa;
        if (tipoRendaFixa != null) {
            this.tipoAtivo = TipoAtivo.RENDA_FIXA;
            this.tipoRendaVariavel = null;
        }
    }



    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
}
